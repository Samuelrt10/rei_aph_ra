package com.rei.aph;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;
import com.rei.aph.databinding.JuegoBinding;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class JuegoActivity extends AppCompatActivity {

	private JuegoBinding binding;

	private ArrayList<Double> listaNumeros = new ArrayList<>();

	private SintomaAdapter adapter;
	private GmsBarcodeScanner qrScanner;
	
	// NFC Adapter & PendingIntent
	private NfcAdapter nfcAdapter;
	private PendingIntent nfcPendingIntent;
	
	private long startTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		SharedPreferences prefs = getSharedPreferences("APH_PREFS", MODE_PRIVATE);
		boolean isDarkMode = prefs.getBoolean("modo_oscuro", false);
		if (isDarkMode) {
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
		} else {
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
		}

		super.onCreate(savedInstanceState);
		binding = JuegoBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		// Inicializar escáner QR de Google
		GmsBarcodeScannerOptions options = new GmsBarcodeScannerOptions.Builder()
				.setBarcodeFormats(Barcode.FORMAT_QR_CODE)
				.enableAutoZoom()
				.build();
		qrScanner = GmsBarcodeScanning.getClient(this, options);

		// Inicializar sensor NFC
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		int pendingIntentFlags = PendingIntent.FLAG_MUTABLE;
		Intent nfcIntent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		nfcPendingIntent = PendingIntent.getActivity(this, 0, nfcIntent, pendingIntentFlags);

		startTime = System.currentTimeMillis();

		initialize(savedInstanceState);
		initializeLogic();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (nfcAdapter != null) {
			nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, null, null);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (nfcAdapter != null) {
			nfcAdapter.disableForegroundDispatch(this);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent != null && (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())
				|| NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())
				|| NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction()))) {
			procesarLecturaNFC(intent);
		}
	}

	private void initialize(Bundle savedInstanceState) {
		SharedPreferences prefs = getSharedPreferences("APH_PREFS", MODE_PRIVATE);
		boolean isDarkMode = prefs.getBoolean("modo_oscuro", false);

		if (isDarkMode) {
			binding.btnToggleTheme.setImageResource(R.drawable.ic_sun);
		} else {
			binding.btnToggleTheme.setImageResource(R.drawable.ic_moon);
		}

		binding.btnToggleTheme.setOnClickListener(v -> {
			boolean nuevoModo = !prefs.getBoolean("modo_oscuro", false);
			prefs.edit().putBoolean("modo_oscuro", nuevoModo).apply();
			
			if (nuevoModo) {
				AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
			} else {
				AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
			}
		});

		// Botón Escanear QR
		binding.btnEscanear.setOnClickListener(v -> iniciarEscaneoQR());

		// Botón Escanear NFC
		binding.btnEscanearNFC.setOnClickListener(v -> activarEscaneoNFC());

		// Botón Diagnosticar
		binding.btnDiagnosticar.setOnClickListener(v -> ejecutarDiagnostico());
	}

	private void activarEscaneoNFC() {
		if (nfcAdapter == null) {
			SketchwareUtil.showMessage(getApplicationContext(), getString(R.string.nfc_not_supported));
			return;
		}

		if (!nfcAdapter.isEnabled()) {
			SketchwareUtil.showMessage(getApplicationContext(), getString(R.string.nfc_disabled));
			return;
		}

		new AlertDialog.Builder(JuegoActivity.this)
				.setTitle(getString(R.string.nfc_dialog_title))
				.setMessage(getString(R.string.nfc_dialog_msg))
				.setPositiveButton("Entendido", null)
				.show();
	}

	private void procesarLecturaNFC(Intent intent) {
		String textoPayload = null;

		// Intentar extraer mensaje NDEF
		Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		if (rawMsgs != null && rawMsgs.length > 0) {
			NdefMessage msg = (NdefMessage) rawMsgs[0];
			NdefRecord[] records = msg.getRecords();
			if (records != null && records.length > 0) {
				byte[] payload = records[0].getPayload();
				if (payload != null && payload.length > 0) {
					// NDEF Text Record encoding check (status byte bit 0-5 gives language code length)
					int languageCodeLength = payload[0] & 0x3F;
					try {
						textoPayload = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, StandardCharsets.UTF_8);
					} catch (Exception e) {
						textoPayload = new String(payload, StandardCharsets.UTF_8);
					}
				}
			}
		}

		// Si no se extrajo payload NDEF, intentar leer directamente del Tag
		if (textoPayload == null || textoPayload.trim().isEmpty()) {
			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			if (tag != null) {
				Ndef ndef = Ndef.get(tag);
				if (ndef != null) {
					try {
						ndef.connect();
						NdefMessage message = ndef.getNdefMessage();
						if (message != null && message.getRecords().length > 0) {
							byte[] payload = message.getRecords()[0].getPayload();
							textoPayload = new String(payload, StandardCharsets.UTF_8);
						}
						ndef.close();
					} catch (Exception ignored) {}
				}
			}
		}

		if (textoPayload != null) {
			procesarSintomaNumero(textoPayload.trim(), "NFC");
		} else {
			SketchwareUtil.showMessage(getApplicationContext(), "⚠️ No se pudo leer el contenido de la etiqueta NFC.");
		}
	}

	private void iniciarEscaneoQR() {
		qrScanner.startScan()
				.addOnSuccessListener(barcode -> {
					String rawValue = barcode.getRawValue();
					if (rawValue != null) {
						procesarSintomaNumero(rawValue.trim(), "QR");
					}
				})
				.addOnCanceledListener(() -> {
					SketchwareUtil.showMessage(getApplicationContext(), "Escaneo cancelado");
				})
				.addOnFailureListener(e -> {
					SketchwareUtil.showMessage(getApplicationContext(), "Error al abrir escáner: " + e.getMessage());
				});
	}

	private void procesarSintomaNumero(String valorTexto, String fuente) {
		try {
			int numeroSintoma = Integer.parseInt(valorTexto);
			if (numeroSintoma >= 0 && numeroSintoma < ToxindromeLogic.sintomasNombres.length) {
				double val = (double) numeroSintoma;
				if (listaNumeros.contains(val)) {
					String emoji = ToxindromeLogic.sintomasEmojis[numeroSintoma];
					String nombre = ToxindromeLogic.sintomasNombres[numeroSintoma];
					SketchwareUtil.showMessage(getApplicationContext(),
							"⚠️ " + emoji + " " + nombre + " ya está en la lista.");
				} else {
					listaNumeros.add(val);
					actualizarUI();
					String emoji = ToxindromeLogic.sintomasEmojis[numeroSintoma];
					String nombre = ToxindromeLogic.sintomasNombres[numeroSintoma];
					SketchwareUtil.showMessage(getApplicationContext(),
							"✅ (" + fuente + ") " + emoji + " " + nombre + " agregado.");
				}
			} else {
				SketchwareUtil.showMessage(getApplicationContext(), "Pista no válida para esta simulación (" + numeroSintoma + ")");
			}
		} catch (Exception e) {
			SketchwareUtil.showMessage(getApplicationContext(), "La lectura (" + fuente + ") no contiene un número entero válido (" + valorTexto + ")");
		}
	}

	private void ejecutarDiagnostico() {
		List<Integer> sintomasEnteros = new ArrayList<>();
		for (Double numero : listaNumeros) {
			sintomasEnteros.add(numero.intValue());
		}

		final String reporteReal = ToxindromeLogic.analizar(sintomasEnteros);

		final String[] opciones = {
			"Hipnótico Sedante", "Opioide", "Simpaticomimético", 
			"Serotoninérgico", "Colinérgico", "Anticolinérgico"
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(JuegoActivity.this);
		builder.setTitle("Selecciona tu diagnóstico:");

		builder.setItems(opciones, (dialog, which) -> {
			String eleccion = opciones[which];

			AlertDialog.Builder feedback = new AlertDialog.Builder(JuegoActivity.this);
			
			boolean acierto = reporteReal.contains(eleccion) && reporteReal.indexOf(eleccion) < 45;

			if (acierto) {
				feedback.setTitle("✅ ¡Excelente deducción!");
				feedback.setMessage("Tu diagnóstico (" + eleccion + ") es CORRECTO.\n\n📋 " + reporteReal);
			} else {
				feedback.setTitle("❌ Diagnóstico incorrecto");
				feedback.setMessage("Elegiste " + eleccion + ", pero la clínica apunta a otra cosa.\n\n📋 " + reporteReal);
			}
			
			// Enviar datos a Firestore
			long endTime = System.currentTimeMillis();
			long tiempoDecisionSegundos = (endTime - startTime) / 1000;
			
			SharedPreferences prefs = getSharedPreferences("APH_PREFS", MODE_PRIVATE);
			String nombreEvaluador = prefs.getString("nombre_evaluador", "Anónimo");
			String nivelEstudios = prefs.getString("nivel_estudios", "No especificado");
			
			String toxidromeRealExtraido = "";
			if (reporteReal.contains("•")) {
				int indexPunto = reporteReal.indexOf("•");
				int indexDosPuntos = reporteReal.indexOf(":", indexPunto);
				if (indexPunto != -1 && indexDosPuntos != -1) {
					toxidromeRealExtraido = reporteReal.substring(indexPunto + 2, indexDosPuntos).trim();
				}
			}
			if (toxidromeRealExtraido.isEmpty()) {
				toxidromeRealExtraido = "Desconocido";
			}
			
			SimulationRecord record = new SimulationRecord(
					nombreEvaluador,
					nivelEstudios,
					toxidromeRealExtraido,
					eleccion,
					acierto,
					tiempoDecisionSegundos,
					sintomasEnteros.size(),
					sintomasEnteros
			);
			
			FirebaseManager.getInstance().uploadSimulationData(record);

			feedback.setPositiveButton("Nuevo Caso / Reiniciar", (d, w) -> reiniciarSimulacion());
			feedback.setNegativeButton("Cerrar", null);
			feedback.show();
		});

		builder.setNegativeButton("Aún no sé (Cancelar)", null);
		builder.show();
	}

	private void reiniciarSimulacion() {
		listaNumeros.clear();
		startTime = System.currentTimeMillis();
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
				if (index >= 0 && index < ToxindromeLogic.sintomasNombres.length) {
					txtEmoji.setText(ToxindromeLogic.sintomasEmojis[index]);
					txtNombre.setText(ToxindromeLogic.sintomasNombres[index]);
				}
			}
			txtNumero.setText(getString(R.string.clue_label, position + 1));

			imgEliminar.setOnClickListener(v -> {
				if (position >= 0 && position < listaNumeros.size()) {
					Double eliminado = listaNumeros.remove(position);
					actualizarUI();
					if (eliminado != null) {
						int idx = eliminado.intValue();
						if (idx >= 0 && idx < ToxindromeLogic.sintomasNombres.length) {
							SketchwareUtil.showMessage(getApplicationContext(),
									"🗑️ " + ToxindromeLogic.sintomasNombres[idx] + " eliminado.");
						}
					}
				}
			});

			return view;
		}
	}
}
