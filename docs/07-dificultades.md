# Dificultades y decisiones — versión 1.1.0

Esta versión no añade funciones vistosas: arregla problemas que solo aparecen en
un dispositivo real, con la app cerrada, el teléfono bloqueado o una copia de
seguridad restaurada. Estas son las decisiones y los errores que se corrigieron.

---

## 1. La repetición por días se ignoraba

**Síntoma.** Una alarma configurada solo para los lunes sonaba también los martes.

**Causa.** `AlarmScheduler.nextTrigger(hour, minute)` calculaba siempre “hoy o
mañana” y nunca leía `repeatDays`. La UI sí guardaba los días, pero el scheduler
los descartaba.

**Solución.** Se extrajo `AlarmTimeCalculator`, una función pura que:

- traduce la convención de la UI (1 = lunes … 7 = domingo) a
  `Calendar.DAY_OF_WEEK` (1 = domingo … 7 = sábado);
- prueba hoy y los siete días siguientes;
- devuelve la primera coincidencia futura;
- si no hay días válidos, se comporta como alarma puntual.

Al ser pura se puede probar sin emulador. Los tests cubren lunes desde martes,
domingo, varios días, hora ya pasada y datos inválidos.

---

## 2. Alarmas puntuales que quedaban “encendidas” para siempre

**Síntoma.** Una alarma de un solo uso seguía apareciendo activa después de sonar,
aunque no volvía a sonar.

**Causa.** `AlarmReceiver` solo reprogramaba si `repeatDays` no estaba vacío; para
una alarma puntual no hacía nada y la fila seguía con `isEnabled = true`.

**Solución.** Si no hay repetición, el receptor pone `isEnabled = false` y cancela
cualquier `PendingIntent` restante. Así la UI refleja la realidad.

---

## 3. PendingIntent huérfanos que resucitaban alarmas borradas

**Síntoma.** Tras borrar una alarma, podía sonar una versión antigua con su
etiqueta.

**Causa.** Si el broadcast ya estaba en la cola, `getAlarmById()` devolvía `null`
y el código creaba una alarma de respaldo con los extras del intent. Ese respaldo
se programaba otra vez.

**Solución.** Si la alarma ya no existe, el receptor no reprograma nada. También
se cancelan los `PendingIntent` antiguos antes de borrar o restaurar datos.

---

## 4. Android 14 y el tipo de servicio en primer plano

**Síntoma.** En Android 14 el servicio podía lanzar una excepción al arrancar
desde una notificación o desde una alarma.

**Causa.** El manifiesto declaraba `mediaPlayback`, pero la llamada
`startForeground(id, notification)` no indicaba el tipo en ejecución.

**Solución.** Se usa `ServiceCompat.startForeground(...,
FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)` y se mantiene el tipo en el manifiesto.
También se añadió un wake lock parcial con liberación garantizada.

---

## 5. Permisos de alarma exacta en Android 12, 13 y 14

**Problema.** `SCHEDULE_EXACT_ALARM` y `USE_EXACT_ALARM` no significan lo mismo:

- Android 12–12L usa `SCHEDULE_EXACT_ALARM`, que el usuario puede revocar.
- Android 13+ permite `USE_EXACT_ALARM` para apps de despertador, pero Google Play
  revisa que el uso sea legítimo.

**Solución.** Se declara `SCHEDULE_EXACT_ALARM` con `maxSdkVersion="32"` y
`USE_EXACT_ALARM` sin límite. En tiempo de ejecución se comprueba
`canScheduleExactAlarms()` y, si no está concedido, se usa `set()` como
alternativa inexacta en lugar de fallar.

---

## 6. La restauración podía dejar alarmas fantasma

**Síntoma.** Después de restaurar una copia, podían sonar alarmas que ya no
existían en la base de datos, o las restauradas no sonaban.

**Causa.** El proceso borraba filas e insertaba otras con ids nuevos, pero no
cancelaba los `PendingIntent` antiguos ni reprogramaba los nuevos.

**Solución.**

1. Se valida el JSON antes de tocar la base de datos.
2. Se cancelan las alarmas y recordatorios antiguos.
3. Se borran e insertan los datos.
4. `AlarmRescheduler` reprograma todo con los ids nuevos.

La validación comprueba formato, versión, número máximo de elementos, horas,
minutos, días de repetición, volumen, fechas y tamaño del wallpaper. Un archivo
corrupto ya no puede vaciar las alarmas del usuario.

---

## 7. Reinicio, cambio de hora y Auto Backup

**Problema.** `AlarmManager` pierde sus alarmas al reiniciar, al actualizar la app
y al cambiar la hora o la zona horaria. Auto Backup recupera la base de datos, pero
no los `PendingIntent`.

**Solución.** `AlarmRescheduler` centraliza la reconstrucción y se invoca desde:

- `BootReceiver` (`BOOT_COMPLETED`, `MY_PACKAGE_REPLACED`, `TIME_SET`,
  `TIMEZONE_CHANGED`, `QUICKBOOT_POWERON`);
- `AmiminApp.onCreate()`, para recuperar el caso de Auto Backup.

Se eliminó `LOCKED_BOOT_COMPLETED` porque Room vive en almacenamiento cifrado por
credenciales y no se puede leer antes del primer desbloqueo.

---

## 8. Snooze sin contexto

**Síntoma.** Al posponer, la alarma podía sonar con el tono por defecto, sin
vibración o en otro idioma.

**Causa.** El intent de snooze solo llevaba el id y una etiqueta genérica.

**Solución.** Se propagan tono, volumen, vibración e idioma desde la notificación
y desde la pantalla de alarma. El snooze no desactiva la alarma periódica, porque
esa ya se reprogramó al sonar.

---

## 9. Sonido duplicado y wake lock

El canal de notificación de alarma podía reproducir su propio sonido además del
`MediaPlayer`. Ahora el canal tiene sonido desactivado y la vibración la controla
el servicio. El wake lock se adquiere con timeout de 10 minutos y se libera en
`onDestroy()`, incluso si `MediaPlayer` falla.

---

## 10. Pruebas sin emulador

La lógica de fechas y la validación de copias se diseñaron como funciones puras
para poder probarlas en la JVM:

- `AlarmTimeCalculatorTest`: 10 casos de repetición y límites.
- `BackupValidatorTest`: formato, versión, horas, días, volumen, eventos y límites.

Comando de verificación:

```bash
./gradlew testDebugUnitTest assembleDebug lintDebug
```

---

## Decisiones que se mantuvieron

- **Sin telemetría ni cuentas.** La app no tiene permiso de Internet.
- **SAF para archivos.** No se piden permisos globales de almacenamiento; el
  usuario elige imágenes, audios y vídeos con el selector del sistema.
- **Room + DataStore.** Datos estructurados y preferencias separados, con
  migraciones explícitas.
- **`targetSdk 34`.** Se mantiene para no romper Compose 1.5/Hilt 2.50 en esta
  entrega; la migración a 35+ queda en el roadmap.
- **Licencia PSAL-1.0.** El código se publica para auditoría, no para uso
  comercial ni redistribución.
