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

@Deprecated("Use RX directly instead", ReplaceWith("onNext(value)"))
fun <Element> Observer<Element>.invokeAll(value: Element) = onNext(value)
