# Verificación — AmiminAlarm 1.2.0

Fecha: **2026-10-21**
Entorno: Linux, JDK 17 (`17.0.13-tem`), Android SDK con build-tools 36.0.0.

---

## Comandos ejecutados

```bash
export JAVA_HOME=/ruta/al/jdk-17
./gradlew --no-daemon clean testDebugUnitTest lintDebug assembleRelease bundleRelease
```

Resultado:

```
BUILD SUCCESSFUL in 5m 34s
95 actionable tasks: 94 executed, 1 up-to-date
```

| Tarea | Resultado |
|-------|-----------|
| `testDebugUnitTest` | ✅ 19 tests, 0 fallos, 0 errores |
| `lintDebug` | ✅ sin errores (128 avisos, en su mayoría dependencias y recursos sin usar) |
| `assembleRelease` | ✅ `app-release.apk` (8,2 MB), R8 + resource shrinking |
| `bundleRelease` | ✅ `app-release.aab` (19,7 MB) |

Los 19 tests se reparten así:

- `AlarmTimeCalculatorTest`: 10 tests.
- `BackupValidatorTest`: 9 tests.

---

## Release 1.2.0

| Artefacto | Tamaño | SHA-256 |
|---|---:|---|
| `AmiminAlarm-1.2.0.apk` | 8,2 MB | `b0a71e8d26dd12cd2b387e25600a6310bc2b150acba5cd78bce5e8bdc14c4ffc` |
| `AmiminAlarm-1.2.0.aab` | 19,7 MB | `5f148c1004f52e6e866eb9c6692a173d46484f1370b55f52fee8410d732a8b4a` |

Firma comprobada con `apksigner verify --print-certs`:

```
Signer #1 certificate DN: CN=Jonathan Hurtado, OU=AmiminAlarm,
  O=JonathanDevHurtado, L=Bogota, C=CO
```

El AAB también pasa `jarsigner -verify`. Las credenciales de firma no están en el
repositorio (`keystore.properties` y `FirmaDelApk/` están ignorados). El mapping
de R8 se publica comprimido como `AmiminAlarm-1.2.0-mapping.txt.gz` porque el
archivo sin comprimir supera los 180 MB.

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
