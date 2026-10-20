package com.amimin.app

import android.app.Application
import android.util.Log
import com.amimin.app.data.database.AlarmRescheduler
import com.amimin.app.notifications.NotificationHelper
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class AmiminApp : Application() {

    @Inject
    lateinit var alarmRescheduler: AlarmRescheduler

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.ensureChannels(this)
        // Auto Backup restaura la base de datos pero no los PendingIntent de AlarmManager.
        // Reprogramar al abrir la app recupera también ese caso.
        applicationScope.launch {
            try {
                alarmRescheduler.rescheduleAll()
            } catch (e: Exception) {
                Log.e(TAG, "No se pudieron reprogramar las alarmas al iniciar", e)
            }
        }
    }

    companion object {
        private const val TAG = "AmiminApp"
        const val ALARM_NOTIFICATION_CHANNEL_ID = NotificationHelper.CHANNEL_ALARM
        const val REMINDER_NOTIFICATION_CHANNEL_ID = NotificationHelper.CHANNEL_EVENT
    }
}
