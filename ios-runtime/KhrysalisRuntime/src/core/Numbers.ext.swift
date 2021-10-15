import Foundation


public extension SignedInteger {
    mutating func postInc() -> Self {
        let result = self
        self += 1
        return result
    }
    mutating func postDec() -> Self {
        let result = self
        self -= 1
        return result
    }
    mutating func preInc() -> Self {
        self += 1
        return self
    }
    mutating func preDec() -> Self {
        self -= 1
        return self
    }
}

public extension Float {
    func roundToInt() -> Int { return Int(rounded()) }
    func roundToLong() -> Int64 { return Int64(rounded()) }
}
public extension Double {
    func roundToInt() -> Int { return Int(rounded()) }
    func roundToLong() -> Int64 { return Int64(rounded()) }
}

infix operator >>> : BitwiseShiftPrecedence

public func >>> (lhs: Int64, rhs: Int64) -> Int64 {
    return Int64(bitPattern: UInt64(bitPattern: lhs) >> UInt64(rhs))
}
public func >>> (lhs: Int32, rhs: Int32) -> Int32 {
    return Int32(bitPattern: UInt32(bitPattern: lhs) >> UInt32(rhs))
}
public func >>> (lhs: Int, rhs: Int) -> Int {
    return Int(Int32(bitPattern: UInt32(bitPattern: Int32(lhs)) >> UInt32(rhs)))
}
public func >>> (lhs: Int16, rhs: Int16) -> Int16 {
    return Int16(bitPattern: UInt16(bitPattern: lhs) >> UInt16(rhs))
}
public func >>> (lhs: Int8, rhs: Int8) -> Int8 {
    return Int8 (bitPattern: UInt8 (bitPattern: lhs) >> UInt8 (rhs))
}

public func % (left:Float, right:Float) -> Float {
    return left.truncatingRemainder(dividingBy: right)
}

public func % (left:Double, right:Double) -> Double {
    return left.truncatingRemainder(dividingBy: right)
}

public extension Int {
    func floorMod(other: Int) -> Int { return (self % other + other) % other }
}
public extension Int {
    func floorDiv(other: Int) -> Int {
        if self < 0 {
            return self / other - 1
        } else {
            return self / other
        }
    }
}

public extension Float {
    func floorMod(other: Float) -> Float { return (self % other + other) % other }
}
public extension Float {
    func floorDiv(other: Float) -> Float {
        if self < 0 {
            return self / other - 1
        } else {
            return self / other
        }
    }
}


public extension Double {
    func floorMod(other: Double) -> Double { return (self % other + other) % other }
}
public extension Double {
    func floorDiv(other: Double) -> Double {
        if self < 0 {
            return self / other - 1
        } else {
            return self / other
        }
    }
}