# Guía de contribución y auditoría

**AmiminAlarm** es un proyecto **propietario source-available**. El código se publica
para que pueda leerse, compilarse y auditarse, pero **no es open source**. Antes de
abrir un issue o proponer un cambio, lee [`LICENSE`](LICENSE).

---

## 🐛 Reportar un error

Las issues con errores reproducibles son bienvenidas. Incluye:

- Versión de AmiminAlarm y de Android.
- Modelo de dispositivo y capa de personalización (MIUI, One UI, EMUI…).
- Pasos exactos para reproducir el problema.
- Comportamiento esperado y observado.
- Capturas, vídeo o `adb logcat` si aportan información.
- Si afecta a alarmas, indica si la app estaba cerrada, en segundo plano o el
  teléfono estaba bloqueado.

No publiques datos personales, rutas locales con tu nombre de usuario ni claves.

---

## 💡 Sugerir una mejora

Abre una issue describiendo:

- El problema de usuario que resuelve.
- Cómo lo imaginas.
- Alternativas que has considerado.

Las sugerencias se revisan, pero no implican compromiso de implementación.

---

## 🔧 Cambios de código

La licencia **no permite modificar ni redistribuir** el código sin un acuerdo
escrito. Por eso:

- **No se aceptan pull requests de código no solicitados.**
- Si quieres proponer un cambio concreto, contacta antes por email:
  **JonathanHurtadoDev@proton.me**.
- Los forks de GitHub para revisión o auditoría están permitidos por la licencia,
  pero no pueden publicarse como producto ni distribuirse.

Si el mantenedor solicita un cambio, se acordará por escrito el alcance y la
titularidad antes de recibir código.

---

## 🔒 Seguridad

No abras una issue pública para vulnerabilidades. Sigue el proceso de
[`SECURITY.md`](SECURITY.md).

---

## 🎨 Estilo del código existente

Aunque no se acepten PRs, estas son las convenciones que usa el proyecto:

| Aspecto | Convención |
|---------|-----------|
| Lenguaje | Kotlin |
| Indentación | 4 espacios |
| Arquitectura | MVVM + repositorios + Hilt |
| Estado | `StateFlow` en ViewModels |
| Textos | `strings.xml`, nunca hardcodeados en la UI |
| Idiomas | 8 locales en `values-*` |
| Alarmas | Toda la lógica de fechas pasa por `AlarmTimeCalculator` |
| Reprogramación | `AlarmRescheduler`, no duplicar lógica en receivers |

---

## ✅ Verificación antes de entregar

```bash
./gradlew testDebugUnitTest
./gradlew assembleDebug
./gradlew lintDebug
```

El proyecto debe compilar sin errores y con los tests en verde antes de cualquier
entrega.

---

## 📄 Licencia de las contribuciones

Cualquier contribución aceptada por escrito se rige por la sección 4 de
[`LICENSE`](LICENSE): el autor conserva la titularidad del proyecto y recibe una
licencia perpetual para usar e integrar el aporte. No se adquiere ningún derecho
sobre el Software.
