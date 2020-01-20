//Package: com.lightningkite.kwift.rx
//Converted using Kwift2

import Foundation
import RxSwift
import RxRelay



extension Observable where Element: Any {
    public func add(listener: @escaping (Element) -> Bool) -> Disposable {
        var disposable: Disposable?  = nil
        var disp = self.subscribeBy(onNext: { (item) in 
            if listener(item) {
                disposable?.dispose()
            }
        })
        disposable = disp
        return disp
    }
}
 
 

extension Observable where Element: Any {
    public func addWeak<A: AnyObject>(referenceA: A, listener: @escaping (A, Element) -> Void) -> Disposable {
        var disposable: Disposable?  = nil
        weak var weakA: A?  = referenceA
        var disp = self.subscribeBy(onNext: { (item) in 
            var a = weakA
            if let a = a {
                listener(a, item)
            } else {
                disposable?.dispose()
            }
        })
        disposable = disp
        return disp
    }
    public func addWeak<A: AnyObject>(_ referenceA: A, _ listener: @escaping (A, Element) -> Void) -> Disposable {
        return addWeak(referenceA: referenceA, listener: listener)
    }
}
 
 

extension Observable where Element: Any {
    public func addWeak<A: AnyObject, B: AnyObject>(referenceA: A, referenceB: B, listener: @escaping (A, B, Element) -> Void) -> Disposable {
        var disposable: Disposable?  = nil
        weak var weakA: A?  = referenceA
        weak var weakB: B?  = referenceB
        var disp = self.subscribeBy(onNext: { (item) in 
            var a = weakA
            var b = weakB
            if let a = a, let b = b {
                listener(a, b, item)
            } else {
                disposable?.dispose()
            }
        })
        disposable = disp
        return disp
    }
    public func addWeak<A: AnyObject, B: AnyObject>(_ referenceA: A, _ referenceB: B, _ listener: @escaping (A, B, Element) -> Void) -> Disposable {
        return addWeak(referenceA: referenceA, referenceB: referenceB, listener: listener)
    }
}
 
 
 

extension Observable where Element: Any {
    public func addWeak<A: AnyObject, B: AnyObject, C: AnyObject>(referenceA: A, referenceB: B, referenceC: C, listener: @escaping (A, B, C, Element) -> Void) -> Disposable {
        var disposable: Disposable?  = nil
        weak var weakA: A?  = referenceA
        weak var weakB: B?  = referenceB
        weak var weakC: C?  = referenceC
        var disp = self.subscribeBy(onNext: { (item) in 
            var a = weakA
            var b = weakB
            var c = weakC
            if let a = a, let b = b, let c = c {
                listener(a, b, c, item)
            } else {
                disposable?.dispose()
            }
        })
        disposable = disp
        return disp
    }
    public func addWeak<A: AnyObject, B: AnyObject, C: AnyObject>(_ referenceA: A, _ referenceB: B, _ referenceC: C, _ listener: @escaping (A, B, C, Element) -> Void) -> Disposable {
        return addWeak(referenceA: referenceA, referenceB: referenceB, referenceC: referenceC, listener: listener)
    }
}
 
