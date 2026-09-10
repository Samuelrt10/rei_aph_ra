package com.rei.aph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

class ToxidromeLogic {

    public static final String[] sintomasEmojis = {
        "📉🫀", // 0: FC Baja
        "📈🫀", // 1: FC Alta
        "⬇️🫁", // 2: FR Baja
        "⬆️🫁", // 3: FR Alta
        "⬇️🩸", // 4: TA Baja
        "⬆️🩸", // 5: TA Alta
        "🥶🌡️", // 6: Temp Baja
        "🥵🌡️", // 7: Temp Alta
        "👁️▪️", // 8: Miosis
        "👁️⚪", // 9: Midriasis
        "💦🖐️", // 10: Piel Sudorosa
        "🌵🖐️", // 11: Piel Seca
        "💧👄", // 12: Secreciones
        "⬇️🔊", // 13: Ruidos Intestinales Bajos
        "⬆️🔊", // 14: Ruidos Intestinales Altos
        "😵‍💫🧠", // 15: Delirio / Confusión
        "⚡🦵", // 16: Clonus / Espasmos
        "🗣️⚡", // 17: Agitación Violenta
        "🔮👁️", // 18: Alucinaciones
        "👁️📍", // 19: Miosis Puntiforme
        "🚽🚫", // 20: Retención Urinaria
        "💤🧠", // 21: Coma / Somnolencia
        "🍎🔴", // 22: Rubicundez Cutánea
        "🔇🔕"  // 23: Ruidos Intestinales Nulos
    };

    public static final String[] sintomasNombres = {
        "FC Baja (Bradicardia)",           // 0
        "FC Alta (Taquicardia)",           // 1
        "FR Baja (Bradipnea)",             // 2
        "FR Alta (Taquipnea)",             // 3
        "TA Baja (Hipotensión)",           // 4
        "TA Alta (Hipertensión)",          // 5
        "Temp Baja (Hipotermia)",          // 6
        "Temp Alta (Hipertermia)",         // 7
        "Miosis (Pupilas Pequeñas)",       // 8
        "Midriasis (Pupilas Dilatadas)",   // 9
        "Piel Sudorosa (Diaforesis)",      // 10
        "Piel Seca",                       // 11
        "Secreciones / Sialorrea",         // 12
        "Ruidos Intestinales Bajos",       // 13
        "Ruidos Intestinales Altos",       // 14
        "Delirio / Confusión",             // 15
        "Clonus / Espasmos",               // 16
        "Agitación Violenta",              // 17
        "Alucinaciones",                   // 18
        "Miosis Puntiforme No Reactiva",   // 19
        "Retención Urinaria",              // 20
        "Coma / Glasgow Deteriorado",      // 21
        "Rubicundez Cutánea",              // 22
        "Ruidos Intestinales Nulos (Íleo)" // 23
    };

    public static final String[] toxindromesNombres = {
        "Hipnótico Sedante",
        "Opioide",
        "Simpaticomimético",
        "Serotoninérgico",
        "Colinérgico",
        "Anticolinérgico"
    };

