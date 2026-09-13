package com.rei.aph;

import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseManager {

    private static final String TAG = "FirebaseManager";
    private static final String COLLECTION_NAME = "sesiones_simulacion";
    
    private static FirebaseManager instance;
    private final FirebaseFirestore db;

    // Constructor privado
    private FirebaseManager() {
        // Inicializar la instancia de Firestore
        db = FirebaseFirestore.getInstance();
    }

    // Obtener la única instancia de la clase de manera Thread-Safe
    public static synchronized FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    /**
     * Sube el registro de la simulación a Cloud Firestore
     * @param record El objeto SimulationRecord con las métricas
     */
    public void uploadSimulationData(SimulationRecord record) {
        db.collection(COLLECTION_NAME)
                .add(record)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "✅ Métrica guardada exitosamente. ID del Documento: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Error al guardar las métricas de la simulación", e);
                });
    }
}
