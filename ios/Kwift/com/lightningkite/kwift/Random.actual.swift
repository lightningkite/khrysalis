//Stub file made with Kwift 2 (by Lightning Kite)
import Foundation


//--- Random.{
public class Random {
    
    var twister: MersenneTwister
    
    //--- Random.Primary Constructor
    public init(seed: Int64) {
        twister = MersenneTwister(seed: UInt64(seed))
    }
    
    private init() {
        twister = MersenneTwister()
    }
    
    //--- Random.Companion.{
    
    //--- Random.Companion.Default
    public static let Default: Random = Random()
    
    //--- Random.Companion.}
    
    //--- Random.nextInt()
    public func nextInt() -> Int32 {
        return Int32.random(in: Int32.min...Int32.max, using: &twister)
    }
    static public func nextInt() -> Int32 {
        return Int32.random(in: Int32.min...Int32.max)
    }
    
    //--- Random.nextInt(Int)
    public func nextInt(_ until: Int32) -> Int32 {
        return Int32.random(in: 0...until, using: &twister)
    }
    public func nextInt(until: Int32) -> Int32 {
        return nextInt(until)
    }
    static public func nextInt(_ until: Int32) -> Int32 {
        return Int32.random(in: 0...until)
    }
    static public func nextInt(until: Int32) -> Int32 {
        return nextInt(until)
    }
    
    //--- Random.nextInt(Int, Int)
    public func nextInt(_ from: Int32, _ until: Int32) -> Int32 {
        return Int32.random(in: from...until, using: &twister)
    }
    public func nextInt(from: Int32, until: Int32) -> Int32 {
        return nextInt(from, until)
    }
    static public func nextInt(_ from: Int32, _ until: Int32) -> Int32 {
        return Int32.random(in: from...until)
    }
    static public func nextInt(from: Int32, until: Int32) -> Int32 {
        return nextInt(from, until)
    }
    
    //--- Random.nextLong()
    public func nextLong() -> Int64 {
        return Int64.random(in: Int64.min...Int64.max, using: &twister)
    }
    static public func nextLong() -> Int64 {
        return Int64.random(in: Int64.min...Int64.max)
    }
    
    //--- Random.nextLong(Long)
    public func nextLong(_ until: Int64) -> Int64 {
        return Int64.random(in: 0...until, using: &twister)
    }
    public func nextLong(until: Int64) -> Int64 {
        return nextLong(until)
    }
    static public func nextLong(_ until: Int64) -> Int64 {
        return Int64.random(in: 0...until)
    }
    static public func nextLong(until: Int64) -> Int64 {
        return nextLong(until)
    }

    //--- Random.nextLong(Long, Long)
    public func nextLong(_ from: Int64, _ until: Int64) -> Int64 {
        return Int64.random(in: from...until, using: &twister)
    }
    public func nextLong(from: Int64, until: Int64) -> Int64 {
        return nextLong(from, until)
    }
    static public func nextLong(_ from: Int64, _ until: Int64) -> Int64 {
        return Int64.random(in: from...until)
    }
    static public func nextLong(from: Int64, until: Int64) -> Int64 {
        return nextLong(from, until)
    }

    //--- Random.nextBoolean()
    public func nextBoolean() -> Bool {
        return Bool.random(using: &twister)
    }
    static public func nextBoolean() -> Bool {
        return Bool.random()
    }

    //--- Random.nextDouble()
    public func nextDouble() -> Double {
        return Double.random(in: 0.0..<1.0, using: &twister)
    }
    static public func nextDouble() -> Double {
        return Double.random(in: 0.0..<1.0)
    }

    //--- Random.nextDouble(Double)
    public func nextDouble(_ until: Double) -> Double {
        return Double.random(in: 0.0..<until, using: &twister)
    }
    public func nextDouble(until: Double) -> Double {
        return nextDouble(until)
    }
    static public func nextDouble(_ until: Double) -> Double {
        return Double.random(in: 0.0..<until)
    }
    static public func nextDouble(until: Double) -> Double {
        return nextDouble(until)
    }

    //--- Random.nextDouble(Double, Double)
    public func nextDouble(_ from: Double, _ until: Double) -> Double {
        return Double.random(in: from..<until, using: &twister)
    }
    public func nextDouble(from: Double, until: Double) -> Double {
        return nextDouble(from, until)
    }
    static public func nextDouble(_ from: Double, _ until: Double) -> Double {
        return Double.random(in: from..<until)
    }
    static public func nextDouble(from: Double, until: Double) -> Double {
        return nextDouble(from, until)
    }

    //--- Random.nextFloat()
    public func nextFloat() -> Float {
        return Float.random(in: 0..<1, using: &twister)
    }
    static public func nextFloat() -> Float {
        return Float.random(in: 0..<1)
    }

    //--- Random.nextFloat(Float)
    public func nextFloat(_ until: Float) -> Float {
        return Float.random(in: 0..<until, using: &twister)
    }
    public func nextFloat(until: Float) -> Float {
        return nextFloat(until)
    }
    static public func nextFloat(_ until: Float) -> Float {
        return Float.random(in: 0..<until)
    }
    static public func nextFloat(until: Float) -> Float {
        return nextFloat(until)
    }

    //--- Random.nextFloat(Float, Float)
    public func nextFloat(_ from: Float, _ until: Float) -> Float {
        return Float.random(in: from..<until, using: &twister)
    }
    public func nextFloat(from: Float, until: Float) -> Float {
        return nextFloat(from, until)
    }
    static public func nextFloat(_ from: Float, _ until: Float) -> Float {
        return Float.random(in: from..<until)
    }
    static public func nextFloat(from: Float, until: Float) -> Float {
        return nextFloat(from, until)
    }

