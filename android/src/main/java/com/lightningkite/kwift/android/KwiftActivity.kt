package com.lightningkite.kwift.android

import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.lightningkite.kwift.observables.shared.addWeak
import com.lightningkite.kwift.views.shared.EntryPoint
import com.lightningkite.kwift.views.shared.ViewGenerator
import com.lightningkite.kwift.views.shared.showDialogEvent
import java.util.*
import com.lightningkite.kwift.R
import com.lightningkite.kwift.observables.shared.Close

/**
 * An activity that implements [ActivityAccess].
 *
 * Created by jivie on 10/12/15.
 */
abstract class KwiftActivity : AccessibleActivity() {

    abstract val main: ViewGenerator
    lateinit var view: View
    private var showDialogEventCloser:Close? = null

    open fun handleDeepLink(schema: String, host: String, path: String, params: Map<String, String>) {
        println("Got deep link: $schema://$host/$path?${params.entries.joinToString("&") { it.key + "=" + it.value }}")
        (main as? EntryPoint)?.handleDeepLink(schema, host, path, params)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            intent?.data?.let { uri ->
                handleDeepLink(uri)
            }
        }
        view = main.generate(this)
        showDialogEventCloser = showDialogEvent.addWeak(view) { view, request ->
            val builder = AlertDialog.Builder(this)
            builder.setMessage(request.string.get(this))
            request.confirmation?.let { conf ->
                builder.setPositiveButton(android.R.string.ok) { dialog, which -> conf.invoke(); dialog.dismiss() }
                builder.setNeutralButton(android.R.string.cancel) { dialog, which -> dialog.dismiss() }
            } ?: run {
                builder.setPositiveButton(android.R.string.ok) { dialog, which -> dialog.dismiss() }
            }
            builder
                .create()
                .show()
        }
        setContentView(view)
    }

    override fun onDestroy() {
        showDialogEventCloser?.close?.invoke()
        super.onDestroy()
    }

    private fun handleDeepLink(uri: Uri) {
        handleDeepLink(
            uri.scheme ?: "",
            uri.host ?: "",
            uri.path ?: "",
            uri.queryParameterNames.mapNotNull {
                uri.getQueryParameter(it)?.let { p -> it to p }
            }.associate { it }
        )
    }
    override fun onNewIntent(intent: Intent) {
        intent.data?.let { uri ->
            handleDeepLink(uri)
        }
        super.onNewIntent(intent)
    }

    override fun onBackPressed() {
        val toClose = view.findCloseOrBack()
        if(toClose != null){
            toClose.performClick()
        } else {
            val main = main
            if (main !is EntryPoint || !main.onBackPressed() && main.mainStack?.pop() != true){
                super.onBackPressed()
            }
        }
    }

    private val backIds = setOf(R.id.close, R.id.closeButton, R.id.back, R.id.backButton, R.id.backArrow)
    private fun View.findCloseOrBack(): View? {
        if (this.id in backIds) return this
        else if (this is ViewGroup) {
            for (i in this.childCount - 1 downTo 0) {
                val result = getChildAt(i).findCloseOrBack()
                if (result != null) {
                    return result
                }
            }
        }
        return null
    }
}
