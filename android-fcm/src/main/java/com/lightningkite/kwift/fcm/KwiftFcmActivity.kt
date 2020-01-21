package com.lightningkite.kwift.fcm

import android.content.Intent
import android.os.Bundle
import com.lightningkite.kwift.android.KwiftActivity

abstract class KwiftFcmActivity : KwiftActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MyFirebaseMessagingService.main = main
    }
}
