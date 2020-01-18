package com.lightningkite.kwift.rx

import io.reactivex.Observable
import io.reactivex.functions.BiFunction


/**
 * Combine latest operator that produces [Pair]
 */
fun <T : Any, R : Any, OUT: Any> Observable<T>.combineLatest(observable: Observable<R>, function: (T, R)->OUT): Observable<OUT>
        = Observable.combineLatest(this, observable, BiFunction(function))
