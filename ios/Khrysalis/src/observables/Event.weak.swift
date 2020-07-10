//
//  Event.weak.swift
//  Alamofire
//
//  Created by Brady on 6/15/20.
//

import Foundation
import RxSwift
import RxRelay


extension ObservableProperty {
      public func addAndRunWeak<A: AnyObject>(referenceA: A, listener: @escaping (A, T) -> Void) -> Disposable {
        return observable.addWeak(referenceA: referenceA, listener: { (a, value) in
            listener(a, value)
        })
    }
      public func addAndRunWeak<A: AnyObject>(_ referenceA: A, _ listener: @escaping (A, T) -> Void) -> Disposable {
        return addAndRunWeak(referenceA: referenceA, listener: listener)
    }
}


extension ObservableProperty {
      public func addAndRunWeak<A: AnyObject, B: AnyObject>(referenceA: A, referenceB: B, listener: @escaping (A, B, T) -> Void) -> Disposable {
        return observable.addWeak(referenceA: referenceA, referenceB: referenceB, listener: { (a, b, value) in
            listener(a, b, value)
        })
    }
      public func addAndRunWeak<A: AnyObject, B: AnyObject>(_ referenceA: A, _ referenceB: B, _ listener: @escaping (A, B, T) -> Void) -> Disposable {
        return addAndRunWeak(referenceA: referenceA, referenceB: referenceB, listener: listener)
    }
}


extension ObservableProperty {
      public func addAndRunWeak<A: AnyObject, B: AnyObject, C: AnyObject>(referenceA: A, referenceB: B, referenceC: C, listener: @escaping (A, B, C, T) -> Void) -> Disposable {
        return observable.addWeak(referenceA: referenceA, referenceB: referenceB, referenceC: referenceC, listener: { (a, b, c, value) in
            listener(a, b, c, value)
        })
    }
      public func addAndRunWeak<A: AnyObject, B: AnyObject, C: AnyObject>(_ referenceA: A, _ referenceB: B, _ referenceC: C, _ listener: @escaping (A, B, C, T) -> Void) -> Disposable {
        return addAndRunWeak(referenceA: referenceA, referenceB: referenceB, referenceC: referenceC, listener: listener)
    }
}
