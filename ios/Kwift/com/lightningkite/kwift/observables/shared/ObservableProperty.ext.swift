//Package: com.lightningkite.kwift.observables.shared
//Converted using Kwift2

import Foundation



extension ObservableProperty {
    @discardableResult public func addAndRunWeak<A: AnyObject>(referenceA: A, listener: @escaping (A, T) -> Void) -> Close {
        return onChange.addAndRunWeak(referenceA: referenceA, value: value, listener: listener)
    }
    @discardableResult public func addAndRunWeak<A: AnyObject>(_ referenceA: A, _ listener: @escaping (A, T) -> Void) -> Close {
        return addAndRunWeak(referenceA: referenceA, listener: listener)
    }
}
 

extension ObservableProperty {
    @discardableResult public func addAndRunWeak<A: AnyObject, B: AnyObject>(referenceA: A, referenceB: B, listener: @escaping (A, B, T) -> Void) -> Close {
        return onChange.addAndRunWeak(referenceA: referenceA, referenceB: referenceB, value: value, listener: listener)
    }
    @discardableResult public func addAndRunWeak<A: AnyObject, B: AnyObject>(_ referenceA: A, _ referenceB: B, _ listener: @escaping (A, B, T) -> Void) -> Close {
        return addAndRunWeak(referenceA: referenceA, referenceB: referenceB, listener: listener)
    }
}
 

extension ObservableProperty {
    @discardableResult public func addAndRunWeak<A: AnyObject, B: AnyObject, C: AnyObject>(referenceA: A, referenceB: B, referenceC: C, listener: @escaping (A, B, C, T) -> Void) -> Close {
        return onChange.addAndRunWeak(referenceA: referenceA, referenceB: referenceB, referenceC: referenceC, value: value, listener: listener)
    }
    @discardableResult public func addAndRunWeak<A: AnyObject, B: AnyObject, C: AnyObject>(_ referenceA: A, _ referenceB: B, _ referenceC: C, _ listener: @escaping (A, B, C, T) -> Void) -> Close {
        return addAndRunWeak(referenceA: referenceA, referenceB: referenceB, referenceC: referenceC, listener: listener)
    }
}
 
 

public func includes<E>(collection: MutableObservableProperty<Set<E>>, element: E) -> MutableObservableProperty<Bool> {
    return collection.map{ (it) in 
        it.contains(element)
    }.withWrite{ (it) in 
        if it {
            collection.value = collection.value - element
        } else {
            collection.value = collection.value + element
        }
    }
}
public func includes<E>(_ collection: MutableObservableProperty<Set<E>>, _ element: E) -> MutableObservableProperty<Bool> {
    return includes(collection: collection, element: element)
}
 
