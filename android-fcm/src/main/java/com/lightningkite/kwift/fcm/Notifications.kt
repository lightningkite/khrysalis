package com.lightningkite.kwift.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import com.lightningkite.kwift.fcm.R
import com.lightningkite.kwift.net.HttpClient
import com.lightningkite.kwift.observables.StandardObservableProperty

object Notifications {
    var notificationToken = StandardObservableProperty<String?>(null)
    fun configure() {
        if (Build.VERSION.SDK_INT >= 26) {
            (HttpClient.appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                NotificationChannel(HttpClient.appContext.getString(R.string.default_notification_channel_id), "Default", NotificationManager.IMPORTANCE_DEFAULT)
            )
        }
    }
}
