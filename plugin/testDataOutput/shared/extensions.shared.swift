//Package: com.test
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



public protocol SomeInterface {
}
 
 

extension Int32 {
    public func extensionPlusOne() -> Int32 {
        return self + 1
    }
}
 
 

extension Array {
    public func estimateWork() -> Int32 {
        return self.size
    }
}
 
 

extension Array where OVERRIDE == SomeInterface {
    public func estimateWork2() -> Int32 {
        return self.size
    }
}
 
 

extension Array where OVERRIDE: SomeInterface {
    public func estimateWork3() -> Int32 {
        return self.size
    }
}
 
 

extension Array where S ==  SomeInterface {
    public func estimateWork4() -> Int32 {
        return self.size
    }
}
 
 

extension Array where S:  SomeInterface {
    public func estimateWork5() -> Int32 {
        return self.size
    }
}
 
 

extension Array {
    public func estimateWork<A>(method: A, how: (Element, A) -> Int32) -> Int32 {
        var amount = 0
        
        for item in self {
            amount += how(item, method)
        }
        return amount
    }
    public func estimateWork<A>(_ method: A, _ how: (Element, A) -> Int32) -> Int32 {
        return estimateWork(method: method, how: how)
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
 
