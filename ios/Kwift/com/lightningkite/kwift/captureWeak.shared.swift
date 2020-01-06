//Package: com.lightningkite.kwift
//Converted using Kwift2

import Foundation



public func captureWeak<Z: AnyObject>(capture: Z, lambda: @escaping (Z) -> Void) -> () -> Void {
    weak var captured = capture
    return { () in 
        var actualCaptured = captured
        if actualCaptured == nil {
            return
        }
        lambda(actualCaptured!)
    }
}
public func captureWeak<Z: AnyObject>(_ capture: Z, _ lambda: @escaping (Z) -> Void) -> () -> Void {
    return captureWeak(capture: capture, lambda: lambda)
}
 
 

public func captureWeak<Z: AnyObject, A>(capture: Z, lambda: @escaping (Z, A) -> Void) -> (A) -> Void {
    weak var captured = capture
    return { (a) in 
        var actualCaptured = captured
        if actualCaptured == nil {
            return
        }
        lambda(actualCaptured!, a)
    }
}
public func captureWeak<Z: AnyObject, A>(_ capture: Z, _ lambda: @escaping (Z, A) -> Void) -> (A) -> Void {
    return captureWeak(capture: capture, lambda: lambda)
}
 
 

public func captureWeak<Z: AnyObject, A, B>(capture: Z, lambda: @escaping (Z, A, B) -> Void) -> (A, B) -> Void {
    weak var captured = capture
    return { (a, b) in 
        var actualCaptured = captured
        if actualCaptured == nil {
            return
        }
        lambda(actualCaptured!, a, b)
    }
}
public func captureWeak<Z: AnyObject, A, B>(_ capture: Z, _ lambda: @escaping (Z, A, B) -> Void) -> (A, B) -> Void {
    return captureWeak(capture: capture, lambda: lambda)
}
 
 

public func captureWeak<Z: AnyObject, A, B, C>(capture: Z, lambda: @escaping (Z, A, B, C) -> Void) -> (A, B, C) -> Void {
    weak var captured = capture
    return { (a, b, c) in 
        var actualCaptured = captured
        if actualCaptured == nil {
            return
        }
        lambda(actualCaptured!, a, b, c)
    }
}
public func captureWeak<Z: AnyObject, A, B, C>(_ capture: Z, _ lambda: @escaping (Z, A, B, C) -> Void) -> (A, B, C) -> Void {
    return captureWeak(capture: capture, lambda: lambda)
}
 
 

public func captureWeak<Z: AnyObject, A, B, C, D>(capture: Z, lambda: @escaping (Z, A, B, C, D) -> Void) -> (A, B, C, D) -> Void {
    weak var captured = capture
    return { (a, b, c, d) in 
        var actualCaptured = captured
        if actualCaptured == nil {
            return
        }
        lambda(actualCaptured!, a, b, c, d)
    }
}
public func captureWeak<Z: AnyObject, A, B, C, D>(_ capture: Z, _ lambda: @escaping (Z, A, B, C, D) -> Void) -> (A, B, C, D) -> Void {
    return captureWeak(capture: capture, lambda: lambda)
}
 
 

public func captureWeak<Z: AnyObject, A, B, C, D, E>(capture: Z, lambda: @escaping (Z, A, B, C, D, E) -> Void) -> (A, B, C, D, E) -> Void {
    weak var captured = capture
    return { (a, b, c, d, e) in 
        var actualCaptured = captured
        if actualCaptured == nil {
            return
        }
        lambda(actualCaptured!, a, b, c, d, e)
    }
}
public func captureWeak<Z: AnyObject, A, B, C, D, E>(_ capture: Z, _ lambda: @escaping (Z, A, B, C, D, E) -> Void) -> (A, B, C, D, E) -> Void {
    return captureWeak(capture: capture, lambda: lambda)
}
 
