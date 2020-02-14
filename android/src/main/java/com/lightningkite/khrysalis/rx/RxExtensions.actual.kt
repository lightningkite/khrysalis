package com.lightningkite.khrysalis.rx

import com.lightningkite.khrysalis.Box
import io.reactivex.Observable
import io.reactivex.functions.BiFunction


/**
 * Combine latest operator
 */
fun <Element : Any, R : Any, OUT: Any> Observable<Element>.combineLatest(observable: Observable<R>, function: (Element, R)->OUT): Observable<OUT>
        = Observable.combineLatest(this, observable, BiFunction(function))

fun <Element> Observable<Box<Element>>.filterNotNull(): Observable<Element>
        = this.filter { it.value != null }.map { it.value }

fun <Element : Any, Destination: Any> Observable<Element>.mapNotNull(transform: (Element)->Destination?): Observable<Destination>
        = this.flatMap {
    val result = transform(it)
    if(result == null)
        Observable.empty()
    else
        Observable.just(result)
}

