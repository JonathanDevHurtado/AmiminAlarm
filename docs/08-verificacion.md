# Verificación — AmiminAlarm 1.1.0

Fecha: **2026-10-20**  
Entorno: Linux, JDK 17 (`17.0.13-tem`), Android SDK con build-tools 36.0.0.

---

## Comandos ejecutados

```bash
./gradlew --no-daemon testDebugUnitTest assembleDebug lintDebug assembleRelease
```

Resultado:

```
BUILD SUCCESSFUL
102 actionable tasks
```

| Tarea | Resultado |
|-------|-----------|
| `testDebugUnitTest` | ✅ 19 tests, 0 fallos, 0 errores |
| `assembleDebug` | ✅ `app-debug.apk` (22 MB) |
| `lintDebug` | ✅ sin errores (128 avisos, en su mayoría dependencias y recursos sin usar) |
| `assembleRelease` | ✅ `app-release.apk` (8,3 MB), R8 + resource shrinking |

Los 19 tests se reparten así:

- `AlarmTimeCalculatorTest`: 10 tests.
- `BackupValidatorTest`: 9 tests.

---

## Firma del release

`apksigner verify --print-certs` confirma un APK firmado:

```
Signer #1 certificate DN: CN=Jonathan Hurtado, OU=AmiminAlarm,
  O=JonathanDevHurtado, L=Bogota, C=CO
```

Las credenciales de firma no están en el repositorio (`keystore.properties` y
`FirmaDelApk/` están ignorados).

---

## Permisos efectivos del APK

Verificados con `aapt2 dump badging`:

```
RECEIVE_BOOT_COMPLETED
VIBRATE
WAKE_LOCK
POST_NOTIFICATIONS
SCHEDULE_EXACT_ALARM (maxSdkVersion=32)
USE_EXACT_ALARM
FOREGROUND_SERVICE
FOREGROUND_SERVICE_MEDIA_PLAYBACK
USE_FULL_SCREEN_INTENT
ACCESS_NETWORK_STATE   ← transitivo de una dependencia; no hay INTERNET
```

No se solicitan `READ_MEDIA_IMAGES`, `READ_MEDIA_VIDEO`, `READ_EXTERNAL_STORAGE`,
`DISABLE_KEYGUARD` ni `TURN_SCREEN_ON`. La app no puede acceder a Internet.

---

## Logo

- `app/src/main/res/drawable-nodpi/amimin_logo.png` es **idéntico** a `logo.png`
  (mismo SHA-256: `ae6486aa…aa37a`).
- El PNG del lanzador (`mipmap-xxxhdpi/ic_launcher.png`) coincide píxel a píxel
  con `logo.png` redimensionado a 192×192 (diferencia media RGB = 0).
- El logo se muestra en:
  - `HomeScreen.kt` (pie de página, 22 dp).
  - `SettingsScreen.kt → AboutSection` (64 dp + nombre, versión, licencia y contacto).
  - `AlarmRingingActivity.kt` (116 dp en la pantalla de alarma).
- El icono adaptativo incluye `<monochrome>` para Android 13+.

El APK empaquetado contiene `res/drawable-nodpi-v4/amimin_logo.png`.

---

## Limitaciones de esta verificación

No había un dispositivo físico ni un AVD conectado (`adb devices` vacío). Por eso
**no se ejecutó una prueba manual de alarma real, reinicio o bloqueo de pantalla**.
La verificación cubre compilación, tests JVM, lint, firma, permisos y empaquetado
del logo. Las pruebas en dispositivo quedan pendientes antes de una publicación
en tienda.
