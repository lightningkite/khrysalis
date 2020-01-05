package com.lightningkite.kwift.android

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.lightningkite.kwift.observables.Event
import com.lightningkite.kwift.observables.StandardEvent
import java.util.*

/**
 * An activity that implements [ActivityAccess].
 *
 * Created by jivie on 10/12/15.
 */
abstract class AccessibleActivity : AppCompatActivity(), ActivityAccess {

    override val activity: Activity?
        get() = this
    override val context: Context
        get() = this

    override var savedInstanceState: Bundle? = null

    override val onResume = StandardEvent<Unit>()
    override val onPause = StandardEvent<Unit>()
    override val onSaveInstanceState = StandardEvent<Bundle>()
    override val onLowMemory = StandardEvent<Unit>()
    override val onDestroy = StandardEvent<Unit>()
    override val onActivityResult = StandardEvent<Triple<Int, Int, Intent?>>()
    override val onNewIntent = StandardEvent<Intent>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.savedInstanceState = savedInstanceState
    }
    override fun onResume() {
        super.onResume()
        onResume.invokeAll(Unit)
    }
    override fun onPause() {
        onPause.invokeAll(Unit)
        super.onPause()
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        onSaveInstanceState.invokeAll(outState)
    }
    override fun onLowMemory() {
        super.onLowMemory()
        onLowMemory.invokeAll(Unit)
    }

    override val onBackPressed = ArrayList<() -> Boolean>()
    override fun onBackPressed() {
        if (!onBackPressed.reversed().any { it.invoke() }) {
            super.onBackPressed()
        }
    }
    override fun onDestroy() {
        onDestroy.invokeAll(Unit)
        super.onDestroy()
    }
    override fun onNewIntent(intent: Intent) {
        onNewIntent.invokeAll(intent)
        super.onNewIntent(intent)
    }

    val requestReturns: HashMap<Int, (Map<String, Int>) -> Unit> = HashMap()

    companion object {
        val returns: HashMap<Int, (Int, Intent?) -> Unit> = HashMap()
    }

    override fun prepareOnResult(presetCode: Int, onResult: (Int, Intent?) -> Unit): Int {
        returns[presetCode] = onResult
        return presetCode
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        onActivityResult.invokeAll(Triple(requestCode, resultCode, data))
        returns[requestCode]?.invoke(resultCode, data)
        returns.remove(requestCode)
    }

    /**
     * Requests a bunch of permissions and returns a map of permissions that were previously ungranted and their new status.
     */
    override fun requestPermissions(permission: Array<String>, onResult: (Map<String, Int>) -> Unit) {
        val ungranted = permission.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (ungranted.isNotEmpty()) {
            val generated: Int = (Math.random() * 0xFFFF).toInt()

            requestReturns[generated] = onResult

            ActivityCompat.requestPermissions(this, ungranted.toTypedArray(), generated)

        } else {
            onResult(emptyMap())
        }
    }

    /**
     * Requests a single permissions and returns whether it was granted or not.
     */
    override fun requestPermission(permission: String, onResult: (Boolean) -> Unit) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {

            val generated: Int = (Math.random() * 0xFFFF).toInt()
            requestReturns[generated] = {
                onResult(it[permission] == PackageManager.PERMISSION_GRANTED)
            }
            ActivityCompat.requestPermissions(this, arrayOf(permission), generated)

        } else {
            onResult(true)
        }
    }

    @TargetApi(23)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (Build.VERSION.SDK_INT >= 23) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)

            val map = HashMap<String, Int>()
            for (i in permissions.indices) {
                map[permissions[i]] = grantResults[i]
            }
            requestReturns[requestCode]?.invoke(map)

            requestReturns.remove(requestCode)
        }
    }
}
