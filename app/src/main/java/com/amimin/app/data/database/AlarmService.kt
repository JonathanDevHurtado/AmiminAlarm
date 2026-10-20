package com.amimin.app.data.database

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.amimin.app.R
import com.amimin.app.notifications.NotificationHelper
import com.amimin.app.ui.screens.alarm.AlarmRingingActivity

class AlarmService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            stopSelf()
            return START_NOT_STICKY
        }

        val alarmId = intent.getLongExtra("alarm_id", 0L)
        if (alarmId == 0L) {
            Log.w(TAG, "Servicio iniciado sin alarma válida")
            stopSelf()
            return START_NOT_STICKY
        }

        val label = intent.getStringExtra("alarm_label") ?: getString(R.string.app_name)
        val ringtone = intent.getStringExtra("alarm_ringtone")
        val vibrate = intent.getBooleanExtra("alarm_vibrate", true)
        val volume = intent.getIntExtra("alarm_volume", 80).coerceIn(0, 100)
        val language = intent.getStringExtra("language") ?: "en"

        // Si llega una segunda alarma, se libera el reproductor anterior antes de empezar.
        releasePlayer()
        NotificationHelper.ensureChannels(this)
        acquireWakeLock()

        // Android 14 exige declarar el tipo de servicio en primer plano.
        val serviceType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        } else {
            0
        }
        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            buildNotification(alarmId, label, ringtone, vibrate, language),
            serviceType
        )

        playSound(ringtone, volume)
        if (vibrate) startVibration() else stopVibration()

        return START_NOT_STICKY
    }

    private fun buildNotification(
        alarmId: Long,
        label: String,
        ringtone: String?,
        vibrate: Boolean,
        language: String
    ): android.app.Notification {
        val fullScreenIntent = Intent(this, AlarmRingingActivity::class.java).apply {
            putExtra("alarm_id", alarmId)
            putExtra("alarm_label", label)
            putExtra("alarm_ringtone", ringtone)
            putExtra("alarm_vibrate", vibrate)
            putExtra("language", language)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val fullScreenPending = PendingIntent.getActivity(
            this, alarmId.toInt() + 5000, fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val dismissIntent = Intent(this, AlarmNotificationReceiver::class.java).apply {
            action = "DISMISS_ALARM"
            putExtra("alarm_id", alarmId)
        }
        val dismissPending = PendingIntent.getBroadcast(
            this, alarmId.toInt() + 10000, dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val snoozeIntent = Intent(this, AlarmNotificationReceiver::class.java).apply {
            action = "SNOOZE_ALARM"
            putExtra("alarm_id", alarmId)
            putExtra("alarm_label", label)
            putExtra("alarm_ringtone", ringtone)
            putExtra("alarm_vibrate", vibrate)
            putExtra("language", language)
        }
        val snoozePending = PendingIntent.getBroadcast(
            this, alarmId.toInt() + 20000, snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val localized = NotificationHelper.localizedContext(this, language)
        val title = localized.getString(R.string.alarm_ringing)
        val dismiss = localized.getString(R.string.dismiss)
        val snooze = localized.getString(R.string.snooze)

        return NotificationCompat.Builder(this, NotificationHelper.CHANNEL_ALARM)
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle(title)
            .setContentText(label)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOngoing(true)
            .setAutoCancel(false)
            .setFullScreenIntent(fullScreenPending, true)
            .setContentIntent(fullScreenPending)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(R.drawable.ic_alarm, dismiss, dismissPending)
            .addAction(R.drawable.ic_snooze, snooze, snoozePending)
            .build()
    }

    private fun playSound(ringtone: String?, volume: Int) {
        var player: MediaPlayer? = null
        try {
            val uri: Uri = if (!ringtone.isNullOrBlank()) {
                Uri.parse(ringtone)
            } else {
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                    ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            }
            player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                setDataSource(this@AlarmService, uri)
                isLooping = true
                prepare()
                val normalized = volume / 100f
                setVolume(normalized, normalized)
                setOnErrorListener { _, what, extra ->
                    Log.e(TAG, "Error de MediaPlayer: what=$what extra=$extra")
                    true
                }
                start()
            }
            mediaPlayer = player
        } catch (e: Exception) {
            Log.e(TAG, "No se pudo reproducir el tono de alarma", e)
            player?.release()
            mediaPlayer = null
        }
    }

    private fun startVibration() {
        try {
            vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val manager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                manager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            val pattern = longArrayOf(0, 800, 600, 800, 600)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(pattern, 0)
            }
        } catch (e: Exception) {
            Log.e(TAG, "No se pudo iniciar la vibración", e)
        }
    }

    private fun stopVibration() {
        try {
            vibrator?.cancel()
        } catch (e: Exception) {
            Log.w(TAG, "No se pudo detener la vibración", e)
        } finally {
            vibrator = null
        }
    }

    private fun acquireWakeLock() {
        if (wakeLock?.isHeld == true) return
        try {
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "$packageName:AlarmService"
            ).apply {
                setReferenceCounted(false)
                acquire(10 * 60 * 1000L)
            }
        } catch (e: Exception) {
            Log.w(TAG, "No se pudo adquirir el wake lock", e)
        }
    }

    private fun releasePlayer() {
        try {
            mediaPlayer?.stop()
        } catch (e: IllegalStateException) {
            Log.w(TAG, "MediaPlayer ya estaba detenido", e)
        } finally {
            mediaPlayer?.release()
            mediaPlayer = null
        }
        stopVibration()
    }

    override fun onDestroy() {
        releasePlayer()
        try {
            wakeLock?.takeIf { it.isHeld }?.release()
        } catch (e: Exception) {
            Log.w(TAG, "No se pudo liberar el wake lock", e)
        } finally {
            wakeLock = null
        }
        stopForeground(STOP_FOREGROUND_REMOVE)
        super.onDestroy()
    }

    companion object {
        private const val TAG = "AlarmService"
        const val NOTIFICATION_ID = 9001

        fun stop(context: Context) {
            context.stopService(Intent(context, AlarmService::class.java))
        }
    }
}
