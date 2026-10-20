package com.amimin.app.data.database

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.amimin.app.data.repository.AlarmRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var alarmRepository: AlarmRepository

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra("alarm_id", 0L)
        if (alarmId == 0L) {
            Log.w(TAG, "Alarma sin id; se ignora")
            return
        }

        val isSnooze = intent.getBooleanExtra("snooze", false)
        val language = intent.getStringExtra("language") ?: "en"
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val alarm = alarmRepository.getAlarmById(alarmId)
                if (alarm == null) {
                    // PendingIntent huérfano de una alarma borrada: no suena ni se resucita.
                    Log.i(TAG, "Alarma $alarmId ya no existe; se ignora")
                    return@launch
                }
                if (!isSnooze && !alarm.isEnabled) {
                    alarmScheduler.cancel(alarm)
                    return@launch
                }

                // Se consulta la base de datos antes de arrancar el servicio para que
                // una alarma borrada o desactivada no llegue a sonar.
                val serviceIntent = Intent(context, AlarmService::class.java).apply {
                    putExtra("alarm_id", alarm.id)
                    putExtra("alarm_label", alarm.label)
                    putExtra("alarm_ringtone", alarm.ringtoneUri)
                    putExtra("alarm_vibrate", alarm.vibrate)
                    putExtra("alarm_volume", alarm.volume)
                    putExtra("language", language)
                }
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        ContextCompat.startForegroundService(context, serviceIntent)
                    } else {
                        context.startService(serviceIntent)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "No se pudo iniciar el servicio de alarma", e)
                }

                // Un snooze no desactiva ni reprograma la alarma original: ya se
                // reprogramó cuando sonó por primera vez.
                if (isSnooze) return@launch

                if (alarm.repeatDays.isEmpty()) {
                    alarmRepository.setAlarmEnabled(alarm.id, false)
                    alarmScheduler.cancel(alarm)
                } else {
                    alarmScheduler.schedule(alarm, language)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error al procesar la alarma $alarmId", e)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private companion object {
        const val TAG = "AlarmReceiver"
    }
}
