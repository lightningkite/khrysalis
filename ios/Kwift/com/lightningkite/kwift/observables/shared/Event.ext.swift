//Package: com.lightningkite.kwift.observables.shared
//Converted using Kwift2

import Foundation



extension Event {
    @discardableResult public func addWeak<A: AnyObject>(referenceA: A, listener: @escaping (A, T) -> Void) -> Close {
        weak var weakA = referenceA
        return self.add{ (item) in 
            var a = weakA
            if let a = a {
                listener(a, item)
                return false
            } else {
                return true
            }
        }
    }
    @discardableResult public func addWeak<A: AnyObject>(_ referenceA: A, _ listener: @escaping (A, T) -> Void) -> Close {
        return addWeak(referenceA: referenceA, listener: listener)
    }
}
 

extension Event {
    @discardableResult public func addWeak<A: AnyObject, B: AnyObject>(referenceA: A, referenceB: B, listener: @escaping (A, B, T) -> Void) -> Close {
        weak var weakA = referenceA
        weak var weakB = referenceB
        return self.add{ (item) in 
            var a = weakA
            var b = weakB
            if let a = a, let b = b {
                listener(a, b, item)
                return false
            } else {
                return true
            }
        }
    }
    @discardableResult public func addWeak<A: AnyObject, B: AnyObject>(_ referenceA: A, _ referenceB: B, _ listener: @escaping (A, B, T) -> Void) -> Close {
        return addWeak(referenceA: referenceA, referenceB: referenceB, listener: listener)
    }
}
 

extension Event {
    @discardableResult public func addWeak<A: AnyObject, B: AnyObject, C: AnyObject>(referenceA: A, referenceB: B, referenceC: C, listener: @escaping (A, B, C, T) -> Void) -> Close {
        weak var weakA = referenceA
        weak var weakB = referenceB
        weak var weakC = referenceC
        return self.add{ (item) in 
            var a = weakA
            var b = weakB
            var c = weakC
            if let a = a, let b = b, let c = c {
                listener(a, b, c, item)
                return false
            } else {
                return true
            }
        }
    }
    @discardableResult public func addWeak<A: AnyObject, B: AnyObject, C: AnyObject>(_ referenceA: A, _ referenceB: B, _ referenceC: C, _ listener: @escaping (A, B, C, T) -> Void) -> Close {
        return addWeak(referenceA: referenceA, referenceB: referenceB, referenceC: referenceC, listener: listener)
    }
}
 
 

extension Event {
    @discardableResult public func addAndRunWeak<A: AnyObject>(referenceA: A, value: T, listener: @escaping (A, T) -> Void) -> Close {
        listener(referenceA, value)
        return addWeak(referenceA, listener)
    }
    @discardableResult public func addAndRunWeak<A: AnyObject>(_ referenceA: A, _ value: T, _ listener: @escaping (A, T) -> Void) -> Close {
        return addAndRunWeak(referenceA: referenceA, value: value, listener: listener)
    }
}
 

extension Event {
    @discardableResult public func addAndRunWeak<A: AnyObject, B: AnyObject>(referenceA: A, referenceB: B, value: T, listener: @escaping (A, B, T) -> Void) -> Close {
        listener(referenceA, referenceB, value)
        return addWeak(referenceA, referenceB, listener)
    }
    @discardableResult public func addAndRunWeak<A: AnyObject, B: AnyObject>(_ referenceA: A, _ referenceB: B, _ value: T, _ listener: @escaping (A, B, T) -> Void) -> Close {
        return addAndRunWeak(referenceA: referenceA, referenceB: referenceB, value: value, listener: listener)
    }
}
 

extension Event {
    @discardableResult public func addAndRunWeak<A: AnyObject, B: AnyObject, C: AnyObject>(referenceA: A, referenceB: B, referenceC: C, value: T, listener: @escaping (A, B, C, T) -> Void) -> Close {
        listener(referenceA, referenceB, referenceC, value)
        return addWeak(referenceA, referenceB, referenceC, listener)
    }
    @discardableResult public func addAndRunWeak<A: AnyObject, B: AnyObject, C: AnyObject>(_ referenceA: A, _ referenceB: B, _ referenceC: C, _ value: T, _ listener: @escaping (A, B, C, T) -> Void) -> Close {
        return addAndRunWeak(referenceA: referenceA, referenceB: referenceB, referenceC: referenceC, value: value, listener: listener)
    }
}
 
