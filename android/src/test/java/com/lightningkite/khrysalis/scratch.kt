package com.lightningkite.khrysalis

import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject

fun test(){
    val obs = PublishSubject.create<Int>()
    obs.map {  }
}