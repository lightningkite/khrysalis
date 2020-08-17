import RxSwift
import RxRelay

//--- test()
func testa() -> Void {
//    let x: Observable<Optional<Int>> = Observable.just(
//        Optional.some(1),
//        Optional.some(2),
//        Optional.some(3),
//        Optional.none
//    )
//    let mapped: Observable<Optional<String>> = x.map { it in it.value?.toString() }
//    let x2: Observable<Int32> = PublishSubject.create()
//    let x3 = PublishSubject<String>.create().subscribeOn(MainScheduler.instance)
//    let x2: Observable<Int> = Observable.create { obs in
//        obs.onNext(32)
//    }
//
//    Observable.just(1, 2, 3).map { it in it + 1 }.flatMap { it in Observable.just(it, it + 1) }.observeOn(AndroidSchedulers.mainThread()).subscribeBy { it in print(it) }.dispose()
//    Observable.create { (it: ObservableEmitter<Int>) in it.onNext(3); it.onComplete() }.subscribeBy { it in print(it) }.dispose()
//    Observable.create { (it: ObservableEmitter<Int>) in it.onNext(3); it.onComplete() }.asObservableProperty(1)
//    Single.just(32).fla
//    Observable.just(1).switchMap { it in Observable.just(1 + 2) }
}

//--- Observer.onComplete
public extension ObserverType {
    func onComplete() {
        self.onCompleted()
    }
}

//--- Observable.{
public typealias Observables<T> = Observable<T>
public extension Observable {

    //--- Observable.map((Element)->Destination)
    //--- Observable.filter((Element)->Boolean)
    //--- Observable.flatMap((Element)->Observable<Destination>)
    func flatMapNR<Destination>(_ conversion: @escaping (Element)->Observable<Destination>) -> Observable<Destination> {
        return self.flatMap { (it: Element) -> Observable<Destination> in
            conversion(it)
        }
    }

    //--- Observable.switchMap((Element)->Observable<Destination>)
    func switchMap<Destination>(_ conversion: @escaping (Element) -> Observable<Destination>) -> Observable<Destination> {
        return self.flatMapLatest { (it: Element) -> Observable<Destination> in
            conversion(it)
        }
    }

    //--- Observable.drop(Int)
    func drop(_ count: Int) -> Observable<Element> {
        return self.skip(count)
    }

    //--- Observable.subscribeOn(Scheduler)
    //--- Observable.observeOn(Scheduler)

    //--- Observable.subscribeBy((Throwable)->Unit, ()->Unit, (Element)->Unit)
    func subscribeBy(onError: ((Swift.Error) -> Void)? = nil, onComplete: (() -> Void)? = nil, onNext: ((Element) -> Void)? = nil) -> Disposable {
        return subscribe(onNext: onNext, onError: onError, onCompleted: onComplete, onDisposed: nil)
    }
    func subscribeBy(_ onError: ((Swift.Error) -> Void)? = nil, _ onComplete: (() -> Void)? = nil, _ onNext: ((Element) -> Void)? = nil) -> Disposable {
        return subscribe(onNext: onNext, onError: onError, onCompleted: onComplete, onDisposed: nil)
    }
    func subscribe(_ onNext: @escaping (Element) -> Void) -> Disposable {
        return subscribe(onNext: onNext, onError: nil, onCompleted: nil, onDisposed: nil)
    }
    func subscribeBy(_ onNext: ((Element) -> Void)? = nil) -> Disposable {
        return subscribe(onNext: onNext, onError: nil, onCompleted: nil, onDisposed: nil)
    }
    
    //--- Observable.doOnComplete(()->Unit)
    func doOnComplete(_ action: @escaping () throws -> Void) -> Observable<Element> {
        return self.do(onCompleted: action)
    }
    //--- Observable.doOnError((Throwable)->Unit)
    func doOnError(_ action: @escaping (Error) throws -> Void) -> Observable<Element> {
        return self.do(onError: action)
    }
    //--- Observable.doOnNext((T)->Unit)
    func doOnNext(_ action: @escaping (Element) throws -> Void) -> Observable<Element> {
        return self.do(onNext: action)
    }
    //--- Observable.doOnTerminate(()->Unit)
    func doOnTerminate(_ action: @escaping () -> Void) -> Observable<Element> {
        return self.do(onDispose: action)
    }
    