    /**
     * Matriz de Ponderación (Pesos):
     * 0 = Síntoma no asociado.
     * 1 = Síntoma leve / inespecífico.
     * 2 = Síntoma moderado / característico.
     * 3 = Síntoma clave / patognomónico (Máximo peso).
     */
    public static final int[][] matrizC = {
        // 0: Hipnótico Sedante
        // 0:FCB, 1:FCA, 2:FRB, 3:FRA, 4:TAB, 5:TAA, 6:TempB, 7:TempA, 8:Miosis, 9:Midr, 10:Sudor, 11:Seca, 12:Secr, 13:RuiB, 14:RuiA, 15:Deli, 16:Clon, 17:AgitV, 18:Aluc, 19:MiosP, 20:RetU, 21:Coma, 22:Rubi, 23:RuiN
        {  2,     0,    2,    0,    2,    0,    1,     0,      0,        0,       0,        0,       0,       1,      0,      0,       0,      0,       0,      0,       0,     3,     0,      2    },

        // 1: Opioide
        {  2,     0,    3,    0,    2,    0,    2,     0,      2,        0,       0,        0,       0,       1,      0,      0,       0,      0,       0,      3,       2,     3,     0,      2    },

        // 2: Simpaticomimético
        {  0,     2,    0,    2,    0,    2,    0,     2,      0,        2,       2,        0,       0,       1,      0,      1,       0,      3,       2,      0,       0,     0,     0,      0    },

        // 3: Serotoninérgico
        {  0,     2,    0,    2,    0,    2,    0,     3,      0,        2,       2,        0,       0,       0,      2,      2,       3,      2,       2,      0,       0,     0,     0,      0    },

        // 4: Colinérgico
        {  2,     1,    2,    1,    2,    1,    1,     0,      2,        0,       3,        0,       3,       0,      2,      0,       0,      1,       0,      3,       0,     2,     0,      0    },

        // 5: Anticolinérgico
        {  0,     2,    0,    1,    0,    2,    0,     3,      0,        3,       0,        3,       0,       1,      0,      3,       0,      3,       3,      0,       3,     1,     3,      3    }
    };

    public static class Resultado implements Comparable<Resultado> {
        public String toxindrome;
        public double probabilidad;
        public double puntos;

        public Resultado(String toxindrome, double probabilidad, double puntos) {
            this.toxindrome = toxindrome;
            this.probabilidad = probabilidad;
            this.puntos = puntos;
        }

        @Override
        public int compareTo(Resultado otro) {
            return Double.compare(otro.probabilidad, this.probabilidad);
        }
    }

    public static String analizar(List<Integer> sintomasEscaneados) {
        int totalCartas = sintomasEscaneados.size();
        if (totalCartas < 2) return "Necesitas al menos 2 síntomas para diagnosticar.";

        int[] presente = new int[sintomasNombres.length];
        for (int idx : sintomasEscaneados) {
            if (idx >= 0 && idx < sintomasNombres.length) {
                presente[idx] = 1;
            }
        }

        double[] pesosObtenidos = new double[matrizC.length];
        double[] puntajeRaw = new double[matrizC.length];
        double sumaPuntajesRaw = 0;

        for (int i = 0; i < matrizC.length; i++) {
            double wObtenido = 0;
            double wTotal = 0;

            for (int j = 0; j < sintomasNombres.length; j++) {
                int peso = matrizC[i][j];
                if (peso > 0) {
                    wTotal += peso;
                    if (presente[j] == 1) {
                        wObtenido += peso;
                    }
                }
            }

            pesosObtenidos[i] = wObtenido;

            if (wObtenido > 0) {
                // Coherencia del cuadro clínico = wObtenido / wTotal
                double coherencia = wObtenido / wTotal;
                // Puntaje proporcional considerando tanto el acumulado de puntos como la especificidad
                double raw = wObtenido * (1.0 + coherencia);
                puntajeRaw[i] = raw;
                sumaPuntajesRaw += raw;
            }
        }

        List<Resultado> resultados = new ArrayList<>();
        if (sumaPuntajesRaw > 0) {
            for (int i = 0; i < matrizC.length; i++) {
                if (puntajeRaw[i] > 0) {
                    // Probabilidad relativa respecto al total ponderado acumulado
                    double probabilidad = (puntajeRaw[i] / sumaPuntajesRaw) * 100.0;
                    resultados.add(new Resultado(toxindromesNombres[i], probabilidad, pesosObtenidos[i]));
                }
            }
        }

        Collections.sort(resultados);

        StringBuilder mensaje = new StringBuilder();
        mensaje.append("Análisis Ponderado del Sistema:\n\n");
        for (Resultado res : resultados) {
            mensaje.append("• ").append(res.toxindrome).append(": ")
                   .append(String.format(Locale.getDefault(), "%.1f", res.probabilidad)).append("% ")
                   .append("(").append((int) res.puntos).append(" pts)\n");
        }

        if (resultados.isEmpty()) return "No hay coincidencias toxicológicas claras.";
        return mensaje.toString();
    }
}
