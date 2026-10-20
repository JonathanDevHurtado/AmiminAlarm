# Alarmas y notificaciones

Cómo funciona el sistema de alarmas de **AmiminAlarm** por dentro.

---

## Componentes

| Componente | Rol |
|-----------|-----|
| `AlarmTimeCalculator` | Cálculo puro de la próxima ocurrencia según `repeatDays` |
| `AlarmScheduler` | Programa y cancela alarmas, recordatorios y snooze |
| `AlarmReceiver` | Se dispara a la hora de la alarma |
| `AlarmService` | Servicio en primer plano que reproduce el sonido |
| `AlarmRingingActivity` | Pantalla completa sobre el bloqueo |
| `AlarmReminderReceiver` | Avisos previos (1 h / 30 min / 10 min) |
| `EventReminderReceiver` | Avisos de eventos del calendario |
| `AlarmNotificationReceiver` | Acciones Dismiss / Snooze |
| `AlarmRescheduler` | Reagenda todo tras reiniciar, actualizar o cambiar la hora |
| `BootReceiver` | Escucha los broadcasts del sistema y delega en `AlarmRescheduler` |

---

## Programar una alarma

```kotlin
alarmScheduler.schedule(alarm, language)
```

`AlarmScheduler` delega el cálculo en `AlarmTimeCalculator`, que respeta
`repeatDays` (1 = lunes … 7 = domingo), y usa **`AlarmManager`**:

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

El servicio declara `foregroundServiceType="mediaPlayback"` y llama a
`ServiceCompat.startForeground(..., FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)`.
Android 14 exige indicar el tipo también en tiempo de ejecución. La notificación
es **ongoing** y de prioridad máxima; el servicio mantiene un wake lock parcial
mientras suena y lo libera en `onDestroy()`.

### Pantalla completa sobre el bloqueo

La actividad usa:

```kotlin
setShowWhenLocked(true)
setTurnScreenOn(true)
```

El manifiesto declara `showWhenLocked` y `turnScreenOn`. Así la alarma aparece
por encima de la pantalla de bloqueo y enciende la pantalla.

---

## Permisos necesarios

| Permiso | Para qué |
|---------|----------|
| `SCHEDULE_EXACT_ALARM` (Android 12–12L) | Alarmas exactas con permiso especial |
| `USE_EXACT_ALARM` (Android 13+) | Alarmas exactas para apps de despertador |
| `POST_NOTIFICATIONS` | Mostrar notificaciones (Android 13+) |
| `FOREGROUND_SERVICE` + `..._MEDIA_PLAYBACK` | Servicio de sonido |
| `USE_FULL_SCREEN_INTENT` | Pantalla completa sobre bloqueo |
| `RECEIVE_BOOT_COMPLETED` | Reagendar tras reiniciar |
| `VIBRATE` | Vibración |
| `WAKE_LOCK` | Despertar el dispositivo |

> La app pide `POST_NOTIFICATIONS` al abrirse en Android 13+.

---

## Reagendado tras reiniciar

`BootReceiver` escucha `BOOT_COMPLETED`, `MY_PACKAGE_REPLACED`, `TIME_SET`,
`TIMEZONE_CHANGED` y `QUICKBOOT_POWERON`. Al recibirlos delega en
`AlarmRescheduler`:

1. Reprograma las alarmas activas y cancela las desactivadas.
2. Reprograma los recordatorios de eventos futuros y cancela los pasados.

`AmiminApp` ejecuta la misma rutina al abrir la app. Así se recuperan los datos
restaurados por Auto Backup, que recupera la base de datos pero no los
`PendingIntent` de `AlarmManager`.

> No se escucha `LOCKED_BOOT_COMPLETED`: la base de datos Room está en
> almacenamiento cifrado por credenciales y no puede leerse antes del desbloqueo.

Esto garantiza que las alarmas **sobrevivan a un reinicio** o a una
actualización de la app.

---

## Snooze

Al pulsar *Snooze* (5 minutos):

```kotlin
val trigger = System.currentTimeMillis() + 5 * 60_000
setExactAndAllowWhileIdle(RTC_WAKEUP, trigger, pendingIntent)
```

El `requestCode` del snooze es `alarmId + 900` y el intent conserva el tono,
volumen, vibración e idioma originales. La alarma periódica ya se reprogramó al
sonar, así que el snooze no la desactiva.

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
> configuraciones existentes de los usuarios. El canal de alarma tiene el sonido
> desactivado a propósito: el tono lo reproduce `AlarmService` para poder
> controlar volumen, bucle y liberación de recursos.

---

## Limitaciones de fabricantes

Algunos fabricantes (Xiaomi/MIUI, Huawei/EMUI, Samsung One UI, OPPO/ColorOS)
aplican optimizaciones de batería que pueden retrasar o bloquear alarmas exactas.
Si la alarma no suena:

1. Desactiva la optimización de batería para AmiminAlarm.
2. Permite el inicio automático y la actividad en segundo plano.
3. Verifica que las notificaciones y el canal de alarma no estén silenciados.
4. En Android 12–12L, concede el permiso de alarmas exactas en Ajustes.
