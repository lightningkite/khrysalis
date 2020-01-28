package com.lightningkite.kwift

import android.os.Handler
import android.os.Looper
import com.lightningkite.kwift.observables.StandardEvent
import io.reactivex.subjects.PublishSubject

fun delay(milliseconds: Long, action: () -> Unit) {
    if (milliseconds == 0L) action()
    else Handler(Looper.getMainLooper()).postDelayed(action, milliseconds)
}

fun post(action: () -> Unit) {
    Handler(Looper.getMainLooper()).post(action)
}

val animationFrame: PublishSubject<Float> = PublishSubject.create()