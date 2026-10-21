# 📦 Publicar una release — AmiminAlarm

Guía del proceso usado para empaquetar y publicar las versiones oficiales.

---

## 🔢 Versionado

Se usa [SemVer](https://semver.org/lang/es/):

| Cambio | Ejemplo |
|---|---|
| Corrección compatible | `1.2.1` |
| Nueva función compatible | `1.3.0` |
| Cambio incompatible | `2.0.0` |

Cada release incrementa `versionCode` en `app/build.gradle.kts`. El tag de Git
lleva la `v`: `v1.2.0`.

---

## 🔐 Firma

La clave de release **nunca se sube al repositorio**. En este proyecto se guarda
en `FirmaDelApk/`, que está ignorado por Git, y se referencia desde
`keystore.properties` (también ignorado):

```properties
storeFile=FirmaDelApk/amiminalarm-release.jks
storePassword=...
keyAlias=amiminalarm
keyPassword=...
```

Sin `keystore.properties`, `assembleRelease` genera un APK **sin firmar**. No se
publica. Haz una copia de seguridad de la clave y de sus contraseñas fuera del
equipo.

---

## 🛠️ Compilar los artefactos

```bash
export JAVA_HOME=/ruta/al/jdk-17

./gradlew --no-daemon clean testDebugUnitTest lintDebug
./gradlew --no-daemon assembleRelease bundleRelease
```

Salidas:

| Artefacto | Ruta |
|---|---|
| APK firmado | `app/build/outputs/apk/release/app-release.apk` |
| AAB firmado | `app/build/outputs/bundle/release/app-release.aab` |
| Mapping R8 | `app/build/outputs/mapping/release/mapping.txt` |

Verificar la firma:

```bash
apksigner verify --print-certs app/build/outputs/apk/release/app-release.apk
jarsigner -verify -verbose -certs app/build/outputs/bundle/release/app-release.aab
```

---

## 🧾 Nombres y checksums

```text
AmiminAlarm-1.2.0.apk
AmiminAlarm-1.2.0.aab
AmiminAlarm-1.2.0-docs.zip
AmiminAlarm-1.2.0-mapping.txt.gz
SHA256SUMS.txt
```

```bash
sha256sum AmiminAlarm-1.2.0.apk \
          AmiminAlarm-1.2.0.aab \
          AmiminAlarm-1.2.0-docs.zip \
          AmiminAlarm-1.2.0-mapping.txt.gz > SHA256SUMS.txt

# Opcional: descomprimir para leer el mapping (no borra el .gz)
gzip -dk AmiminAlarm-1.2.0-mapping.txt.gz
```

---

## 🚀 Publicar en GitHub

1. Crear el tag y subirlo:
   ```bash
   git tag -a v1.2.0 -m "AmiminAlarm 1.2.0"
   git push origin main --tags
   ```
2. Crear la release con el título `🚀 AmiminAlarm 1.2.0`.
3. Adjuntar APK, AAB, documentación, mapping y checksums.
4. Marcar la release como **Latest**.
5. Comprobar el enlace `releases/latest` del README.

---

## ✅ Checklist

- [ ] `versionCode` y `versionName` actualizados.
- [ ] `CHANGELOG.md` actualizado.
- [ ] Tests y lint sin errores nuevos.
- [ ] APK y AAB firmados y verificados.
- [ ] Checksums generados y comprobados.
- [ ] Documentación incluida en el ZIP.
- [ ] Release publicada con notas y emojis.
- [ ] Clave de firma respaldada fuera del repositorio.
