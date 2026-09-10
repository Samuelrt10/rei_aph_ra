# 🩺 Simulador Toxidrómico APH (Atención Prehospitalaria)

![Android](https://img.shields.bg/badge/Android-34%2B-green.svg)
![Java](https://img.shields.bg/badge/Language-Java_8-orange.svg)
![Gradle](https://img.shields.bg/badge/Gradle-8.x-blue.svg)
![License](https://img.shields.bg/badge/License-MIT-brightgreen.svg)

**Simulador Toxidrómico APH** es una aplicación móvil Android diseñada para personal de atención prehospitalaria, médicos, paramédicos y estudiantes de medicina. Permite simular y deducir diagnósticos toxicológicos de emergencia en tiempo real mediante el escaneo de pistas en códigos QR ubicadas en pacientes o escenarios clínicos.

---

## ✨ Características Principales

* 📷 **Escáner QR Integrado con Google Code Scanner:** Utiliza la API oficial de *Google Play Services ML Kit* con autoenfoque y zoom automático, sin requerir permisos complejos ni aplicaciones externas.
* 🧬 **24 Signos y Síntomas Clínicos:** Incluye terminología médica estandarizada (Bradicardia, Miosis Puntiforme, Diaforesis, Íleo Paralítico, Clonus, etc.) acompañados de emojis representativos.
* ⚖️ **Motor Matemático con Matriz Ponderada ($0, 1, 2, 3$):** Diagnostica mediante ponderación clínica, dando más peso a síntomas patognomónicos (como la Miosis Puntiforme en Opioide o el Clonus en Serotoninérgico).
* 🛡️ **Verificación Anti-Duplicados y Eliminación Dinámica:** Previene agregar dos veces la misma pista y permite borrar síntomas individuales de la lista usando un botón de papelera.
* 🎯 **Módulo Interactivo de Diagnóstico:** Permite al usuario seleccionar su diagnóstico deductivo y recibir retroalimentación del sistema con desglose de probabilidades.
* 🎨 **Interfaz Médica Moderna:** Diseñada con tarjetas neutras, banderas de colores, insignias dinámicas y soporte para pantallas de estado vacío.

---

## 🧪 Toxidromes Simulados (6 Cuadros Clínicos)

1. **Hipnótico Sedante:** Depresión del SNC, bradicardia, bradipnea, hipotensión, hipotermia, disminución de ruidos intestinales.
2. **Opioide:** Tríada clásica de miosis puntiforme no reactiva, depresión respiratoria severa y coma.
3. **Simpaticomimético:** Taquicardia, hipertensión, hipertermia, midriasis, diaforesis, agitación violenta y alucinaciones.
4. **Serotoninérgico:** Hipertermia, clonus/hiperreflexia, hipermotilidad intestinal, diaforesis, midriasis y agitación.
5. **Colinérgico:** Síndrome SLUDGE (Sialorrea, lagrimeo, incontinencia, broncorrea), miosis puntiforme, diaforesis y bradicardia.
6. **Anticolinérgico:** "Loco como sombrerero, rojo como remolacha, seco como hueso" (Midriasis, piel seca, rubicundez, retención urinaria, alucinaciones e íleo paralítico).

---

## 📊 Matriz de Síntomas y Códigos QR (0 a 23)

Para preparar los casos clínicos en las sesiones de entrenamiento, genera códigos QR que contengan únicamente el **número entero** del síntoma (del 0 al 23):

| Código QR | Emoji | Síntoma / Signo Clínico | Categoría / Valor Ponderado |
| :-: | :---: | :--- | :--- |
| **0** | 📉🫀 | FC Baja (Bradicardia) | Frecuencia Cardíaca |
| **1** | 📈🫀 | FC Alta (Taquicardia) | Frecuencia Cardíaca |
| **2** | ⬇️🫁 | FR Baja (Bradipnea) | Frecuencia Respiratoria |
| **3** | ⬆️🫁 | FR Alta (Taquipnea) | Frecuencia Respiratoria |
| **4** | ⬇️🩸 | TA Baja (Hipotensión) | Tensión Arterial |
| **5** | ⬆️🩸 | TA Alta (Hipertensión) | Tensión Arterial |
| **6** | 🥶🌡️ | Temp Baja (Hipotermia) | Temperatura |
| **7** | 🥵🌡️ | Temp Alta (Hipertermia) | Temperatura |
| **8** | 👁️▪️ | Miosis (Pupilas Pequeñas) | Pupilas |
| **9** | 👁️⚪ | Midriasis (Pupilas Dilatadas) | Pupilas |
| **10** | 💦🖐️ | Piel Sudorosa (Diaforesis) | Piel / Tegumentos |
| **11** | 🌵🖐️ | Piel Seca | Piel / Tegumentos |
| **12** | 💧👄 | Secreciones / Sialorrea | Exocrino |
| **13** | ⬇️🔊 | Ruidos Intestinales Bajos | Gastrointestinal |
| **14** | ⬆️🔊 | Ruidos Intestinales Altos | Gastrointestinal |
| **15** | 😵‍💫🧠 | Delirio / Confusión | Estado Mental |
| **16** | ⚡🦵 | Clonus / Espasmos | Neuromuscular |
| **17** | 🗣️⚡ | Agitación Violenta | Estado Mental |
| **18** | 🔮👁️ | Alucinaciones | Estado Mental |
| **19** | 👁️📍 | Miosis Puntiforme No Reactiva | Pupilas (Patognomónico Opioide) |
| **20** | 🚽🚫 | Retención Urinaria | Genitourinario (Anticolinérgico) |
| **21** | 💤🧠 | Coma / Glasgow Deteriorado | Estado Mental |
| **22** | 🍎🔴 | Rubicundez Cutánea | Piel / Tegumentos (Anticolinérgico) |
| **23** | 🔇🔕 | Ruidos Intestinales Nulos (Íleo) | Gastrointestinal (Íleo Paralítico) |

---

## 🛠️ Estructura del Proyecto

```
rei_aph_ra/
├── app/
│   ├── build.gradle                   # Configuración de dependencias y SDK
│   └── src/main/
│       ├── AndroidManifest.xml        # Declaración de componentes y permisos
│       ├── java/com/rei/aph/
│       │   ├── MainActivity.java      # Pantalla de inicio e instrucciones
│       │   ├── JuegoActivity.java     # Pantalla principal del caso y escáner
│       │   ├── ToxindromeLogic.java   # Motor matemático y matriz ponderada
│       │   ├── SketchApplication.java # Clase de inicialización de la app
│       │   └── SketchwareUtil.java    # Utilidades de interfaz
│       └── res/
│           ├── layout/
│           │   ├── main.xml           # UI Pantalla Principal
│           │   ├── juego.xml          # UI Pantalla de Caso Clínico
│           │   └── item_sintoma.xml   # UI Tarjeta de Síntoma individual
│           ├── drawable/              # Iconos vectoriales y fondos redondeados
│           ├── mipmap-*/              # Iconos oficiales de la app
│           └── values/
│               ├── colors.xml         # Paleta médica
│               ├── strings.xml        # Textos de la app
│               └── styles.xml         # Temas de la app
└── README.md
```

---

## 🚀 Compilación e Instalación

### Requisitos
* **Android Studio:** 2023.x o superior.
* **JDK:** Java 8 o superior.
* **Android SDK:** `minSdkVersion 21` (Android 5.0) | `targetSdkVersion 34` (Android 14).

### Pasos para Compilar
1. Clona o descarga el repositorio en tu equipo.
2. Abre la carpeta en **Android Studio**.
3. Ejecuta en la terminal de Gradle:
   ```bash
   ./gradlew :app:assembleDebug
   ```
4. El archivo ejecutable APK se generará en:
   `app/build/outputs/apk/debug/app-debug.apk`

---

## ⚖️ Licencia

Este proyecto está bajo la Licencia **MIT**. Libre uso para fines educativos, académicos y de simulación médica.
