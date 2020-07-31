package com.lightningkite.khrysalis.fcm

import android.os.Bundle
import com.lightningkite.khrysalis.android.KhrysalisActivity
import com.lightningkite.khrysalis.views.ViewGenerator

abstract class FirebaseMessagingActivity: KhrysalisActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MyFirebaseMessagingService.main = this.main
    }
}