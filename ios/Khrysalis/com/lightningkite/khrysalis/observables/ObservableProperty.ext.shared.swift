//Package: com.lightningkite.khrysalis.observables
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



extension ObservableProperty {
    public var observable: Observable<T> {
        get {
            return onChange.startWith(value)
        }
    }
}
 

extension ObservableProperty {
    public var observableNN: Observable<T> {
        get {
            return onChange.startWith(value)
        }
    }
}
 

extension ObservableProperty {
    public var onChangeNN: Observable<T> {
        get {
            return onChange
        }
    }
}
 

extension ObservableProperty {
     public func subscribeBy(onError: @escaping (Throwable) -> Void = { (it) in 
        it.printStackTrace()
    }, onComplete: @escaping () -> Void = { () in 
    }, onNext: @escaping (T) -> Void = { (it) in 
    }) -> Disposable {
        return self.observable.subscribeBy(onError: onError, onComplete: onComplete, onNext: { (boxed) in 
            onNext(boxed.value)
        })
    }
     public func subscribeBy(_ onError: @escaping (Throwable) -> Void, _ onComplete: @escaping () -> Void = { () in 
    }, _ onNext: @escaping (T) -> Void = { (it) in 
    }) -> Disposable {
        return subscribeBy(onError: onError, onComplete: onComplete, onNext: onNext)
    }
}
 
 

public func includes<E>(collection: MutableObservableProperty<Set<E>>, element: E) -> MutableObservableProperty<Bool> {
    return collection.map{ (it) in 
        it.contains(element)
    }.withWrite{ (it) in 
        if it {
            collection.value = collection.value.plus(element)
        } else {
            collection.value = collection.value.minus(element)
        }
    }
}
public func includes<E>(_ collection: MutableObservableProperty<Set<E>>, _ element: E) -> MutableObservableProperty<Bool> {
    return includes(collection: collection, element: element)
}
 
