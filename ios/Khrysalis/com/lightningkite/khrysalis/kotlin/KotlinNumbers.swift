//
//  KotlinNumbers.swift
//  KhrysalisTemplate
//
//  Created by Joseph Ivie on 9/5/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation

public extension Float {
    func roundToInt() -> Int { return Int(rounded()) }
    func roundToLong() -> Int64 { return Int64(rounded()) }
}
public extension Double {
    func roundToInt() -> Int { return Int(rounded()) }
    func roundToLong() -> Int64 { return Int64(rounded()) }
}

infix operator >>> : BitwiseShiftPrecedence

func >>> (lhs: Int64, rhs: Int64) -> Int64 {
    return Int64(bitPattern: UInt64(bitPattern: lhs) >> UInt64(rhs))
}
func >>> (lhs: Int32, rhs: Int32) -> Int32 {
    return Int32(bitPattern: UInt32(bitPattern: lhs) >> UInt32(rhs))
}
func >>> (lhs: Int, rhs: Int) -> Int {
    return Int(Int32(bitPattern: UInt32(bitPattern: Int32(lhs)) >> UInt32(rhs)))
}
func >>> (lhs: Int16, rhs: Int16) -> Int16 {
    return Int16(bitPattern: UInt16(bitPattern: lhs) >> UInt16(rhs))
}
func >>> (lhs: Int8, rhs: Int8) -> Int8 {
    return Int8 (bitPattern: UInt8 (bitPattern: lhs) >> UInt8 (rhs))
}

public func % (left:Float, right:Float) -> Float {
    return left.truncatingRemainder(dividingBy: right)
}

public func % (left:Double, right:Double) -> Double {
    return left.truncatingRemainder(dividingBy: right)
}
