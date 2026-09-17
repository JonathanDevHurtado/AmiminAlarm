package com.amimin.app

import android.app.Application
import com.amimin.app.notifications.NotificationHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AmiminApp : Application() {

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.ensureChannels(this)
    }

    companion object {
        const val ALARM_NOTIFICATION_CHANNEL_ID = NotificationHelper.CHANNEL_ALARM
        const val REMINDER_NOTIFICATION_CHANNEL_ID = NotificationHelper.CHANNEL_EVENT
    }
}