    //--- Observable.doOnSubscribe(()->Unit)
    func doOnSubscribe(_ action: @escaping (Disposable) -> Void) -> Observable<Element> {
        return self.do(onSubscribe: { action(placeholderDisposable) })
    }
    //--- Observable.doOnDispose(()->Unit)
    func doOnDispose(_ action: @escaping () -> Void) -> Observable<Element> {
        return self.do(onDispose: action)
    }

    //--- Observable.Companion.{ (overwritten on flow generation)
    
    //--- Observable.Companion.create((ObservableEmitter<Element>)->Unit)
    static func create(_ action: @escaping (ObservableEmitter<Element>) -> Void) -> Observable<Element> {
        return Observable.create { (it: AnyObserver<Element>) in
            let emitter = ObservableEmitter(basedOn: it)
            action(emitter)
            return emitter.disposable ?? Disposables.create { }
        }
    }

    //--- Observable.Companion.just(Element)
    static func just(_ items: Element...) -> Observable<Element> {
        return Observable<Element>.from(items)
    }
    
    //--- Observable.Companion.empty()

    //--- Observable.Companion.} (overwritten on flow generation)
    
    //--- Observable.} (overwritten on flow generation)
}

//--- ObservableEmitter
public class ObservableEmitter<Element>: ObserverType {
    public func on(_ event: RxSwift.Event<Element>) {
        basedOn.on(event)
    }

    public var basedOn: AnyObserver<Element>
    public init(basedOn: AnyObserver<Element>) {
        self.basedOn = basedOn
    }

    public var disposable: Disposable? = nil
    public func setDisposable(_ disposable: Disposable?) {
        self.disposable = disposable
    }
    public var isDisposed: Bool = false
}

//--- Subject.{
public typealias Subject<T> = Observable<T>
public extension Subject {

    func tryObserver() -> AnyObserver<Element>? {
        if let thing = self as? PublishSubject {
            return thing.asObserver()
        }
        if let thing = self as? BehaviorSubject {
            return thing.asObserver()
        }
        if let thing = self as? AsyncSubject {
            return thing.asObserver()
        }
        if let thing = self as? ReplaySubject {
            return thing.asObserver()
        }
        return nil
    }

    //--- Subject.onNext(Element)
    func onNext(_ element: Element) {
        tryObserver()?.onNext(element)
    }
    func onNext(value: Element) -> Void {
        return onNext(value)
    }

    //--- Subject.onError(Exception)
    func onError(error: Error) -> Void {
        return onError(error)
    }
    func onError(_ error: Error) {
        tryObserver()?.onError(error)
    }

    //--- Subject.onComplete()
    func onCompleted() {
        tryObserver()?.onCompleted()
    }

    //--- Subject.} (overwritten on flow generation)
}

private let placeholderDisposable = DisposableLambda {}

//--- Single.{
public extension PrimitiveSequenceType where Trait == SingleTrait {

    //--- Single.map((Element)->Destination)
    //--- Single.flatMap((Element)->Observable<Destination>)
    //--- Single.subscribeOn(Scheduler)
    //--- Single.observeOn(Scheduler)

    //--- Single.toObservable()
    func toObservable() -> Observable<Element> {
        return self.primitiveSequence.asObservable()
    }

    //--- Single.subscribeBy((Throwable)->Unit, (Element)->Unit)
    func subscribeBy(_ onError: ((Error) -> Void)? = nil, _ onSuccess: ((Element) -> Void)? = nil) -> Disposable {
        return self.subscribe(onSuccess: onSuccess, onError: onError)
    }
    func subscribeBy(onError: @escaping (Error) -> Void, onSuccess: @escaping (Element) -> Void) -> Disposable {
        return subscribeBy(onError, onSuccess)
    }

    //--- Single.cache()
    func cache() -> Single<Self.Element> {
        return self.toObservable().share(replay: 1, scope: .forever).asSingle()
    }

    //--- Single.doOnSubscribe(()->Unit)
    func doOnSubscribe(_ action: @escaping (Disposable) -> Void) -> Single<Element> {
        return self.do(onSubscribe: { action(placeholderDisposable) })
    }

    //--- Single.doFinally(()->Unit)
    func doFinally(_ action: @escaping () -> Void) -> Single<Element> {
        return self.do(onSuccess: { _ in action() }, onError: { _ in action() })
    }

    //--- Single.doOnSuccess((Element)->Unit)
    func doOnSuccess(_ action: @escaping (Element) -> Void) -> Single<Element> {
        return self.do(onSuccess: action)
    }

    //--- Single.doOnError((Exception)->Unit)
    func doOnError(_ action: @escaping (Swift.Error) -> Void) -> Single<Element> {
        return self.do(onError: action)
    }

