# Guía de contribución

¡Gracias por querer contribuir a **AmiminAlarm**! 🎉

Este documento describe cómo reportar problemas y enviar cambios de forma que
sean fáciles de revisar e integrar.

---

## 📋 Antes de empezar

- Revisa los [issues abiertos](https://github.com/JonathanDevHurtado/AmiminAlarm/issues)
  para no duplicar trabajo.
- Para cambios grandes, **abre primero un issue** para discutir el enfoque.
- Sigue el [Código de Conducta](CODE_OF_CONDUCT.md).

---

## 🐛 Reportar un bug

Abre un issue con la plantilla **Bug report** e incluye:

- Versión de AmiminAlarm y de Android.
- Modelo de dispositivo.
- Pasos para reproducir el problema.
- Comportamiento esperado vs. observado.
- Capturas o vídeo si es posible.
- Logs relevantes (`adb logcat`).

---

## 💡 Sugerir una funcionalidad

Abre un issue con la plantilla **Feature request** e incluye:

- Qué problema resuelve.
- Cómo imaginas la solución.
- Alternativas consideradas.

---

## 🔧 Enviar cambios

### 1. Prepara el entorno

```bash
# Fork del repositorio en GitHub, luego:
git clone https://github.com/TU-USUARIO/AmiminAlarm.git
cd AmiminAlarm
git remote add upstream https://github.com/JonathanDevHurtado/AmiminAlarm.git
```

### 2. Crea una rama

Usa nombres descriptivos con prefijo:

```bash
git checkout -b feature/widget-escritorio
git checkout -b fix/alarma-no-suena
git checkout -b docs/mejorar-readme
```

### 3. Programa tu cambio

- Sigue el estilo de código existente (Kotlin, 4 espacios, sin comentarios innecesarios).
- Mantén la arquitectura **MVVM** y la separación por capas.
- No añadas dependencias sin justificarlo en el PR.

### 4. Verifica

```bash
# Compila
./gradlew assembleDebug

# Lint
./gradlew lint

# Tests (si aplica)
./gradlew test
```

Asegúrate de que **compila sin errores** antes de enviar el PR.

### 5. Haz commit

Usa mensajes claros en imperativo:

```
Añade widget de escritorio para la próxima alarma
Corrige el snooze que no reagendaba la alarma
Actualiza la traducción al japonés
```

### 6. Push y Pull Request

```bash
git push origin feature/widget-escritorio
```

Abre el PR contra la rama `main` y describe:

- Qué cambia.
- Por qué.
- Cómo probarlo.
- Capturas si afecta a la UI.

---

## 🎨 Estilo de código

| Aspecto | Convención |
|---------|-----------|
| Lenguaje | Kotlin |
| Indentación | 4 espacios |
| Nombres de archivo | PascalCase para clases, camelCase para funciones |
| Composables | PascalCase, `@Composable` arriba |
| Estado | `StateFlow` en ViewModels |
| Textos | Siempre en `strings.xml` (nunca hardcodeados) |
| Idiomas | Añade la traducción a los 8 idiomas |

---

## 🌍 Añadir traducciones

1. Edita `app/src/main/res/values-<código>/strings.xml`.
2. Mantén **todas** las claves del archivo base `values/strings.xml`.
3. No traduzcas `app_name` ni nombres propios.

---

## ✅ Lista de verificación del PR

- [ ] El proyecto compila (`./gradlew assembleDebug`).
- [ ] No hay textos hardcodeados.
- [ ] Añadí las traducciones necesarias.
- [ ] Actualicé el `CHANGELOG.md` si aplica.
- [ ] Probé el cambio en un dispositivo o emulador.

---

## 📄 Licencia

Al contribuir aceptas que tu aporte se distribuya bajo la
[Licencia MIT](LICENSE) del proyecto.