    //--- Random.}
}

//--- Random
//
//  CwlRandom.swift
//  CwlUtils
//
//  Created by Matt Gallagher on 2016/05/17.
//  Copyright © 2016 Matt Gallagher ( https://www.cocoawithlove.com ). All rights reserved.
//
//  Permission to use, copy, modify, and/or distribute this software for any
//  purpose with or without fee is hereby granted, provided that the above
//  copyright notice and this permission notice appear in all copies.
//
//  THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
//  WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
//  MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
//  SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
//  WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
//  ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
//  IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
//
import Foundation

#if !swift(>=4.2)
    public protocol RandomNumberGenerator {
        mutating func next() -> UInt64
    }
    public struct SystemRandomNumberGenerator: RandomNumberGenerator {
        public init() {}
        public mutating func next() -> UInt64 {
            var value: UInt64 = 0
            arc4random_buf(&value, MemoryLayout<UInt64>.size)
            return value
        }
    }
#endif

public protocol RandomGenerator: RandomNumberGenerator {
    mutating func randomize(buffer: UnsafeMutableRawBufferPointer)
}

extension RandomGenerator {
    public mutating func randomize<Value>(value: inout Value) {
        withUnsafeMutablePointer(to: &value) { ptr in
            self.randomize(buffer: UnsafeMutableRawBufferPointer(start: ptr, count: MemoryLayout<Value>.size))
        }
    }
}

public struct DevRandom: RandomGenerator {
    class FileDescriptor {
        let value: CInt
        init() {
            value = open("/dev/urandom", O_RDONLY)
            precondition(value >= 0)
        }
        deinit {
            close(value)
        }
    }
    
    let fd: FileDescriptor
    public init() {
        fd = FileDescriptor()
    }
    
    public mutating func randomize(buffer: UnsafeMutableRawBufferPointer) {
        let result = read(fd.value, buffer.baseAddress, buffer.count)
        precondition(result == buffer.count)
    }
    
    public mutating func next() -> UInt64 {
        var bits: UInt64 = 0
        withUnsafeMutablePointer(to: &bits) { ptr in
            self.randomize(buffer: UnsafeMutableRawBufferPointer(start: ptr, count: MemoryLayout<UInt64>.size))
        }
        return bits
    }
}

public struct MersenneTwister: RandomNumberGenerator {
    // 312 words of storage is 13 x 6 x 4
    private typealias StateType = (
        UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64,
        UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64,
        UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64,
        UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64,
        UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64,
        UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64,

        UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64,
        UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64,
        UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64,
        UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64,
        UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64,
        UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64,

        UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64,
        UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64,
        UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64,
        UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64,
        UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64,
        UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64,

        UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64,
        UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64,
        UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64,
        UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64,
        UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64,
        UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64, UInt64
    )
    
    private var state_internal: StateType = (
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,

        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,

        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,

        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    )
    private var index: Int
    private static let stateCount: Int = 312
    
    public init() {
        var dr = DevRandom()
        dr.randomize(value: &state_internal)
        index = MersenneTwister.stateCount
    }
    
    public init(seed: UInt64) {
        index = MersenneTwister.stateCount
        withUnsafeMutablePointer(to: &state_internal) { $0.withMemoryRebound(to: UInt64.self, capacity: MersenneTwister.stateCount) { state in
            state[0] = seed
            for i in 1..<MersenneTwister.stateCount {
                state[i] = 6364136223846793005 &* (state[i &- 1] ^ (state[i &- 1] >> 62)) &+ UInt64(i)
            }
        } }
    }

    public mutating func next() -> UInt64 {
        if index == MersenneTwister.stateCount {
            withUnsafeMutablePointer(to: &state_internal) { $0.withMemoryRebound(to: UInt64.self, capacity: MersenneTwister.stateCount) { state in
                let n = MersenneTwister.stateCount
                let m = n / 2
                let a: UInt64 = 0xB5026F5AA96619E9
                let lowerMask: UInt64 = (1 << 31) - 1
                let upperMask: UInt64 = ~lowerMask
                var (i, j, stateM) = (0, m, state[m])
                repeat {
                    let x1 = (state[i] & upperMask) | (state[i &+ 1] & lowerMask)
                    state[i] = state[i &+ m] ^ (x1 >> 1) ^ ((state[i &+ 1] & 1) &* a)
                    let x2 = (state[j] & upperMask) | (state[j &+ 1] & lowerMask)
                    state[j] = state[j &- m] ^ (x2 >> 1) ^ ((state[j &+ 1] & 1) &* a)
                    (i, j) = (i &+ 1, j &+ 1)
                } while i != m &- 1
                
                let x3 = (state[m &- 1] & upperMask) | (stateM & lowerMask)
                state[m &- 1] = state[n &- 1] ^ (x3 >> 1) ^ ((stateM & 1) &* a)
                let x4 = (state[n &- 1] & upperMask) | (state[0] & lowerMask)
                state[n &- 1] = state[m &- 1] ^ (x4 >> 1) ^ ((state[0] & 1) &* a)
            } }
            
            index = 0
        }
        
        var result = withUnsafePointer(to: &state_internal) { $0.withMemoryRebound(to: UInt64.self, capacity: MersenneTwister.stateCount) { ptr in
            return ptr[index]
        } }
        index = index &+ 1

        result ^= (result >> 29) & 0x5555555555555555
        result ^= (result << 17) & 0x71D67FFFEDA60000
        result ^= (result << 37) & 0xFFF7EEE000000000
        result ^= result >> 43

        return result
    }
}
