//Package: com.lightningkite.kwift.shared
//Converted using Kwift2

import Foundation



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
 
