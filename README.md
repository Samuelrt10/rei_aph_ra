# 🩺 REI APH RA - Simulador Toxidrómico APH

![Android](https://img.shields.io/badge/Android-API_23%2B_--_34-brightgreen.svg)
![Language](https://img.shields.io/badge/Language-Java_8-orange.svg)
![Gradle](https://img.shields.io/badge/Gradle-8.12.0-blue.svg)
![Firebase](https://img.shields.io/badge/Firebase-Firestore_%26_Auth-FFCA28.svg?logo=firebase)
![NFC](https://img.shields.io/badge/Hardware-NFC_%26_QR-008080.svg)

**REI APH RA (Simulador Toxidrómico APH)** es una aplicación móvil Android de simulación médica orientada a la **Atención Prehospitalaria (APH)**, medicina de emergencias y toxicología clínica. Permite a docentes, estudiantes, paramédicos y profesionales de la salud recrear y resolver casos clínicos toxicológicos en tiempo real mediante el escaneo de pistas clínicas con **códigos QR** o **etiquetas NFC** colocadas en pacientes o escenarios de entrenamiento.

> 📘 **Informe Técnico y Teórico en LaTeX:** Consulta la formulación matemática completa, las ecuaciones en espacio de estados y la matriz de ponderación clínica en el artículo en código LaTeX: **[📄 Informe_Teorico_Simulador_Toxidromico.tex](docs/Informe_Teorico_Simulador_Toxidromico.tex)**.

---

## 🚀 Novedades y Últimas Actualizaciones

* 📡 **Lectura Dual NFC y Código QR:** Escaneo instantáneo de síntomas tanto por **cámara QR** (Google Code Scanner ML Kit) como por **sensor NFC** (etiquetas NDEF del 0 al 25), permitiendo dinámicas avanzadas de simulación médica.
* 🧬 **Expansión a 26 Signos y Síntomas Clínicos:** Incorporación de **Convulsiones ($u_{24}$)**, **Dolor Torácico / Isquemia ($u_{25}$)** y estandarización de **Agitación ($u_{17}$)** con ponderaciones específicas para cada toxíndrome.
* 🌙 **Modo Oscuro Azul Pizarra (No Negro Puro):** Interfaz médica adaptativa diseñada en elegantes tonos azul marino/pizarra (`#0B132B` / `#1C2541` / `#1E293B`) con botón superior conmutador Sol/Luna (`ic_sun` / `ic_moon`) y persistencia de preferencias.
* 🔐 **Autenticación e Identificación con Google Sign-In:** Integración de Google Play Services Auth para autorellenar y verificar el nombre del evaluador con un solo toque.
* 🎓 **Perfil del Evaluador y Selección de Nivel de Estudios:** Registra la categoría académica/profesional del usuario (Médico Especialista, Residente, Tecnólogo APH, Estudiante APH, Estudiante de Medicina, Interno, Evaluador Internacional) para fines estadísticos e investigación docente.
* ☁️ **Sincronización en la Nube con Cloud Firestore (`FirebaseManager`):** Registro automatizado de cada sesión de simulación en la nube mediante un patrón *Singleton Thread-Safe*, permitiendo la recolección de métricas clínicas centralizadas.
* ⏱️ **Métricas de Rendimiento y Tiempo de Respuesta (`SimulationRecord`):** Captura analítica en tiempo real:
  * Fecha y hora precisa de la simulación (`@ServerTimestamp`).
  * Tiempo exacto de toma de decisión médica (en segundos).
  * Toxidrome real deducido por el algoritmo vs. Toxidrome elegido por el usuario.
  * Tasa de acierto (`true`/`false`).
  * Cantidad total y secuencia cronológica exacta de los síntomas escaneados.
* 📱 **Arquitectura Robusta con AppCompat y ViewBinding:** Compatibilidad con `Theme.AppCompat.DayNight.NoActionBar` y bindings tipados de Jetpack (`MainBinding`, `JuegoBinding`), garantizando total estabilidad sin cierres inesperados.

---

## ✨ Características Principales

* 📷 **Escáner QR Nativo con Google Code Scanner:** API de *Google Play Services ML Kit* con autoenfoque y zoom automático, sin requerir permisos manuales de cámara.
* 📡 **Lectura NFC por Contacto (NDEF):** Detección automática en primer plano (*Foreground Dispatch*) de tarjetas o tags NFC con identificadores numéricos (`0` al `25`).
* 🧬 **26 Signos y Síntomas Clínicos Estandarizados:** Terminología médica rigurosa con emojis representativos.
* ⚖️ **Motor Matemático con Matriz Ponderada ($0, 1, 2, 3$):** Evalúa el cuadro clínico asignando mayor peso a signos patognomónicos (ej. Miosis Puntiforme en Opioide, Clonus en Serotoninérgico, Dolor Torácico en Simpaticomimético).
* 🛡️ **Prevención de Duplicados y Gestión Dinámica:** Bloquea la inclusión de síntomas repetidos y permite eliminar elementos individuales mediante botones de acción rápida.
* 🎯 **Módulo Interactivo de Diagnóstico y Feedback:** Compara el diagnóstico deductivo del usuario con el resultado ponderado del sistema, ofreciendo porcentajes de probabilidad y puntos acumulados.

---

## 🧪 Toxidromes Simulados (6 Cuadros Clínicos)

1. **Hipnótico Sedante:** Depresión del SNC, bradicardia, bradipnea, hipotensión, hipotermia, disminución de ruidos intestinales y coma.
2. **Opioide:** Tríada clásica de miosis puntiforme no reactiva, depresión respiratoria severa, hipotensión y coma.
3. **Simpaticomimético:** Taquicardia, hipertensión, hipertermia, midriasis, diaforesis, agitación, alucinaciones, convulsiones y dolor torácico (isquemia miocárdica).
4. **Serotoninérgico:** Hipertermia severa, clonus/hiperreflexia, hipermotilidad intestinal, diaforesis, midriasis, agitación y convulsiones.
5. **Colinérgico:** Síndrome SLUDGE (Sialorrea, lagrimeo, incontinencia, broncorrea), miosis puntiforme, diaforesis, bradicardia y convulsiones.
6. **Anticolinérgico:** Mnemotecnia clásica (*"Loco como sombrerero, rojo como remolacha, seco como hueso"*): midriasis, piel seca, rubicundez, retención urinaria, alucinaciones, agitación, íleo paralítico y convulsiones.

---

## 📊 Matriz de Síntomas y Códigos QR / NFC (0 a 25)

Para preparar los casos clínicos en las sesiones de entrenamiento, genera los códigos QR o graba etiquetas NFC que contengan el **número entero** del síntoma (del 0 al 25):

> [!TIP]
> 📄 **Documento de Tarjetas Imprimibles:** Puedes descargar directamente el archivo PDF con todas las tarjetas de síntomas listas para imprimir e implementar en simulaciones clínicas: **[📥 Descargar Tarjetas_Simulacion_REI_APH_RA.pdf](https://github.com/Samuelrt10/rei_aph_ra/raw/master/docs/Tarjetas_Simulacion_REI_APH_RA.pdf)** *(o ver en el repositorio: [`docs/Tarjetas_Simulacion_REI_APH_RA.pdf`](./docs/Tarjetas_Simulacion_REI_APH_RA.pdf))*.

| Código QR / NFC | Emoji | Síntoma / Signo Clínico | Categoría / Relevancia |
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
| **12** | 💧👄 | Secreciones / Sialorrea | Exocrino (SLUDGE) |
| **13** | ⬇️🔊 | Ruidos Intestinales Bajos | Gastrointestinal |
| **14** | ⬆️🔊 | Ruidos Intestinales Altos | Gastrointestinal |
| **15** | 😵‍💫🧠 | Delirio / Confusión | Estado Mental |
| **16** | ⚡🦵 | Clonus / Espasmos | Neuromuscular (Clave Serotoninérgico) |
| **17** | 🗣️⚡ | Agitación | Estado Mental |
| **18** | 🔮👁️ | Alucinaciones | Estado Mental |
| **19** | 👁️📍 | Miosis Puntiforme No Reactiva | Pupilas (Patognomónico Opioide) |
| **20** | 🚽🚫 | Retención Urinaria | Genitourinario (Anticolinérgico) |
| **21** | 💤🧠 | Coma / Glasgow Deteriorado | Estado Mental |
| **22** | 🍎🔴 | Rubicundez Cutánea | Piel / Tegumentos (Anticolinérgico) |
| **23** | 🔇🔕 | Ruidos Intestinales Nulos (Íleo) | Gastrointestinal (Íleo Paralítico) |
| **24** | 🧠⚡ | Convulsiones / Espasmos Tónicos | Neuromuscular / SNC |
| **25** | 🫀💥 | Dolor Torácico / Isquemia | Cardíaco (Clave Simpaticomimético) |

---

## 📐 Algoritmo y Motor de Análisis Clínico (`ToxindromeLogic`)

El diagnóstico se calcula evaluando los síntomas escaneados contra la matriz ponderada de asociación $C_{6 \times 26}$:

$$\text{Puntaje Raw}_i = w_{\text{obtenido}, i} \times \left(1.0 + \frac{w_{\text{obtenido}, i}}{w_{\text{total}, i}}\right)$$

Donde:
* $w_{\text{obtenido}, i}$: Suma de los pesos de los síntomas presentes en el toxidrome $i$.
* $w_{\text{total}, i}$: Suma total teórica de pesos de todos los síntomas asignados al toxidrome $i$.
* $\frac{w_{\text{obtenido}, i}}{w_{\text{total}, i}}$: Factor de **coherencia clínica** proporcional del cuadro.

La probabilidad final de cada toxidrome se expresa como un porcentaje relativo sobre el total de puntos acumulados:

$$\text{Probabilidad}_i = \left( \frac{\text{Puntaje Raw}_i}{\sum_{k=1}^6 \text{Puntaje Raw}_k} \right) \times 100\%$$

---

## ☁️ Estructura de Datos en Firebase Cloud Firestore

Los datos guardados en la colección `sesiones_simulacion` contienen la siguiente estructura JSON:

```json
{
  "fechaSimulacion": "2026-09-28T18:30:00.000Z",
  "nombreEvaluador": "Dr. Carlos Mendoza",
  "nivelDeEstudios": "Médico especialista en toxicología clínica",
  "toxidromeReal": "Simpaticomimético",
  "toxidromeElegido": "Simpaticomimético",
  "acierto": true,
  "tiempoDecisionSegundos": 38,
  "cantidadSintomas": 5,
  "ordenSintomas": [1, 5, 9, 24, 25]
}
```

---

## 🛠️ Estructura del Proyecto

```
rei_aph_ra/
├── docs/
│   ├── app-debug.apk                           # Ejecutable APK listo para instalar
│   ├── Tarjetas_Simulacion_REI_APH_RA.pdf      # PDF imprimible con las tarjetas QR para simulaciones
│   └── Informe_Teorico_Simulador_Toxidromico.tex # Artículo teórico completo en formato IEEE LaTeX
├── app/
│   ├── build.gradle                   # Dependencias (AppCompat, Firebase, ML Kit, Google Auth, ViewBinding)
│   ├── google-services.json           # Configuración del proyecto Firebase
│   └── src/main/
│       ├── AndroidManifest.xml        # Declaración de permisos NFC, intenciones y tema
│       ├── java/com/rei/aph/
│       │   ├── MainActivity.java      # Registro de perfil, Google Sign-In, modo oscuro e instrucciones
│       │   ├── JuegoActivity.java     # Escáner QR, sensor NFC, cronómetro y feedback diagnóstico
│       │   ├── ToxindromeLogic.java   # Motor matemático, matriz de pesos C (6x26) y probabilidades
│       │   ├── FirebaseManager.java   # Cliente Singleton para subir métricas a Cloud Firestore
│       │   ├── SimulationRecord.java  # Modelo de datos POJO para las métricas de la simulación
│       │   ├── SketchApplication.java # Inicialización de la aplicación
│       │   ├── SketchwareUtil.java    # Utilidades de mensajes emergentes
│       │   └── FileUtil.java          # Manejo de archivos y almacenamiento
│       └── res/
│           ├── layout/
│           │   ├── main.xml           # UI de Perfil de Evaluador, botón de tema e instrucciones
│           │   ├── juego.xml          # UI de Simulación (Botonera QR/NFC, lista y Empty State)
│           │   └── item_sintoma.xml   # UI de Tarjeta de Síntoma Escaneado
│           ├── drawable/              # Iconos vectoriales (ic_nfc, ic_moon, ic_sun, etc.) y botones
│           ├── mipmap-*/              # Iconos de launcher de la aplicación
│           └── values/
│               ├── colors.xml         # Paleta de colores en Modo Claro
│               ├── strings.xml        # Textos de UI y selector de niveles de estudio
│               └── styles.xml         # Configuración del Tema DayNight de AppCompat
│           └── values-night/
│               └── colors.xml         # Paleta de colores en Modo Oscuro Azul Pizarra (#0B132B)
├── build.gradle                       # Plugins globales y repositorios Gradle
├── gradle.properties                  # Propiedades de compilación
└── README.md
```

---

## 🚀 Descarga e Instalación

### 📲 Instalación Directa (Recomendado)
Puedes descargar la aplicación e instalarla directamente en tu dispositivo Android sin necesidad de compilar el código:
* **[📥 Descargar APK Directo (app-debug.apk)](https://github.com/Samuelrt10/rei_aph_ra/raw/master/docs/app-debug.apk)** *(Última versión actualizada)*.
* O ve a la sección de **[Releases / Lanzamientos](https://github.com/Samuelrt10/rei_aph_ra/releases)** para descargar versiones oficiales publicadas.

**Pasos de Instalación:**
1. Descarga el archivo ejecutable **`app-debug.apk`** en tu teléfono Android.
2. Abre el archivo e instálalo (si el dispositivo lo solicita, otorga permisos para instalar aplicaciones de fuentes desconocidas).

---

### 🛠️ Compilación para Desarrolladores

#### Requisitos
* **Android Studio:** Ladybug (2024.2.1) o superior recomendada.
* **JDK:** Java 8 o Java 11.
* **Android SDK:** `minSdkVersion 23` (Android 6.0) | `targetSdkVersion 34` (Android 14).
* **Google Play Services & NFC:** Requerido en el dispositivo para el escáner ML Kit, Google Sign-In y lectura NFC.

#### Pasos para Compilar
1. Clona el repositorio en tu equipo local:
   ```bash
   git clone https://github.com/Samuelrt10/rei_aph_ra.git
   ```
2. Abre la carpeta del proyecto en **Android Studio**.
3. Asegúrate de contar con el archivo `app/google-services.json` configurado correctamente.
4. Sincroniza el proyecto con los archivos Gradle.
5. Genera el ejecutable mediante Gradle:
   ```bash
   ./gradlew :app:assembleDebug
   ```
6. El archivo APK resultante se ubicará en:
   `app/build/outputs/apk/debug/app-debug.apk`
