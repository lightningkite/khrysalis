package com.lightningkite.khrysalis.rx

import com.lightningkite.khrysalis.observables.StandardObservableProperty
import com.lightningkite.khrysalis.observables.asObservableProperty
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers


private fun test(){
    Observable.just(1, 2, 3).map { it + 1 }.flatMap { Observable.just(it, it + 1) }.observeOn(AndroidSchedulers.mainThread()).subscribeBy { println(it) }.dispose()
    Observable.create { it: ObservableEmitter<Int> -> it.onNext(3); it.onComplete() }.subscribeBy { println(it) }.dispose()
    Observable.create { it: ObservableEmitter<Int> -> it.onNext(3); it.onComplete() }.asObservableProperty(1)

}


/* SHARED DECLARATIONS
class Observable<Element> {
    fun <Destination> map(conversion: (Element)->Destination): Observable<Destination>
    fun filter(predicate: (Element)->Boolean): Observable<Element>
    fun <Destination> flatMap(conversion: (Element)->Observable<Destination>): Observable<Destination>
    fun <Destination> switchMap(conversion: (Element)->Observable<Destination>): Observable<Destination>
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

class Single<Element> {
    fun <Destination> map(conversion: (Element)->Destination): Single<Destination>
    fun <Destination> flatMap(conversion: (Element)->Observable<Destination>): Single<Destination>
    fun subscribeOn(scheduler: Scheduler): Single<Element>
    fun observeOn(scheduler: Scheduler): Single<Element>

    fun toObservable(): Observable<Element>

    fun subscribeBy(
        onError: (Throwable) -> Unit,
        onSuccess: (Element) -> Unit
    ): Disposable

    fun cache(): Single<Element>
    fun doOnSubscribe(action: ()->Unit): Single<Element>
    fun doFinally(action: ()->Unit): Single<Element>
    fun doOnSuccess(action: (Element)->Unit): Single<Element>
    fun doOnError(action: (Exception)->Unit): Single<Element>

    companion object {
        fun create(action: (SingleEmitter<Element>)->Unit): Single<Element>
        fun just(arg: Element): Single<Element>
    }
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
