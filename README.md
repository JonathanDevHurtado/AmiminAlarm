<p align="center">
  <img src="banner.png" alt="AmiminAlarm — Anime Alarm & Calendar for Android"/>
</p>

<p align="center">
  <strong>Despertador y calendario con estilo anime: fondos animados, stickers, temas dinámicos y notificaciones que funcionan aunque la app esté cerrada.</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Versión-1.1.0-FF6B9D?style=for-the-badge" alt="Versión 1.1.0"/>
  <img src="https://img.shields.io/badge/Android-8.0%2B%20(API%2026)-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android 8.0+"/>
  <img src="https://img.shields.io/badge/Kotlin-1.9-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin 1.9"/>
  <img src="https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose"/>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/License-PSAL--1.0-FFD93D?style=flat-square" alt="PSAL-1.0"/>
  <img src="https://img.shields.io/badge/Issues-welcome-FF6B9D?style=flat-square" alt="Issues Welcome"/>
  <img src="https://img.shields.io/badge/Idiomas-8-9C27B0?style=flat-square" alt="8 idiomas"/>
  <img src="https://img.shields.io/badge/Source--Available-00BCD4?style=flat-square" alt="Source-available"/>
</p>

---

## 📖 Descripción

**AmiminAlarm** es una aplicación Android nativa de **alarma y calendario** con una estética
anime cuidada al detalle. No es un despertador más: es una experiencia visual que se
adapta a ti, con **fondos animados**, **stickers**, **temas que se generan a partir de
tus imágenes** y notificaciones que **funcionan incluso con la app cerrada o el teléfono
bloqueado**.

Todo el proyecto está construido con **Kotlin + Jetpack Compose**, sin publicidad,
sin cuentas y con **código auditable** bajo una licencia propietaria source-available.

---

## ✨ Características

### ⏰ Alarma
- **Alarma a pantalla completa** que suena aunque el teléfono esté bloqueado.
- **Recordatorios anticipados** configurables a **1 hora, 30 minutos y 10 minutos** antes.
- **Sonido personalizado**: elige entre los sonidos del sistema o **tu propio audio**.
- **Stickers anime** para cada alarma (5 categorías, 60 stickers).
- **Foto personalizada** por alarma.
- **Repetición por días** de la semana.
- **Posponer (snooze)** desde la notificación o la pantalla completa.
- **Fondo de pantalla de alarma personalizable**: animación sakura, tu imagen o tu video.

### 📅 Calendario
- Vista mensual con navegación **rápida por meses y años**.
- **Eventos con repetición**: semanal, mensual y anual.
- **Notificaciones de eventos** antes de que empiecen.
- **Stickers anime** y **colores personalizados** por evento.
- **Foto personalizada** por evento.

### 🎨 Personalización
- **Fondo de pantalla animado (Live Wallpaper)** con 5 modos:
  - **Degradado** — color que fluye suavemente
  - **Sakura** — pétalos de cerezo cayendo
  - **Estrellas** — cielo titilante
  - **Aurora** — ondas de aurora boreal
  - **Video** — **tu propio video** como fondo animado
- **Sonido on/off** para el fondo animado.
- **Temas de color**: 6 paletas predefinidas + colores personalizados + **colores
  extraídos automáticamente de tu wallpaper**.
- **5 estilos tipográficos anime**: Kawaii, Elegant, Cyberpunk, Minimal y Retro.
- **Modo claro y oscuro**.

### 🌍 Idiomas
Interfaz completa en **8 idiomas**, con **saludo según el país y la hora**:

| | | | |
|---|---|---|---|
| 🇪🇸 Español | 🇬🇧 English | 🇯🇵 日本語 | 🇰🇷 한국어 |
| 🇨🇳 中文 | 🇷🇺 Русский | 🇫🇷 Français | 🇩🇪 Deutsch |

> El saludo detecta el país: en **España** los tramos horarios son distintos
> (buenos días hasta las 14:00) que en **Latinoamérica** (buenos días hasta las 12:00).

### 👤 Perfil
- **Nombre de usuario** con el que la app te saluda cada vez que entras.
- **Formato de hora** configurable: **12 h (1:00 PM)** o **24 h (13:00)**.

