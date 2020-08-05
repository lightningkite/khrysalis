package com.lightningkite.khrysalis

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.lightningkite.khrysalis.observables.ObservableProperty
import com.lightningkite.khrysalis.observables.StandardObservableProperty
import com.lightningkite.khrysalis.observables.asObservableProperty
import com.lightningkite.khrysalis.observables.map
import com.lightningkite.khrysalis.views.geometry.GFloat
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

fun delay(milliseconds: Long, action: () -> Unit) {
    if (milliseconds == 0L) action()
    else Handler(Looper.getMainLooper()).postDelayed(action, milliseconds)
}

fun post(action: () -> Unit) {
    Handler(Looper.getMainLooper()).post(action)
}

val animationFrame: PublishSubject<GFloat> = PublishSubject.create()

private val applicationIsActiveEvent = PublishSubject.create<Boolean>()
val applicationIsActive: ObservableProperty<Boolean> = applicationIsActiveEvent
    .debounce(100L, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
    .distinctUntilChanged()
    .asObservableProperty(true)

@PlatformSpecific fun applicationIsActiveStartup(application: Application){
    var activeCount = 0
    application.registerActivityLifecycleCallbacks(object: Application.ActivityLifecycleCallbacks {
        override fun onActivityResumed(activity: Activity) {
            if(activeCount == 0){
                applicationIsActiveEvent.onNext(true)
            }
            activeCount++
        }
        override fun onActivityPaused(activity: Activity) {
            activeCount--
            if(activeCount == 0){
                applicationIsActiveEvent.onNext(false)
            }
        }
        override fun onActivityStarted(activity: Activity) {}
        override fun onActivityDestroyed(activity: Activity) {}
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        override fun onActivityStopped(activity: Activity) {}
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    })
}