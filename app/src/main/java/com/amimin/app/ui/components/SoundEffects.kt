package com.amimin.app.ui.components

import android.content.Context
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

data class AlarmSoundOption(val title: String, val uri: String)

object AlarmSounds {

    fun getSystemSounds(context: Context): List<AlarmSoundOption> {
        val sounds = mutableListOf<AlarmSoundOption>()
        try {
            val manager = RingtoneManager(context)
            manager.setType(RingtoneManager.TYPE_ALARM or RingtoneManager.TYPE_RINGTONE)
            val cursor = manager.cursor
            while (cursor.moveToNext()) {
                val title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX) ?: continue
                val uri = manager.getRingtoneUri(cursor.position) ?: continue
                sounds.add(AlarmSoundOption(title, uri.toString()))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return sounds
    }

    fun defaultAlarmUri(): String =
        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)?.toString() ?: ""

    fun playPreview(context: Context, uriString: String): Ringtone? {
        return try {
            val uri = if (uriString.isBlank()) {
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            } else Uri.parse(uriString)
            val ringtone = RingtoneManager.getRingtone(context, uri)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ringtone.audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            }
            ringtone.play()
            ringtone
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun stopPreview(ringtone: Ringtone?) {
        try {
            if (ringtone?.isPlaying == true) ringtone.stop()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun vibrateShort(context: Context) {
    try {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(30)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
