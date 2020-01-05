package com.lightningkite.kwift.views.android

import android.content.Intent
import android.os.Bundle
import com.lightningkite.kwift.views.ViewDependency


/**
 * Starts an intent with a direct callback.
 */
fun ViewDependency.startIntent(
    intent: Intent,
    options: Bundle = android.os.Bundle(),
    onResult: (Int, Intent?) -> Unit = { _, _ -> }
) {
    activity?.startActivityForResult(intent, prepareOnResult(onResult = onResult), options)
}
