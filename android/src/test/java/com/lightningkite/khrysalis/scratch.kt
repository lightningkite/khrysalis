package com.lightningkite.khrysalis

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject

fun test(){
    val obs = PublishSubject.create<Int>()
    obs.doOnSubscribe {  }
}