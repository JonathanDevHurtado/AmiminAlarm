# Compilar y ejecutar

Guía paso a paso para compilar **AmiminAlarm** desde el código fuente.

---

## Requisitos

| Herramienta | Versión |
|-------------|---------|
| Android Studio | Hedgehog (2023.1.1) o superior |
| JDK | 17 |
| Android SDK | API 34 |
| Gradle | 8.2 (incluido en el wrapper) |
| Kotlin | 1.9.22 |

---

## 1. Clonar el repositorio

```bash
git clone https://github.com/JonathanDevHurtado/AmiminAlarm.git
cd AmiminAlarm
```

## 2. Configurar el SDK

Crea el archivo `local.properties` en la raíz con la ruta de tu Android SDK:

```properties
sdk.dir=/ruta/a/tu/Android/Sdk
```

Rutas habituales:

- **Linux:** `/home/usuario/Android/Sdk`
- **macOS:** `/Users/usuario/Library/Android/sdk`
- **Windows:** `C:\\Users\\usuario\\AppData\\Local\\Android\\Sdk`

> Si abres el proyecto en Android Studio, este archivo se genera solo.

## 3. Compilar

```bash
# APK de debug
./gradlew assembleDebug

# APK de release (requiere firma)
./gradlew assembleRelease
```

En Windows usa `gradlew.bat` en lugar de `./gradlew`.

El APK se genera en:

```
app/build/outputs/apk/debug/app-debug.apk
```

## 4. Instalar en un dispositivo

```bash
# Con el dispositivo conectado por USB y depuración USB activada
./gradlew installDebug
```

O manualmente con ADB:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## Tareas útiles

| Comando | Descripción |
|---------|-------------|
| `./gradlew assembleDebug` | Compila el APK de debug |
| `./gradlew installDebug` | Compila e instala |
| `./gradlew clean` | Limpia los artefactos de build |
| `./gradlew lint` | Ejecuta el análisis estático |
| `./gradlew test` | Ejecuta los tests unitarios |
| `./gradlew connectedAndroidTest` | Tests instrumentados |

---

## Solución de problemas

### `SDK location not found`

Falta el archivo `local.properties` o la ruta es incorrecta. Revísalo.

### `Unsupported class file major version`

Estás usando un JDK distinto de 17. Configúralo en Android Studio:
**Settings → Build → Build Tools → Gradle → Gradle JDK → 17**.

### `Execution failed for task ':app:kspDebugKotlin'`

Limpia y reconstruye:

```bash
./gradlew clean assembleDebug
```

### El APK no se instala (`INSTALL_FAILED_UPDATE_INCOMPATIBLE`)

Hay una versión previa firmada con otra clave. Desinstala primero:

```bash
adb uninstall com.amimin.app
```

### Compilación lenta la primera vez

Es normal: Gradle descarga dependencias y compila. Las siguientes son mucho más rápidas.

---

## Build de release

Para generar un APK firmado:

1. Crea un keystore:

```bash
keytool -genkey -v -keystore amiminalarm.jks \
  -keyalg RSA -keysize 2048 -validity 10000 -alias amiminalarm
```

2. Añade la configuración de firma en `app/build.gradle.kts` (no subas el keystore
   ni las contraseñas al repositorio).

3. Compila:

```bash
./gradlew assembleRelease
```

> El `.gitignore` ya excluye `*.jks`, `*.keystore` y `keystore.properties`.
