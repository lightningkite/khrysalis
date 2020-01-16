package com.lightningkite.kwift.android

import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject

fun test(){
    Observable.just(1, 2, 3).map { it + 1 }.flatMap { Observable.just(it, it + 1) }
}

/*

TODO:
startWith multi
PublishSubject constructor

 */
