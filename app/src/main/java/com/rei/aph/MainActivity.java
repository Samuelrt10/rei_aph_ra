package com.rei.aph;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.rei.aph.databinding.MainBinding;

public class MainActivity extends Activity {
	
	private static final int RC_SIGN_IN = 9001;
	
	private MainBinding binding;
	private GoogleSignInClient mGoogleSignInClient;
	private String nivelEstudiosSeleccionado = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = MainBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		
		initializeGoogleSignIn();
		initializeUI();
	}
	
	private void initializeGoogleSignIn() {
	    // Configurar solicitud de datos a Google
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestIdToken(getString(R.string.default_web_client_id))
				.requestEmail()
				.build();
		
		mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
	}
	
	private void initializeUI() {

        // Cargar datos previos si existen
		SharedPreferences prefs = getSharedPreferences("APH_PREFS", MODE_PRIVATE);
		String nombreGuardado = prefs.getString("nombre_evaluador", "");
		binding.edtNombreEvaluador.setText(nombreGuardado);

        // Botón Iniciar Sesión con Google
		binding.btnSignInGoogle.setOnClickListener(v -> {
		    // Para evitar el error 12502 (Sign in in progress), cerramos sesión previa primero si existía
			mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
			    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
			    startActivityForResult(signInIntent, RC_SIGN_IN);
			});
		});

        // Configurar selección del nivel de estudios
		binding.spinnerNivelEstudios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position > 0) { // Mayor a 0 para ignorar el "Seleccione..."
					nivelEstudiosSeleccionado = parent.getItemAtPosition(position).toString();
				} else {
					nivelEstudiosSeleccionado = "";
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				nivelEstudiosSeleccionado = "";
			}
		});

        // Botón INICIAR SIMULACIÓN
		binding.button1.setOnClickListener(v -> {
			String nombre = binding.edtNombreEvaluador.getText().toString().trim();
			
			// Validaciones antes de permitir iniciar
			if (nombre.isEmpty()) {
				Toast.makeText(MainActivity.this, "Por favor, ingresa o sincroniza tu nombre.", Toast.LENGTH_SHORT).show();
				return;
			}
			
			if (nivelEstudiosSeleccionado.isEmpty()) {
				Toast.makeText(MainActivity.this, "Por favor, selecciona tu nivel de estudios.", Toast.LENGTH_SHORT).show();
				return;
			}
			
			// Guardar el perfil localmente para usarlo en la siguiente pantalla (JuegoActivity)
			prefs.edit()
			     .putString("nombre_evaluador", nombre)
			     .putString("nivel_estudios", nivelEstudiosSeleccionado)
			     .apply();

            // Lanzar juego
			Intent i = new Intent(MainActivity.this, JuegoActivity.class);
			startActivity(i);
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Resultado de haber intentado iniciar sesión en Google
		if (requestCode == RC_SIGN_IN) {
			Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
			try {
				GoogleSignInAccount account = task.getResult(ApiException.class);
				if (account != null && account.getDisplayName() != null) {
				    // ¡Éxito! Colocamos el nombre de Google en la caja de texto
					binding.edtNombreEvaluador.setText(account.getDisplayName());
					Toast.makeText(this, "Hola, " + account.getDisplayName(), Toast.LENGTH_SHORT).show();
				}
			} catch (ApiException e) {
				Toast.makeText(this, "Error de Google Sign-In (" + e.getStatusCode() + "). Ingresa tu nombre manualmente.", Toast.LENGTH_LONG).show();
			}
		}
	}
}
