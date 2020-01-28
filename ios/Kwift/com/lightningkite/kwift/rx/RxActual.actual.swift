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
    func getRepo(_ repo: String) -> Single<[String: Any]> {
        return Single<[String: Any]>.create { (single: @escaping (SingleEvent<[String: Any]>)->Void) in
            let task = URLSession.shared.dataTask(with: URL(string: "https://api.github.com/repos/\(repo)")!) { data, _, error in
                if let error = error {
                    single(.error(error))
                    return
                }

                guard let data = data,
                      let json = try? JSONSerialization.jsonObject(with: data, options: .mutableLeaves),
                      let result = json as? [String: Any] else {
                    single(.error(IllegalStateException("fail")))
                    return
                }

                single(.success(result))
            }

            task.resume()

            return Disposables.create { task.cancel() }
        }
    }
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
    
    //--- Observable.subscribeOn(Scheduler)
    //--- Observable.observeOn(Scheduler)
    
    //--- Observable.subscribeBy((Throwable)->Unit, ()->Unit, (Element)->Unit)
    func subscribeBy(onNext: ((Element) -> Void)? = nil, onError: ((Swift.Error) -> Void)? = nil, onCompleted: (() -> Void)? = nil) -> Disposable {
        return subscribe(onNext: onNext, onError: onError, onCompleted: onCompleted, onDisposed: nil)
    }
    func subscribeBy(_ onNext: ((Element) -> Void)? = nil, _ onError: ((Swift.Error) -> Void)? = nil, _ onCompleted: (() -> Void)? = nil) -> Disposable {
        return subscribe(onNext: onNext, onError: onError, onCompleted: onCompleted, onDisposed: nil)
    }
    func subscribe(_ onNext: ((Element) -> Void)? = nil, _ onError: ((Swift.Error) -> Void)? = nil, _ onCompleted: (() -> Void)? = nil) -> Disposable {
        return subscribe(onNext: onNext, onError: onError, onCompleted: onCompleted, onDisposed: nil)
    }
    func subscribe(_ onNext: @escaping (Element) -> Void) -> Disposable {
        return subscribe(onNext: onNext, onError: nil, onCompleted: nil, onDisposed: nil)
    }
    func subscribeBy(_ onNext: @escaping (Element) -> Void) -> Disposable {
        return subscribe(onNext: onNext, onError: nil, onCompleted: nil, onDisposed: nil)
    }
    
    //--- Observable.Companion.{ (overwritten on flow generation)
    
    //--- Observable.Companion.create((ObservableEmitter<Element>)->Unit)
    static func create(_ action: @escaping (ObservableEmitter<Element>) -> Void) -> Observable<Element> {
        return Observable.create { (it: AnyObserver<Element>) in
            let emitter = ObservableEmitter(basedOn: it)
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
    func subscribeBy(_ onError: @escaping (Error) -> Void, _ onSuccess: @escaping (Element) -> Void) -> Disposable {
        return self.subscribe(onSuccess: onSuccess, onError: onError)
    }
    func subscribeBy(onError: @escaping (Error) -> Void, onSuccess: @escaping (Element) -> Void) -> Disposable {
        return subscribeBy(onError, onSuccess)
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
public typealias Scheduler = RxSwift.ImmediateSchedulerType

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