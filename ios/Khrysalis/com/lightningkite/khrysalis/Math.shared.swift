//Package: com.lightningkite.khrysalis
//Converted using Khrysalis2

import Foundation
import Khrysalis
import RxSwift
import RxRelay



extension Int32 {
    public func floorMod(other: Int32) -> Int32 {
        return ( self % other + other ) % other
    }
    public func floorMod(_ other: Int32) -> Int32 {
        return floorMod(other: other)
    }
}
 

extension Int32 {
    public func floorDiv(other: Int32) -> Int32 {
        if self < 0 {
            return self / other - 1
        } else {
            return self / other
        }
    }
    public func floorDiv(_ other: Int32) -> Int32 {
        return floorDiv(other: other)
    }
}
 
 

extension Float {
    public func floorMod(other: Float) -> Float {
        return ( self % other + other ) % other
    }
    public func floorMod(_ other: Float) -> Float {
        return floorMod(other: other)
    }
}
 

extension Float {
    public func floorDiv(other: Float) -> Float {
        if self < 0 {
            return self / other - 1
        } else {
            return self / other
        }
    }
    public func floorDiv(_ other: Float) -> Float {
        return floorDiv(other: other)
    }
}
 
 
 

extension Double {
    public func floorMod(other: Double) -> Double {
        return ( self % other + other ) % other
    }
    public func floorMod(_ other: Double) -> Double {
        return floorMod(other: other)
    }
}
 

extension Double {
    public func floorDiv(other: Double) -> Double {
        if self < 0 {
            return self / other - 1
        } else {
            return self / other
        }
    }
    public func floorDiv(_ other: Double) -> Double {
        return floorDiv(other: other)
    }
}
 
