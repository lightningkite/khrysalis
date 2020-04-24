//Package: com.lightningkite.khrysalis.observables
//Converted using Khrysalis2

import Foundation
import Khrysalis
import RxSwift
import RxRelay



extension ObservableProperty {
    public var observable: Observable<Box<T>> {
        get {
            return onChange.startWith(boxWrap(value))
        }
    }
}
 

extension ObservableProperty {
    public var observableNN: Observable<T> {
        get {
            return onChange.startWith(boxWrap(value)).map{ (it) in 
                it.value
            }
        }
    }
}
 

extension ObservableProperty {
    public var onChangeNN: Observable<T> {
        get {
            return onChange.map{ (it) in 
                it.value
            }
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
 

extension ObservableProperty {
      public func addAndRunWeak<A: AnyObject>(referenceA: A, listener: @escaping (A, T) -> Void) -> Disposable {
        return observable.addWeak(referenceA: referenceA, listener: { (a, value) in 
            listener(a, value.value)
        })
    }
      public func addAndRunWeak<A: AnyObject>(_ referenceA: A, _ listener: @escaping (A, T) -> Void) -> Disposable {
        return addAndRunWeak(referenceA: referenceA, listener: listener)
    }
}
 

extension ObservableProperty {
      public func addAndRunWeak<A: AnyObject, B: AnyObject>(referenceA: A, referenceB: B, listener: @escaping (A, B, T) -> Void) -> Disposable {
        return observable.addWeak(referenceA: referenceA, referenceB: referenceB, listener: { (a, b, value) in 
            listener(a, b, value.value)
        })
    }
      public func addAndRunWeak<A: AnyObject, B: AnyObject>(_ referenceA: A, _ referenceB: B, _ listener: @escaping (A, B, T) -> Void) -> Disposable {
        return addAndRunWeak(referenceA: referenceA, referenceB: referenceB, listener: listener)
    }
}
 

extension ObservableProperty {
      public func addAndRunWeak<A: AnyObject, B: AnyObject, C: AnyObject>(referenceA: A, referenceB: B, referenceC: C, listener: @escaping (A, B, C, T) -> Void) -> Disposable {
        return observable.addWeak(referenceA: referenceA, referenceB: referenceB, referenceC: referenceC, listener: { (a, b, c, value) in 
            listener(a, b, c, value.value)
        })
    }
      public func addAndRunWeak<A: AnyObject, B: AnyObject, C: AnyObject>(_ referenceA: A, _ referenceB: B, _ referenceC: C, _ listener: @escaping (A, B, C, T) -> Void) -> Disposable {
        return addAndRunWeak(referenceA: referenceA, referenceB: referenceB, referenceC: referenceC, listener: listener)
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
 