### 💾 Copia de seguridad
- **Copia local**: genera el archivo `amiminalarm-copia-de-seguridad-AAAA-MM-DD.json`
  y tú eliges dónde guardarlo.
- **Copia en Google Drive** vía el selector del sistema.
- **Restaurar** alarmas, eventos, ajustes y wallpaper en cualquier teléfono.
- **Migraciones de base de datos** que **preservan tus datos** en cada actualización.

### 🔔 Notificaciones
- Canales separados para **alarma**, **recordatorios** y **eventos**.
- **Localizadas** en el idioma que hayas elegido.
- **Pantalla completa** sobre el bloqueo (full-screen intent).
- **Servicio en primer plano** para que el sonido no se corte.
- **Reagendado automático** tras reiniciar, actualizar, cambiar la hora/zona horaria o restaurar una copia.

---

## ⚠️ Estado y limitaciones

- Proyecto personal, mantenido por un solo desarrollador. La versión estable actual es **1.1.0**.
- `targetSdk` es **34**. La app funciona en Android 8.0–15, pero una futura publicación en Google Play requerirá subir el target.
- La copia de seguridad es **local**: el selector del sistema permite guardarla en Drive u otro proveedor, pero la app no se conecta a ninguna nube.
- No hay cuentas, sincronización, telemetría ni publicidad.
- El código es **propietario y source-available**. Se publica para auditoría; no se aceptan PRs de código no solicitados. Consulta [`LICENSE`](LICENSE).
- Las notificaciones de alarma pueden verse afectadas por optimizaciones agresivas de batería de algunos fabricantes (Xiaomi, Huawei, Samsung). Está documentado en [`docs/05-alarmas.md`](docs/05-alarmas.md).

---

## 📱 Capturas

<p align="center">
  <img src="screenshots/1-home.png" width="200" alt="Inicio"/>
  &nbsp;
  <img src="screenshots/2-alarm.png" width="200" alt="Alarmas"/>
  &nbsp;
  <img src="screenshots/3-calendar.png" width="200" alt="Calendario"/>
</p>
<p align="center">
  <img src="screenshots/5-alarm-dialog.png" width="200" alt="Configurar alarma"/>
  &nbsp;
  <img src="screenshots/4-event-dialog.png" width="200" alt="Nuevo evento"/>
  &nbsp;
  <img src="screenshots/6-settings.png" width="200" alt="Configuración"/>
</p>

<p align="center">
  <em>Wallpaper anime con colores extraídos automáticamente · stickers · fondos animados</em>
</p>

---

## ⬇️ Descarga

La forma más fácil de probar AmiminAlarm es descargar el APK firmado:

<p align="center">
  <a href="https://github.com/JonathanDevHurtado/AmiminAlarm/releases/latest">
    <img src="https://img.shields.io/badge/⬇️_Descargar_APK-v1.1.0-FF6B9D?style=for-the-badge" alt="Descargar APK"/>
  </a>
</p>

> **Requiere Android 8.0 (API 26) o superior.**
> Al instalar, activa «Instalar apps de origen desconocido» para tu navegador
> o gestor de archivos.

