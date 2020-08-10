// Generated by Khrysalis Swift converter - this file will be overwritten.
// File: captureWeak.shared.kt
// Package: com.lightningkite.khrysalis
import Foundation

public func captureWeak<Z : AnyObject>(capture: Z, lambda: @escaping  (Z) -> Void) -> () -> Void {
    weak var captured: Z? = capture
    return { () -> Void in 
        let actualCaptured = captured
        if actualCaptured == nil {
            return
        }
        lambda(actualCaptured!)
    }
}

public func captureWeak<Z : AnyObject, A>(capture: Z, lambda: @escaping  (Z, A) -> Void) -> (A) -> Void {
    weak var captured: Z? = capture
    return { (a) -> Void in 
        let actualCaptured = captured
        if actualCaptured == nil {
            return
        }
        lambda(actualCaptured!, a)
    }
}

public func captureWeak<Z : AnyObject, A, B>(capture: Z, lambda: @escaping  (Z, A, B) -> Void) -> (A, B) -> Void {
    weak var captured: Z? = capture
    return { (a, b) -> Void in 
        let actualCaptured = captured
        if actualCaptured == nil {
            return
        }
        lambda(actualCaptured!, a, b)
    }
}

public func captureWeak<Z : AnyObject, A, B, C>(capture: Z, lambda: @escaping  (Z, A, B, C) -> Void) -> (A, B, C) -> Void {
    weak var captured: Z? = capture
    return { (a, b, c) -> Void in 
        let actualCaptured = captured
        if actualCaptured == nil {
            return
        }
        lambda(actualCaptured!, a, b, c)
    }
}

public func captureWeak<Z : AnyObject, A, B, C, D>(capture: Z, lambda: @escaping  (Z, A, B, C, D) -> Void) -> (A, B, C, D) -> Void {
    weak var captured: Z? = capture
    return { (a, b, c, d) -> Void in 
        let actualCaptured = captured
        if actualCaptured == nil {
            return
        }
        lambda(actualCaptured!, a, b, c, d)
    }
}

public func captureWeak<Z : AnyObject, A, B, C, D, E>(capture: Z, lambda: @escaping  (Z, A, B, C, D, E) -> Void) -> (A, B, C, D, E) -> Void {
    weak var captured: Z? = capture
    return { (a, b, c, d, e) -> Void in 
        let actualCaptured = captured
        if actualCaptured == nil {
            return
        }
        lambda(actualCaptured!, a, b, c, d, e)
    }
}


