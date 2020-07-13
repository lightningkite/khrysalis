// Generated by Khrysalis Swift converter - this file will be overwritten.
// File: observables/ObservableProperty.ext.shared.kt
// Package: com.lightningkite.khrysalis.observables
import RxSwift
import Foundation

public extension ObservableProperty {
    var observable: Observable<T> {
        get { return self.onChange.startWith(self.value) }
    }
}
public extension ObservableProperty {
    var observableNN: Observable<T> {
        get { return self.onChange.startWith(self.value).map({ (it: T) -> T in it }) }
    }
}
public extension ObservableProperty {
    var onChangeNN: Observable<T> {
        get { return self.onChange.map({ (it: T) -> T in it }) }
    }
}

public extension ObservableProperty {
    func subscribeBy(onError: @escaping  (Error) -> Void = { (it: Error) -> Void in it.printStackTrace() }, onComplete: @escaping  () -> Void = { () -> Void in  }, onNext: @escaping  (T) -> Void = { (it: T) -> Void in  }) -> Disposable { return self.observable.subscribeBy(onError: onError, onComplete: onComplete, onNext: { (boxed: T) -> Void in onNext(boxed) }) }
}

public func includes<E>(collection: MutableObservableProperty<Set<E>>, element: E) -> MutableObservableProperty<Bool> {
    return collection.map(read: { (it: Set<E>) -> Bool in it.contains(element) }).withWrite(onWrite: { (it: Bool) -> Void in if it {
                collection.value = collection.value.union([element])
            } else {
                collection.value = collection.value.subtracting([element])
    } })
}