Si prefieres compilarlo tú mismo, consulta la sección
[Compilar y ejecutar](#-compilar-y-ejecutar).

---

## 🛠️ Tecnologías

| Capa | Tecnología |
|------|-----------|
| **Lenguaje** | Kotlin 1.9 |
| **UI** | Jetpack Compose · Material 3 |
| **Arquitectura** | MVVM + Clean Architecture (data / ui) |
| **Inyección de dependencias** | Hilt (Dagger) |
| **Persistencia** | Room (SQLite) · DataStore (Preferences) |
| **Navegación** | Navigation Compose |
| **Asincronía** | Coroutines · Flow |
| **Imágenes** | Coil · AndroidX Palette |
| **Video** | Media3 ExoPlayer |
| **Serialización** | Gson |
| **Build** | Gradle (Kotlin DSL) · AGP 8.2 |
| **Min SDK** | 26 (Android 8.0) |
| **Target SDK** | 34 (Android 14) |

---

## 🏗️ Arquitectura

AmiminAlarm sigue una arquitectura **MVVM** con separación clara por capas:

```
┌──────────────────────────────────────────────────────────┐
│                        UI (Compose)                       │
│   Screens  ·  ViewModels  ·  Theme  ·  Animations         │
└───────────────────────────┬──────────────────────────────┘
                            │ StateFlow / Flow
┌───────────────────────────▼──────────────────────────────┐
│                     Repositories                          │
│   AlarmRepository · CalendarRepository · SettingsRepository│
│                     BackupManager                         │
└───────────────────────────┬──────────────────────────────┘
                            │
┌───────────────────────────▼──────────────────────────────┐
│                        Data                               │
│   Room (Alarms, Events)  ·  DataStore (Ajustes)           │
│   AlarmScheduler  ·  Receivers  ·  Foreground Service     │
└──────────────────────────────────────────────────────────┘
```

**Puntos clave del diseño:**

- **Estado unidireccional**: los `ViewModel` exponen `StateFlow`, la UI solo observa.
- **AlarmScheduler** centraliza la programación con `AlarmManager` (exacta y con
  `setExactAndAllowWhileIdle`), los recordatorios y el snooze.
- **Foreground Service** (`AlarmService`) reproduce el sonido y muestra la notificación
  a pantalla completa, garantizando que suene en segundo plano y en bloqueo.
- **Migraciones incrementales** de Room que nunca destruyen datos del usuario.
- **Temas dinámicos**: los colores se derivan de la fuente activa (tema, personalizado,
  wallpaper o live wallpaper) y se propagan por `MaterialTheme`.

---

## 📂 Estructura del proyecto

```
AmiminAlarm/
├── app/
│   ├── src/main/
│   │   ├── java/com/amimin/app/
│   │   │   ├── MainActivity.kt              # Entrada + tema global
│   │   │   ├── AmiminApp.kt                 # Application + canales de notificación
│   │   │   ├── data/
│   │   │   │   ├── backup/BackupManager.kt  # Exportar / importar copia
│   │   │   │   ├── database/                # Room, DAOs, receivers, service
│   │   │   │   │   ├── AmiminDatabase.kt
│   │   │   │   │   ├── DatabaseMigrations.kt
│   │   │   │   │   ├── AlarmScheduler.kt
│   │   │   │   │   ├── AlarmService.kt
│   │   │   │   │   ├── AlarmReceiver.kt
│   │   │   │   │   ├── AlarmReminderReceiver.kt
│   │   │   │   │   ├── EventReminderReceiver.kt
│   │   │   │   │   ├── BootReceiver.kt
│   │   │   │   │   └── DAOs.kt
│   │   │   │   ├── model/Models.kt
│   │   │   │   └── repository/              # Repositorios + SettingsRepository
│   │   │   ├── di/DatabaseModule.kt         # Hilt
│   │   │   ├── notifications/               # Notificaciones localizadas
│   │   │   └── ui/
│   │   │       ├── animations/              # Partículas, live wallpaper, video
│   │   │       ├── components/              # StickerPicker, SoundEffects
│   │   │       ├── navigation/
│   │   │       ├── screens/
│   │   │       │   ├── home/                # Reloj, saludo, tarjetas
│   │   │       │   ├── alarm/               # Alarmas + pantalla de timbre
│   │   │       │   ├── calendar/            # Calendario + eventos
│   │   │       │   └── settings/            # Configuración
│   │   │       └── theme/                   # Color, Type, Shape, Theme
│   │   └── res/
│   │       ├── values/                      # Español (base)
│   │       ├── values-en/ ... values-zh/    # 8 idiomas
│   │       └── xml/                         # FileProvider, backup rules
│   └── build.gradle.kts
├── gradle/
├── docs/                                    # Documentación detallada
├── screenshots/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew / gradlew.bat
├── README.md
├── LICENSE
├── CHANGELOG.md
├── CONTRIBUTING.md
├── CODE_OF_CONDUCT.md
└── SECURITY.md
```

---

## 🚀 Compilar y ejecutar

### Requisitos
- **Android Studio** Hedgehog (2023.1.1) o superior
- **JDK 17**
- **Android SDK 34**

### Pasos

```bash
# 1. Clona el repositorio
git clone https://github.com/JonathanDevHurtado/AmiminAlarm.git
cd AmiminAlarm

# 2. Configura el SDK (local.properties)
echo "sdk.dir=/ruta/a/tu/Android/Sdk" > local.properties

# 3. Compila el APK de debug
./gradlew assembleDebug

# 4. (Opcional) Instálalo en tu dispositivo conectado
./gradlew installDebug
```

El APK se genera en:
```
app/build/outputs/apk/debug/app-debug.apk
```

> Para una guía detallada consulta [`docs/04-compilar.md`](docs/04-compilar.md).

---

## 🧭 Documentación

| Documento | Contenido |
|-----------|-----------|
| [Arquitectura](docs/01-arquitectura.md) | Capas, flujo de datos y decisiones de diseño |
| [Estructura de archivos](docs/02-estructura.md) | Descripción de cada paquete y archivo |
| [Funcionalidades](docs/03-funcionalidades.md) | Detalle de cada característica |
| [Compilar](docs/04-compilar.md) | Guía de build paso a paso |
| [Alarmas y notificaciones](docs/05-alarmas.md) | Cómo funcionan AlarmManager y el servicio |
| [Temas y personalización](docs/06-temas.md) | Sistema de temas, wallpaper y colores |
| [Dificultades y decisiones](docs/07-dificultades.md) | Problemas reales encontrados y cómo se resolvieron |
| [Verificación](docs/08-verificacion.md) | Comandos, tests, lint, firma y limitaciones de la prueba |

---

## 🗺️ Roadmap

- [x] Alarma con pantalla completa y snooze
- [x] Calendario con eventos y repetición
- [x] Live wallpaper (degradado, sakura, estrellas, aurora, video)
- [x] Temas dinámicos desde wallpaper
- [x] 8 idiomas con saludo por país
- [x] Copia de seguridad local (guardable en Drive u otro proveedor)
- [ ] Widget de escritorio
- [ ] Integración con Google Calendar
- [ ] Modo tableta / plegable
- [ ] Migración a `targetSdk` 35+

---

## 🤝 Contribuir

Este es un proyecto **propietario source-available**, no un proyecto open source.
Las **issues** con errores, reproducciones y propuestas son bienvenidas; los cambios
de código requieren un acuerdo previo por escrito. Consulta [`CONTRIBUTING.md`](CONTRIBUTING.md).

---

## 🔒 Seguridad

Si encuentras una vulnerabilidad, **no abras un issue público**. Consulta
[`SECURITY.md`](SECURITY.md) para el proceso de reporte responsable.

---

## 🧩 Dificultades técnicas de la versión 1.1.0

La versión 1.1.0 corrigió problemas que no se ven en una demo pero sí en un
dispositivo real: la repetición por días se ignoraba, las alarmas puntuales
quedaban activas tras sonar, los `PendingIntent` huérfanos podían resucitar
alarmas borradas, Android 14 exigía el tipo de servicio en primer plano y una
restauración incompleta podía dejar alarmas fantasma. El detalle está en
[`docs/07-dificultades.md`](docs/07-dificultades.md).

---

## 📄 Licencia

Este proyecto está bajo la **Proprietary Source-Available License 1.0 (PSAL-1.0)**.
**No es una licencia open source** ni está aprobada por la OSI. El código se publica
para que pueda leerse, compilarse y auditarse; no se permite su uso en producción,
modificación ni redistribución sin un acuerdo escrito. Ver [`LICENSE`](LICENSE).

Para licencias comerciales: **JonathanHurtadoDev@proton.me**.

```
Copyright (c) 2026 Jonathan Hurtado (JonathanHurtado). All rights reserved.
```

---

## 👤 Autor

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/JonathanDevHurtado">
        <img src="https://avatars.githubusercontent.com/JonathanDevHurtado" width="100" alt="Jonathan Hurtado"/><br/>
        <b>Jonathan Hurtado</b>
      </a><br/>
      <sub>Desarrollador Android</sub>
    </td>
  </tr>
</table>

- **GitHub:** [@JonathanDevHurtado](https://github.com/JonathanDevHurtado)
- **Email:** [JonathanHurtadoDev@proton.me](mailto:JonathanHurtadoDev@proton.me)

---

<p align="center">
  <img src="logo.png" width="72" alt="AmiminAlarm"/>
</p>

<p align="center">
  Hecho con ❤️, Kotlin y Jetpack Compose
</p>
