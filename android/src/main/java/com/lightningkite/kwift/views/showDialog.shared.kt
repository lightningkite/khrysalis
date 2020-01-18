package com.lightningkite.kwift.views

import com.lightningkite.kwift.observables.StandardEvent
import com.lightningkite.kwift.observables.StandardObservableProperty
import io.reactivex.subjects.PublishSubject

val lastDialog = StandardObservableProperty<DialogRequest?>(null)
val showDialogEvent: PublishSubject<DialogRequest> = PublishSubject.create()

class DialogRequest(
    val string: ViewString,
    val confirmation: (()->Unit)? = null
)

fun showDialog(request: DialogRequest) {
    lastDialog.value = request
    showDialogEvent.onNext(request)
}

fun showDialog(message: ViewString) {
    showDialog(DialogRequest(string = message))
}
