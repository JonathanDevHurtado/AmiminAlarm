package com.amimin.app.data.backup

/**
 * Valida una copia de seguridad antes de tocar la base de datos.
 *
 * Un JSON corrupto o manipulado no debe borrar las alarmas actuales del usuario.
 */
object BackupValidator {

    const val CURRENT_VERSION = 1
    const val MAX_ALARMS = 500
    const val MAX_EVENTS = 1_000
    const val MAX_JSON_BYTES = 30 * 1024 * 1024
    const val MAX_WALLPAPER_BASE64_LENGTH = 25_000_000

    @Suppress("SENSELESS_COMPARISON")
    fun validate(backup: AmiminBackup): String? {
        if (backup.format != "amimin-backup") {
            return "El archivo no es una copia de Amimin"
        }
        if (backup.version !in 1..CURRENT_VERSION) {
            return "Versión de copia no compatible: ${backup.version}"
        }
        if (backup.alarms == null || backup.events == null || backup.settings == null) {
            return "La copia está incompleta"
        }
        if (backup.alarms.size > MAX_ALARMS) {
            return "La copia contiene demasiadas alarmas"
        }
        if (backup.events.size > MAX_EVENTS) {
            return "La copia contiene demasiados eventos"
        }

        backup.alarms.forEachIndexed { index, alarm ->
            if (alarm.hour !in 0..23 || alarm.minute !in 0..59) {
                return "Hora inválida en la alarma ${index + 1}"
            }
            if (alarm.label.length > 200) {
                return "Etiqueta demasiado larga en la alarma ${index + 1}"
            }
            if (alarm.repeatDays.any { it !in 1..7 }) {
                return "Día de repetición inválido en la alarma ${index + 1}"
            }
            if (alarm.volume !in 0..100) {
                return "Volumen inválido en la alarma ${index + 1}"
            }
        }

        backup.events.forEachIndexed { index, event ->
            if (event.title.isBlank() || event.title.length > 200) {
                return "Título inválido en el evento ${index + 1}"
            }
            if (event.description.length > 5_000) {
                return "Descripción demasiado larga en el evento ${index + 1}"
            }
            if (event.startTime <= 0L || event.endTime < event.startTime) {
                return "Fechas inválidas en el evento ${index + 1}"
            }
            if (event.reminderMinutes !in 0..10_080) {
                return "Recordatorio inválido en el evento ${index + 1}"
            }
        }

        val wallpaperLength = backup.wallpaperBase64?.length ?: 0
        if (wallpaperLength > MAX_WALLPAPER_BASE64_LENGTH) {
            return "El fondo de pantalla de la copia es demasiado grande"
        }

        return null
    }
}
