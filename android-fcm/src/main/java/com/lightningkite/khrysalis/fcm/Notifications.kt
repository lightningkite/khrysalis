package com.lightningkite.khrysalis.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import com.lightningkite.khrysalis.net.HttpClient
import com.lightningkite.khrysalis.observables.StandardObservableProperty

object Notifications {
    var notificationToken = StandardObservableProperty<String?>(null)
    @Deprecated("Use 'request' instead", ReplaceWith("this.request"))
    fun configure() = request()
    fun request(firebaseAppName: String? = null) {
        if (Build.VERSION.SDK_INT >= 26) {
            (HttpClient.appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                NotificationChannel(HttpClient.appContext.getString(R.string.default_notification_channel_id), "Default", NotificationManager.IMPORTANCE_DEFAULT)
            )
        }
        if(firebaseAppName != null){
            FirebaseInstanceId.getInstance(FirebaseApp.getInstance(firebaseAppName)).instanceId.addOnCompleteListener {
                notificationToken.value = it.result?.token
            }
        } else {
            FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
                notificationToken.value = it.result?.token
            }
        }
    }
}
