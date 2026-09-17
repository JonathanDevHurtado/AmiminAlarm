# Changelog

Todos los cambios importantes de **AmiminAlarm** se documentan en este archivo.

El formato sigue [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/)
y el proyecto se adhiere a [Versionado Semántico](https://semver.org/lang/es/).

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
