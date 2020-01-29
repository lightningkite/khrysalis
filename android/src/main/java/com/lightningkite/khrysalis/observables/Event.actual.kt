package com.lightningkite.khrysalis.observables

import com.lightningkite.khrysalis.AnyObject
import com.lightningkite.khrysalis.weak
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.*

@Deprecated("Use RX directly instead", ReplaceWith("onNext(value)"))
fun <Element> Observer<Element>.invokeAll(value: Element) = onNext(value)
