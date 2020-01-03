

import Foundation

public class Random{

    static func nextInt() -> Int32 {return  Int32.random(in: Int32.min...Int32.max)}
    static func nextInt(until: Int32) -> Int32 { return Int32.random(in: 0..<until)}
    static func nextInt(from: Int32, until: Int32) -> Int32 { return Int32.random(in:from..<until)}

    static func nextLong() -> Int64 { return Int64.random(in: Int64.min...Int64.max)}
    static func nextLong(until: Int64) -> Int64 { return Int64.random(in: 0..<until)}
    static func nextLong(from: Int64, until: Int64) -> Int64 { return Int64.random(in: from..<until)}

    static func nextBoolean() -> Bool { return Bool.random()}

    static func nextDouble() -> Double { return Double.random(in: 0.0..<1.0)}
    static func nextDouble(until: Double) -> Double { return Double.random(in: 0.0..<until)}
    static func nextDouble(from: Double, until: Double) -> Double { return Double.random(in: from..<until)}

    static func nextFloat() -> Float { return Float.random(in: 0..<1)}
}
