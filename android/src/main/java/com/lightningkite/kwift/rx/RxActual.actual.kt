package com.lightningkite.kwift.rx

import com.lightningkite.kwift.observables.StandardObservableProperty
import com.lightningkite.kwift.observables.asObservableProperty
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers


fun test(){
    Observable.just(1, 2, 3).map { it + 1 }.flatMap { Observable.just(it, it + 1) }.observeOn(AndroidSchedulers.mainThread()).subscribeBy { println(it) }.dispose()
    Observable.create { it: ObservableEmitter<Int> -> it.onNext(3); it.onComplete() }.subscribeBy { println(it) }.dispose()
    Observable.create { it: ObservableEmitter<Int> -> it.onNext(3); it.onComplete() }.asObservableProperty(1)
}


/* SHARED DECLARATIONS
class Observable<Element> {
    fun <Destination> map(conversion: (Element)->Destination): Observable<Destination>
    fun filter(predicate: (Element)->Boolean): Observable<Element>
    fun <Destination> flatMap(conversion: (Element)->Observable<Destination>): Observable<Destination>
    fun subscribeOn(scheduler: Scheduler): Observable<Element>
    fun observeOn(scheduler: Scheduler): Observable<Element>

    fun subscribeBy(
        onError: (Throwable) -> Unit,
        onComplete: () -> Unit,
        onNext: (Element) -> Unit
    ): Disposable

    companion object {
        fun create(action: (ObservableEmitter<Element>)->Unit): Observable<Element>
        fun just(vararg args: Element): Observable<Element>
        fun empty(): Observable<Element>
    }
}
class Subject<Element> {
    fun onNext(value: Element)
    fun onError(error: Exception)
    fun onComplete()
}

typealias Scheduler = ImmediateModeScheduler
object Schedulers {
    fun newThread(): Scheduler
    fun io(): Scheduler
}
object AndroidSchedulers {
    fun mainThread(): Scheduler
}

fun <Element> BehaviorSubject.Companion.create(value: Element): BehaviorSubject<Element>
fun <Element> PublishSubject.Companion.create(): PublishSubject<Element>

 */
