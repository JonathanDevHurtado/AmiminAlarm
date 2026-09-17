# Política de Seguridad

## Versiones soportadas

| Versión | Soportada |
|---------|-----------|
| 1.0.x   | ✅        |
| < 1.0   | ❌        |

## Reportar una vulnerabilidad

Si descubres una vulnerabilidad de seguridad en **AmiminAlarm**, por favor **no** la
publiques en un issue público. Contacta al mantenedor directamente por email:

**JonathanHurtadoDev@proton.me**

### Qué incluir en el reporte

- Descripción de la vulnerabilidad.
- Pasos para reproducirla.
- Versión afectada de AmiminAlarm y versión de Android.
- Modelo de dispositivo.
- Posible impacto.
- Sugerencia de solución (si la tienes).

### Qué esperar

- **Confirmación** de recibido dentro de 48 horas.
- **Evaluación** de la gravedad dentro de 1 semana.
- **Fix o mitigación** según la gravedad.
- **Crédito** en el changelog si lo deseas.

## Buenas prácticas del proyecto

AmiminAlarm se toma la seguridad en serio:

- **Sin telemetría**: la app no envía datos a servidores externos.
- **Sin cuentas**: no se recopilan credenciales.
- **Permisos mínimos**: solo los necesarios para alarmas, notificaciones y
  selección de archivos del usuario.
- **Almacenamiento local**: los datos (alarmas, eventos, ajustes, wallpaper)
  permanecen en el dispositivo.
- **URIs persistentes**: se solicitan permisos de lectura persistentes solo
  cuando el usuario elige un archivo propio.
- **Sin secretos en el repositorio**: no se incluyen claves ni tokens.

## Dependencias

Las dependencias se mantienen actualizadas. Si detectas una vulnerabilidad en
una de ellas, avísanos igualmente por el canal privado.