    //--- Single.Companion.{

    //--- Single.Companion.create((SingleEmitter<Element>)->Unit)
    static func create(_ action: @escaping (SingleEmitter<Element>) -> Void) -> Single<Element> {
        return Single.create { (callback) -> Disposable in
            let emitter = SingleEmitter<Element>(basedOn: callback)
            action(emitter)
            return emitter.disposable ?? Disposables.create { }
        }
    }

    //--- Single.Companion.just(Element)
    //--- Single.Companion.} (overwritten on flow generation)
    
    //--- Single.} (overwritten on flow generation)
}

//--- SingleEmitter
public class SingleEmitter<Element> {
    public func on(_ event: RxSwift.SingleEvent<Element>) {
        basedOn(event)
    }
    public func onSuccess(_ element: Element) {
        basedOn(.success(element))
    }
    public func onError(_ error: Error) {
        basedOn(.error(error))
    }

    public var basedOn: (RxSwift.SingleEvent<Element>)->Void
    public init(basedOn: @escaping (RxSwift.SingleEvent<Element>)->Void) {
        self.basedOn = basedOn
    }

    public var disposable: Disposable? = nil
    public func setDisposable(_ disposable: Disposable?) {
        self.disposable = disposable
    }
    public var isDisposed: Bool = false
}

//--- Scheduler
public typealias Scheduler = RxSwift.SchedulerType

//--- Schedulers.{ (overwritten on flow generation)
public enum Schedulers {
    
    //--- Schedulers.newThread()
    public static func newThread() -> Scheduler {
        return ConcurrentDispatchQueueScheduler(qos: .background)
    }

    //--- Schedulers.io()
    public static func io() -> Scheduler {
        return ConcurrentDispatchQueueScheduler(qos: .background)
    }

    //--- Schedulers.} (overwritten on flow generation)
}

//--- AndroidSchedulers.{ (overwritten on flow generation)
public enum AndroidSchedulers {
    
    //--- AndroidSchedulers.mainThread()
    public static func mainThread() -> Scheduler {
        return MainScheduler.instance
    }

    //--- AndroidSchedulers.} (overwritten on flow generation)
}

//--- BehaviorSubject.Companion.create(Element)
public extension BehaviorSubject {
    static func create(value: Element) -> BehaviorSubject<Element> {
        return BehaviorSubject(value: value)
    }
}
//--- PublishSubject.Companion.create()
public extension PublishSubject {
    static func create() -> PublishSubject<Element> {
        return PublishSubject()
    }
}

//
//  distinct.swift
//  RxSwiftExt
//
//  Created by Segii Shulga on 5/4/16.
//  Copyright © 2017 RxSwift Community. All rights reserved.
//  Copyright (c) 2016-latest RxSwiftCommunity https://github.com/RxSwiftCommunity
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files (the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions:
//
//  The above copyright notice and this permission notice shall be included in
//  all copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//  THE SOFTWARE.
//

extension Observable {
    public func distinct<T: Hashable>(_ by: @escaping (Element)->T) -> Observable<Element> {
         var cache = Set<T>()
         return flatMap { element -> Observable<Element> in
             if cache.contains(by(element)) {
                 return Observable<Element>.empty()
             } else {
                 cache.insert(by(element))
                 return Observable<Element>.just(element)
             }
         }
     }
}

extension Observable where Element: Hashable {
    /**
     Suppress duplicate items emitted by an Observable
     - seealso: [distinct operator on reactivex.io](http://reactivex.io/documentation/operators/distinct.html)
     - returns: An observable sequence only containing the distinct contiguous elements, based on equality operator, from the source sequence.
     */
     public func distinct() -> Observable<Element> {
         var cache = Set<Element>()
         return flatMap { element -> Observable<Element> in
             if cache.contains(element) {
                 return Observable<Element>.empty()
             } else {
                 cache.insert(element)
                 return Observable<Element>.just(element)
             }
         }
     }
}

extension Observable where Element: Equatable {
    /**
     Suppress duplicate items emitted by an Observable
     - seealso: [distinct operator on reactivex.io](http://reactivex.io/documentation/operators/distinct.html)
     - returns: An observable sequence only containing the distinct contiguous elements, based on equality operator, from the source sequence.
     */
    public func distinct() -> Observable<Element> {
        var cache = [Element]()
        return flatMap { element -> Observable<Element> in
            if cache.contains(element) {
                return Observable<Element>.empty()
            } else {
                cache.append(element)
                return Observable<Element>.just(element)
            }
        }
    }
}
