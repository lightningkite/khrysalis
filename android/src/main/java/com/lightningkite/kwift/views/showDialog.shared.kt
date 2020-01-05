package com.lightningkite.kwift.views

import com.lightningkite.kwift.observables.StandardEvent
import com.lightningkite.kwift.observables.StandardObservableProperty

val lastDialog = StandardObservableProperty<DialogRequest?>(null)
val showDialogEvent = StandardEvent<DialogRequest>()

class DialogRequest(
    val string: ViewString,
    val confirmation: (()->Unit)? = null
)

fun showDialog(request: DialogRequest) {
    lastDialog.value = request
    showDialogEvent.invokeAll(request)
}

fun showDialog(message: ViewString) {
    showDialog(DialogRequest(string = message))
}
