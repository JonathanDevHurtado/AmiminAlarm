package com.amimin.app.data.database

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.amimin.app.MainActivity
import com.amimin.app.R
import com.amimin.app.notifications.NotificationHelper
import com.amimin.app.ui.screens.alarm.AlarmRingingActivity

class AlarmService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val alarmId = intent?.getLongExtra("alarm_id", 0L) ?: 0L
        val label = intent?.getStringExtra("alarm_label") ?: "Anime Alarm"
        val ringtone = intent?.getStringExtra("alarm_ringtone")
        val language = intent?.getStringExtra("language") ?: "en"

        startForeground(NOTIFICATION_ID, buildNotification(alarmId, label, language))
        playSound(ringtone)
        startVibration()

        return START_STICKY
    }

    private fun buildNotification(alarmId: Long, label: String, language: String): android.app.Notification {
        val fullScreenIntent = Intent(this, AlarmRingingActivity::class.java).apply {
            putExtra("alarm_id", alarmId)
            putExtra("alarm_label", label)
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

    private fun playSound(ringtone: String?) {
        try {
            val uri: Uri = if (!ringtone.isNullOrBlank()) {
                Uri.parse(ringtone)
            } else {
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            }
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                setDataSource(this@AlarmService, uri)
                isLooping = true
                prepare()
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            vibrator?.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }

    companion object {
        const val NOTIFICATION_ID = 9001

        fun stop(context: Context) {
            context.stopService(Intent(context, AlarmService::class.java))
        }
    }
}
