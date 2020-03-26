package com.lightningkite.khrysalis.rx

import com.lightningkite.khrysalis.AnyObject
import com.lightningkite.khrysalis.escaping
import com.lightningkite.khrysalis.weak
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.subscribeBy

@Deprecated("Just use RX stuff")
fun <Element: Any> Observable<Element>.add(listener: @escaping() (Element) -> Boolean): Disposable {
    var disposable: Disposable? = null
    val disp = this.subscribeBy(onNext = { item ->
        if (listener(item)) {
            disposable?.dispose()
        }
    })
    disposable = disp
    return disp
}

@Deprecated("Just use RX disposal stuff")
fun <A: AnyObject, Element: Any> Observable<Element>.addWeak(referenceA: A, listener: @escaping() (A, Element) -> Unit): Disposable {
    var disposable: Disposable? = null
    val weakA: A? by weak(referenceA)
    val disp = this.subscribeBy(onNext = { item ->
        val a = weakA
        if (a != null) {
            listener(a, item)
        } else {
            disposable?.dispose()
        }
    })
    disposable = disp
    return disp
}

@Deprecated("Just use RX disposal stuff")
fun <A: AnyObject, B: AnyObject, Element: Any> Observable<Element>.addWeak(referenceA: A, referenceB: B, listener: @escaping() (A, B, Element) -> Unit): Disposable {
    var disposable: Disposable? = null
    val weakA: A? by weak(referenceA)
    val weakB: B? by weak(referenceB)
    val disp = this.subscribeBy(onNext = { item ->
        val a = weakA
        val b = weakB
        if (a != null && b != null) {
            listener(a, b, item)
        } else {
            disposable?.dispose()
        }
    })
    disposable = disp
    return disp
}


@Deprecated("Just use RX disposal stuff")
fun <A: AnyObject, B: AnyObject, C: AnyObject, Element: Any> Observable<Element>.addWeak(referenceA: A, referenceB: B, referenceC: C, listener: @escaping() (A, B, C, Element) -> Unit): Disposable {
    var disposable: Disposable? = null
    val weakA: A? by weak(referenceA)
    val weakB: B? by weak(referenceB)
    val weakC: C? by weak(referenceC)
    val disp = this.subscribeBy(onNext = { item ->
        val a = weakA
        val b = weakB
        val c = weakC
        if (a != null && b != null && c != null) {
            listener(a, b, c, item)
        } else {
            disposable?.dispose()
        }
    })
    disposable = disp
    return disp
}
