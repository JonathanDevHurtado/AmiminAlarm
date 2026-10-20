# Changelog

Todos los cambios importantes de **AmiminAlarm** se documentan en este archivo.

El formato sigue [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/)
y el proyecto se adhiere a [Versionado Semántico](https://semver.org/lang/es/).

---

## [1.1.0] — 2026-10-20

Versión de mantenimiento centrada en fiabilidad de alarmas, seguridad y auditoría.

### Corregido

- **Repetición por días**: `AlarmScheduler` ignoraba `repeatDays` y programaba la
  alarma para el día siguiente. Ahora se calcula la próxima ocurrencia real
  (por ejemplo, un lunes desde un martes salta al lunes siguiente).
- **Alarmas puntuales**: después de sonar se desactivan en la base de datos en
  lugar de quedar encendidas sin una activación pendiente.
- **PendingIntent huérfanos**: una alarma borrada ya no se “resucita” cuando su
  receptor se dispara tarde.
- **Android 14**: el servicio en primer plano declara explícitamente
  `FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK`.
- **Snooze**: conserva tono, volumen, vibración e idioma de la alarma original.
- **Restauración**: se cancelan los `PendingIntent` antiguos, se valida el JSON
  antes de borrar datos y se reprograman las alarmas restauradas.
- **Reinicio / cambio de hora**: `AlarmRescheduler` centraliza la reprogramación
  tras boot, actualización, cambio de hora o zona horaria.
- **Pantalla de bloqueo**: se eliminó `LOCKED_BOOT_COMPLETED`, que no puede leer
  la base de datos cifrada antes del desbloqueo.
- **Permisos**: se retiraron `READ_MEDIA_*`, `READ_EXTERNAL_STORAGE`,
  `DISABLE_KEYGUARD` y `TURN_SCREEN_ON`; los archivos se eligen con SAF.
- **Alarmas exactas**: `SCHEDULE_EXACT_ALARM` queda limitado a Android 12–12L y
  `USE_EXACT_ALARM` se usa desde Android 13.
- **Canal de alarma**: ya no duplica el sonido del servicio.
- **Wake lock**: el servicio mantiene la CPU despierta y lo libera al terminar.

### Añadido

- Logo de AmiminAlarm en Inicio, Acerca de y pantalla de alarma.
- Icono monocromo para temas dinámicos de Android 13+.
- Tests unitarios de cálculo de próxima alarma y validación de copias.
- Documento [`docs/07-dificultades.md`](docs/07-dificultades.md).

### Cambiado

- Publicación bajo **PSAL-1.0** (propietaria, source-available).
- README, guía de contribución y política de seguridad actualizados.
- Versión `1.1.0` (`versionCode 2`).

---

## [1.0.0] — 2026-09-17

Primera versión estable de AmiminAlarm.

### Añadido

#### Alarma
- Alarma a pantalla completa que suena con el teléfono bloqueado.
- Recordatorios anticipados configurables a 1 h, 30 min y 10 min antes.
- Sonido personalizado: sonidos del sistema o audio propio del usuario.
- Stickers anime por alarma (5 categorías, 60 stickers).
- Foto personalizada por alarma.
- Repetición por días de la semana.
- Posponer (snooze) desde la notificación o la pantalla completa.
- Fondo de pantalla de alarma personalizable (sakura, imagen o video).

#### Calendario
- Vista mensual con navegación rápida por meses y años.
- Eventos con repetición semanal, mensual y anual.
- Notificaciones de eventos antes de su inicio.
- Stickers y colores personalizados por evento.
- Foto personalizada por evento.

#### Personalización
- Live wallpaper con 5 modos: degradado, sakura, estrellas, aurora y video propio.
- Control de sonido on/off para el fondo animado.
- 6 temas de color predefinidos.
- Colores personalizados manualmente.
- Extracción automática de colores desde el wallpaper.
- 5 estilos tipográficos: Kawaii, Elegant, Cyberpunk, Minimal y Retro.
- Modo claro y oscuro.

#### Idiomas
- Interfaz completa en 8 idiomas: español, inglés, japonés, coreano, chino,
  ruso, francés y alemán.
- Saludo adaptado al país y a la hora local.

#### Perfil
- Nombre de usuario con saludo personalizado.
- Formato de hora de 12 h o 24 h.

#### Copia de seguridad
- Copia local con archivo `amiminalarm-copia-de-seguridad-AAAA-MM-DD.json`.
- Copia en Google Drive mediante el selector del sistema.
- Restauración completa de alarmas, eventos, ajustes y wallpaper.
- Migraciones de base de datos que preservan los datos del usuario.

#### Notificaciones
- Canales separados para alarma, recordatorios y eventos.
- Notificaciones localizadas en el idioma seleccionado.
- Pantalla completa sobre el bloqueo (full-screen intent).
- Servicio en primer plano para el sonido de la alarma.
- Reagendado automático tras reiniciar el dispositivo.

#### Rendimiento
- Sistema de partículas sincronizado con la tasa de refresco de pantalla.
- Reproducción de video con Media3 ExoPlayer.

---

## [Unreleased]

### Planeado
- Widget de escritorio.
- Integración con Google Calendar.
- Modo tableta / plegable.
- Publicación en F-Droid.

---

<!--
Plantilla para nuevas versiones:

## [X.Y.Z] — AAAA-MM-DD
### Añadido
### Cambiado
### Corregido
### Eliminado
-->
