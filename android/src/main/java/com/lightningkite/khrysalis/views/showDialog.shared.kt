package com.lightningkite.khrysalis.views

import com.lightningkite.khrysalis.observables.StandardEvent
import com.lightningkite.khrysalis.observables.StandardObservableProperty
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
