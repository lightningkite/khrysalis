

import Foundation

public class Random{

        static func nextBits(bitCount: Int) -> Int = I
        static func nextInt() -> Int32 = Int32.random(in: Int32.min...Int32.max)
        static func nextInt(until: Int) -> Int32 = Int32.random(in: 0..<until)
        static func nextInt(from: Int, until: Int) -> Int32 = Int32.random(in: from..<until)

        static func nextLong() -> Int64 = Int64.random(in: Int64.min...Int64.max)
        static func nextLong(until: Int64) -> Int64 = Int64.random(in: 0..<until)
        static func nextLong(from: Int64, until: Int64) -> Int64 = Int64.random(in: from..<until)

        static func nextBoolean() -> Bool = Bool.random()

        static func nextDouble() -> Double = Double.random(in: 0.0..<1.0)
        static func nextDouble(until: Double) ->  = Double.random(in: 0.0..<until)
        static func nextDouble(from: Double, until: Double) -> Double =  Double.random(in: from..<until)

        static func nextFloat() -> Float =  Float.random(in: 0..<1)

        static func nextBytes(array: ByteArray): ByteArray = defaultRandom.nextBytes(array)
        static func nextBytes(size: Int): ByteArray = defaultRandom.nextBytes(size)
        static func nextBytes(array: ByteArray, fromIndex: Int, toIndex: Int): ByteArray = defaultRandom.nextBytes(array, fromIndex, toIndex)

}