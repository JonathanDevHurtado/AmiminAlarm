# Alarmas y notificaciones

Cómo funciona el sistema de alarmas de **AmiminAlarm** por dentro.

---

## Componentes

| Componente | Rol |
|-----------|-----|
| `AlarmScheduler` | Programa y cancela alarmas, recordatorios y snooze |
| `AlarmReceiver` | Se dispara a la hora de la alarma |
| `AlarmService` | Servicio en primer plano que reproduce el sonido |
| `AlarmRingingActivity` | Pantalla completa sobre el bloqueo |
| `AlarmReminderReceiver` | Avisos previos (1 h / 30 min / 10 min) |
| `EventReminderReceiver` | Avisos de eventos del calendario |
| `AlarmNotificationReceiver` | Acciones Dismiss / Snooze |
| `BootReceiver` | Reagenda todo tras reiniciar |

---

## Programar una alarma

```kotlin
alarmScheduler.schedule(alarm, language)
```

`AlarmScheduler` calcula la próxima ocurrencia y usa **`AlarmManager`**:

```kotlin
if (canScheduleExactAlarms()) {
    setExactAndAllowWhileIdle(RTC_WAKEUP, trigger, pendingIntent)
} else {
    set(RTC_WAKEUP, trigger, pendingIntent)   // fallback sin permiso exacto
}
```

- `RTC_WAKEUP` despierta el dispositivo aunque esté dormido.
- `setExactAndAllowWhileIdle` garantiza precisión incluso en *Doze*.

### Recordatorios

Si están activados, se programan tres avisos independientes:

| Aviso | Código de request |
|-------|-------------------|
| 1 hora antes | `alarmId * 10 + 1` |
| 30 min antes | `alarmId * 10 + 2` |
| 10 min antes | `alarmId * 10 + 3` |

Solo se programan si el instante del aviso aún no ha pasado.

---

## Cuando suena la alarma

```
AlarmManager
    │
    ▼
AlarmReceiver.onReceive()
    │  startForegroundService()
    ▼
AlarmService
    ├─ reproduce el sonido en bucle (MediaPlayer + USAGE_ALARM)
    ├─ vibra en patrón
    └─ notificación con fullScreenIntent
              │
              ▼
    AlarmRingingActivity (showWhenLocked + turnScreenOn)
        ├─ [Dismiss]  → detiene el servicio
        └─ [Snooze]   → reprograma en 5 minutos
```

### Servicio en primer plano

El servicio declara `foregroundServiceType="mediaPlayback"` y publica una
notificación **ongoing** con prioridad máxima. Esto es lo que permite que el
sonido continúe aunque el sistema intente matar la app.

### Pantalla completa sobre el bloqueo

La actividad usa:

```kotlin
setShowWhenLocked(true)
setTurnScreenOn(true)
```

Y el tema declara `showOnLockScreen`. Así la alarma aparece por encima de la
pantalla de bloqueo y enciende la pantalla.

---

## Permisos necesarios

| Permiso | Para qué |
|---------|----------|
| `SCHEDULE_EXACT_ALARM` / `USE_EXACT_ALARM` | Alarmas exactas |
| `POST_NOTIFICATIONS` | Mostrar notificaciones (Android 13+) |
| `FOREGROUND_SERVICE` + `..._MEDIA_PLAYBACK` | Servicio de sonido |
| `USE_FULL_SCREEN_INTENT` | Pantalla completa sobre bloqueo |
| `RECEIVE_BOOT_COMPLETED` | Reagendar tras reiniciar |
| `VIBRATE` | Vibración |
| `WAKE_LOCK` | Despertar el dispositivo |

> La app pide `POST_NOTIFICATIONS` al abrirse en Android 13+.

---

## Reagendado tras reiniciar

`BootReceiver` escucha `BOOT_COMPLETED`, `LOCKED_BOOT_COMPLETED`,
`MY_PACKAGE_REPLACED` y `QUICKBOOT_POWERON`. Al recibirlos:

1. Lee todas las alarmas activas y las reprograma.
2. Lee los eventos futuros y reprograma sus recordatorios.

Esto garantiza que las alarmas **sobrevivan a un reinicio** o a una
actualización de la app.

---

## Snooze

Al pulsar *Snooze* (5 minutos):

```kotlin
val trigger = System.currentTimeMillis() + 5 * 60_000
setExactAndAllowWhileIdle(RTC_WAKEUP, trigger, pendingIntent)
```

El `requestCode` del snooze es `alarmId + 900` para no colisionar con la alarma
original.

---

## Notificaciones localizadas

`NotificationHelper.localizedContext(context, language)` crea un `Context`
con el idioma activo, de modo que los textos de la notificación salen en el
idioma que el usuario eligió, independientemente del idioma del sistema.

---

## Canales de notificación

| Canal | Importancia | Uso |
|-------|-------------|-----|
| `amimin_alarm_channel` | HIGH + bypass DND | Alarma sonando |
| `amimin_alarm_reminder_channel` | HIGH | Avisos previos |
| `amimin_event_channel` | DEFAULT | Eventos del calendario |

> Los identificadores de canal se mantienen estables para no romper
> configuraciones existentes de los usuarios.
