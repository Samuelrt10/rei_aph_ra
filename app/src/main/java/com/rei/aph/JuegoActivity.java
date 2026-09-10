package com.rei.aph;

import android.animation.*;
import android.app.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.net.Uri;
import android.os.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.View;
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;
import com.rei.aph.databinding.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.ArrayList;
import java.util.regex.*;
import org.json.*;

public class JuegoActivity extends Activity {

	private JuegoBinding binding;

	private ArrayList<Double> listaNumeros = new ArrayList<>();

	private SintomaAdapter adapter;
	private GmsBarcodeScanner qrScanner;

	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		binding = JuegoBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		GmsBarcodeScannerOptions options = new GmsBarcodeScannerOptions.Builder()
				.setBarcodeFormats(Barcode.FORMAT_QR_CODE)
				.enableAutoZoom()
				.build();
		qrScanner = GmsBarcodeScanning.getClient(this, options);

		initialize(_savedInstanceState);
		initializeLogic();
	}

	private void initialize(Bundle _savedInstanceState) {

		binding.btnEscanear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View _view) {
				iniciarEscaneoQR();
			}
		});

		binding.btnDiagnosticar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View _view) {
				List<Integer> sintomasEnteros = new ArrayList<>();
				for (Double numero : listaNumeros) {
					sintomasEnteros.add(numero.intValue());
				}

				final String reporteReal = ToxidromeLogic.analizar(sintomasEnteros);

				final String[] opciones = {
					"Hipnótico Sedante", "Opioide", "Simpaticomimético", 
					"Serotoninérgico", "Colinérgico", "Anticolinérgico"
				};

				AlertDialog.Builder builder = new AlertDialog.Builder(JuegoActivity.this);
				builder.setTitle("Selecciona tu diagnóstico:");

				builder.setItems(opciones, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String eleccion = opciones[which];

						AlertDialog.Builder feedback = new AlertDialog.Builder(JuegoActivity.this);

						if (reporteReal.contains(eleccion) && reporteReal.indexOf(eleccion) < 45) {
							feedback.setTitle("✅ ¡Excelente deducción!");
							feedback.setMessage("Tu diagnóstico (" + eleccion + ") es CORRECTO.\n\n📋 " + reporteReal);
						} else {
							feedback.setTitle("❌ Diagnóstico incorrecto");
							feedback.setMessage("Elegiste " + eleccion + ", pero la clínica apunta a otra cosa.\n\n📋 " + reporteReal);
						}

						feedback.setPositiveButton("Nuevo Caso / Reiniciar", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface d, int w) {
								reiniciarSimulacion();
							}
						});
						feedback.setNegativeButton("Cerrar", null);
						feedback.show();
					}
				});

				builder.setNegativeButton("Aún no sé (Cancelar)", null);
				builder.show();
			}
		});
	}

	private void iniciarEscaneoQR() {
		qrScanner.startScan()
				.addOnSuccessListener(barcode -> {
					String rawValue = barcode.getRawValue();
					if (rawValue != null) {
						try {
							int numeroSintoma = Integer.parseInt(rawValue.trim());
							if (numeroSintoma >= 0 && numeroSintoma < ToxidromeLogic.sintomasNombres.length) {
								double val = (double) numeroSintoma;
								if (listaNumeros.contains(val)) {
									String emoji = ToxidromeLogic.sintomasEmojis[numeroSintoma];
									String nombre = ToxidromeLogic.sintomasNombres[numeroSintoma];
									SketchwareUtil.showMessage(getApplicationContext(), 
											"⚠️ " + emoji + " " + nombre + " ya está en la lista.");
								} else {
									listaNumeros.add(val);
									actualizarUI();
									String emoji = ToxidromeLogic.sintomasEmojis[numeroSintoma];
									String nombre = ToxidromeLogic.sintomasNombres[numeroSintoma];
									SketchwareUtil.showMessage(getApplicationContext(), 
											"✅ " + emoji + " " + nombre + " agregado.");
								}
							} else {
								SketchwareUtil.showMessage(getApplicationContext(), "Código QR no válido para esta simulación (" + numeroSintoma + ")");
							}
						} catch (Exception e) {
							SketchwareUtil.showMessage(getApplicationContext(), "El QR no contiene un número válido (" + rawValue + ")");
						}
					}
				})
				.addOnCanceledListener(() -> {
					SketchwareUtil.showMessage(getApplicationContext(), "Escaneo cancelado");
				})
				.addOnFailureListener(e -> {
					SketchwareUtil.showMessage(getApplicationContext(), "Error al abrir escáner: " + e.getMessage());
				});
	}

	private void reiniciarSimulacion() {
		listaNumeros.clear();
		actualizarUI();
		SketchwareUtil.showMessage(getApplicationContext(), "Simulación reiniciada. Puedes escanear un nuevo caso.");
	}

	private void initializeLogic() {
		actualizarUI();
	}

	private void actualizarUI() {
		int cantidad = listaNumeros.size();
		if (cantidad == 1) {
			binding.txtContador.setText(getString(R.string.symptoms_count_singular, cantidad));
		} else {
			binding.txtContador.setText(getString(R.string.symptoms_count_plural, cantidad));
		}

		if (cantidad == 0) {
			binding.linearEmptyState.setVisibility(View.VISIBLE);
			binding.listview1.setVisibility(View.GONE);
		} else {
			binding.linearEmptyState.setVisibility(View.GONE);
			binding.listview1.setVisibility(View.VISIBLE);
		}

		if (adapter == null) {
			adapter = new SintomaAdapter(this, listaNumeros);
			binding.listview1.setAdapter(adapter);
		} else {
			adapter.notifyDataSetChanged();
		}

		binding.btnDiagnosticar.setEnabled(cantidad >= 2);
	}

	private class SintomaAdapter extends ArrayAdapter<Double> {
		public SintomaAdapter(Context context, List<Double> objetos) {
			super(context, 0, objetos);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				view = getLayoutInflater().inflate(R.layout.item_sintoma, parent, false);
			}

			TextView txtEmoji = view.findViewById(R.id.txtEmojiSintoma);
			TextView txtNombre = view.findViewById(R.id.txtNombreSintoma);
			TextView txtNumero = view.findViewById(R.id.txtNumeroSintoma);
			ImageView imgEliminar = view.findViewById(R.id.imgEliminarSintoma);

			Double num = getItem(position);
			if (num != null) {
				int index = num.intValue();
				if (index >= 0 && index < ToxidromeLogic.sintomasNombres.length) {
					txtEmoji.setText(ToxidromeLogic.sintomasEmojis[index]);
					txtNombre.setText(ToxidromeLogic.sintomasNombres[index]);
				}
			}
			txtNumero.setText(getString(R.string.clue_label, position + 1));

			imgEliminar.setOnClickListener(v -> {
				if (position >= 0 && position < listaNumeros.size()) {
					Double eliminado = listaNumeros.remove(position);
					actualizarUI();
					if (eliminado != null) {
						int idx = eliminado.intValue();
						if (idx >= 0 && idx < ToxidromeLogic.sintomasNombres.length) {
							SketchwareUtil.showMessage(getApplicationContext(),
									"🗑️ " + ToxidromeLogic.sintomasNombres[idx] + " eliminado.");
						}
					}
				}
			});

			return view;
		}
	}
}
