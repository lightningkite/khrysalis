package com.lightningkite.khrysalis.android

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.lightningkite.khrysalis.observables.Event
import io.reactivex.subjects.Subject

/**
 * An interface for accessing activities in a decentralized way, where multiple listeners can listen
 * to event like [onPause], [onResume], etc, and most importantly, can use [prepareOnResult] to set
 * up lambdas to occur when a result code is received.
 *
 * Created by joseph on 6/9/17.
 */
interface ActivityAccess {
    val activity: Activity?
    val context: Context
    val savedInstanceState: Bundle?

    val onResume: Subject<Unit>
    val onPause: Subject<Unit>
    val onSaveInstanceState: Subject<Bundle>
    val onLowMemory: Subject<Unit>
    val onDestroy: Subject<Unit>
    val onActivityResult: Subject<Triple<Int, Int, Intent?>>
    val onNewIntent: Subject<Intent>

    fun performBackPress()

    fun prepareOnResult(
        presetCode: Int = (Math.random() * Short.MAX_VALUE).toInt(),
        onResult: (Int, Intent?) -> Unit = { a, b -> }
    ): Int

    fun requestPermissions(permission: Array<String>, onResult: (Map<String, Int>) -> Unit)
    fun requestPermission(permission: String, onResult: (Boolean) -> Unit)

}
