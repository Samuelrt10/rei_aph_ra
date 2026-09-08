package com.rei.aph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class ToxidromeLogic {

    public static final String[] sintomasEmojis = {
        "📉🫀", "📈🫀", "⬇️🫁", "⬆️🫁", 
        "⬇️🩸", "⬆️🩸", "🥶🌡️", "🥵🌡️", 
        "👁️▪️", "👁️⚪", "💦🖐️", "🌵🖐️", "💧👄", 
        "⬇️🔊", "⬆️🔊", "😵‍💫🧠", "⚡🦵", "🗣️⚡"
    };

    public static final String[] sintomasNombres = {
        "FC Baja (Bradicardia)", "FC Alta (Taquicardia)", 
        "FR Baja (Bradipnea)", "FR Alta (Taquipnea)", 
        "TA Baja (Hipotensión)", "TA Alta (Hipertensión)", 
        "Temp Baja (Hipotermia)", "Temp Alta (Hipertermia)", 
        "Miosis (Pupilas Pequeñas)", "Midriasis (Pupilas Dilatadas)", 
        "Piel Sudorosa (Diaforesis)", "Piel Seca", "Secreciones / Sialorrea", 
        "Ruidos Intestinales Bajos", "Ruidos Intestinales Altos", 
        "Delirio / Confusión", "Clonus / Espasmos", "Agitación Psicomotora"
    };

    public static final String[] toxindromesNombres = {
        "Hipnótico Sedante", "Opioide", "Simpaticomimético", 
        "Serotoninérgico", "Colinérgico", "Anticolinérgico"
    };

    public static final int[][] matrizC = {
        {1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
        {1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0},
        {0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 0, 0, 1, 0, 0, 0, 1},
        {0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 0, 0, 0, 1, 0, 1, 1},
        {1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 1},
        {0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0}
    };

    public static class Resultado implements Comparable<Resultado> {
        public String toxindrome;
        public double probabilidad;

        public Resultado(String toxindrome, double probabilidad) {
            this.toxindrome = toxindrome;
            this.probabilidad = probabilidad;
        }

        @Override
        public int compareTo(Resultado otro) {
            return Double.compare(otro.probabilidad, this.probabilidad);
        }
    }

    public static String analizar(List<Integer> sintomasEscaneados) {
        int totalCartas = sintomasEscaneados.size();
        if (totalCartas < 2) return "Necesitas al menos 2 síntomas para diagnosticar.";

        int[] u = new int[18];
        for (int idx : sintomasEscaneados) {
            if (idx >= 0 && idx < 18) {
                u[idx] = 1;
            }
        }

        List<Resultado> resultados = new ArrayList<>();
        for (int i = 0; i < matrizC.length; i++) {
            int coincidencias = 0;
            for (int j = 0; j < u.length; j++) {
                coincidencias += matrizC[i][j] * u[j];
            }
            double probabilidad = ((double) coincidencias / totalCartas) * 100.0;
            if (probabilidad > 0) {
                resultados.add(new Resultado(toxindromesNombres[i], probabilidad));
            }
        }

        Collections.sort(resultados);
        
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("Resultados:\n\n");
        for (Resultado res : resultados) {
            mensaje.append(res.toxindrome).append(": ").append(String.format("%.1f", res.probabilidad)).append("%\n");
        }
        
        if (resultados.isEmpty()) return "No hay coincidencias claras.";
        return mensaje.toString();
    }
}
