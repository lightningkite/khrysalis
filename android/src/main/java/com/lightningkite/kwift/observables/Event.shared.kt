package com.lightningkite.kwift.observables

import com.lightningkite.kwift.AnyObject
import com.lightningkite.kwift.weak
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.*

@Deprecated("Use RX directly instead", ReplaceWith("Observable<Element>", "io.reactivex.Observable"))
typealias Event<Element> = Observable<Element>

@Deprecated("Use RX directly instead", ReplaceWith("Subject<Element>", "io.reactivex.subjects.Subject"))
typealias InvokableEvent<Element> = Subject<Element>

@Deprecated("Use RX directly instead", ReplaceWith("Subject<Element>", "io.reactivex.subjects.PublishSubject"))
typealias StandardEvent<Element> = PublishSubject<Element>

//@Deprecated("Use RX directly instead", ReplaceWith("Subject<Element>", "io.reactivex.subjects.PublishSubject"))
//fun <Element> StandardEvent(): PublishSubject<Element> = PublishSubject.create<Element>()

@Deprecated("Use RX directly instead", ReplaceWith("onNext(value)"))
fun <Element> Observer<Element>.invokeAll(value: Element) = onNext(value)

fun test(){
    PublishSubject.create<Int>()
}
