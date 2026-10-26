package com.rei.aph;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.rei.aph.databinding.MainBinding;

public class MainActivity extends AppCompatActivity {
	
	private static final int RC_SIGN_IN = 9001;
	
	private MainBinding binding;
	private GoogleSignInClient mGoogleSignInClient;
	private String nivelEstudiosSeleccionado = "";
	
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
		binding = MainBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		
		initializeGoogleSignIn();
		initializeUI();
	}
	
	private void initializeGoogleSignIn() {
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestIdToken(getString(R.string.default_web_client_id))
				.requestEmail()
				.build();
		
		mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
	}
	
	private void initializeUI() {
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

		String nombreGuardado = prefs.getString("nombre_evaluador", "");
		binding.edtNombreEvaluador.setText(nombreGuardado);

		binding.btnSignInGoogle.setOnClickListener(v -> {
			mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
				Intent signInIntent = mGoogleSignInClient.getSignInIntent();
				startActivityForResult(signInIntent, RC_SIGN_IN);
			});
		});

		binding.spinnerNivelEstudios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position > 0) {
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

		binding.button1.setOnClickListener(v -> {
			String nombre = binding.edtNombreEvaluador.getText().toString().trim();
			
			if (nombre.isEmpty()) {
				Toast.makeText(MainActivity.this, "Por favor, ingresa o sincroniza tu nombre.", Toast.LENGTH_SHORT).show();
				return;
			}
			
			if (nivelEstudiosSeleccionado.isEmpty()) {
				Toast.makeText(MainActivity.this, "Por favor, selecciona tu nivel de estudios.", Toast.LENGTH_SHORT).show();
				return;
			}
			
			prefs.edit()
			     .putString("nombre_evaluador", nombre)
			     .putString("nivel_estudios", nivelEstudiosSeleccionado)
			     .apply();

			Intent i = new Intent(MainActivity.this, JuegoActivity.class);
			startActivity(i);
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RC_SIGN_IN) {
			Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
			try {
				GoogleSignInAccount account = task.getResult(ApiException.class);
				if (account != null && account.getDisplayName() != null) {
					binding.edtNombreEvaluador.setText(account.getDisplayName());
					Toast.makeText(this, "Hola, " + account.getDisplayName(), Toast.LENGTH_SHORT).show();
				}
			} catch (ApiException e) {
				Toast.makeText(this, "Error de Google Sign-In (" + e.getStatusCode() + "). Ingresa tu nombre manualmente.", Toast.LENGTH_LONG).show();
			}
		}
	}
}
