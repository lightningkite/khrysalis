package com.lightningkite.kwift.actuals

import android.os.Handler
import android.os.Looper

fun delay(milliseconds: Long, action: () -> Unit) {
    if (milliseconds == 0L) action()
    else Handler(Looper.getMainLooper()).postDelayed(action, milliseconds)
}
