//
//  SequentialData.swift
//  Khrysalis
//
//  Created by Shane Thompson on 7/21/22.
//

import Foundation

public class SequentialData {
    public var data: Data
    public var currentIndex: Int
    
    public init(capacity: Int) {
        data = Data(capacity: capacity)
        currentIndex = 0
    }
    public init(data: Data) {
        self.data = data
        currentIndex = 0
    }
    
    public func putRaw<T>(_ value: T, at: Int) -> SequentialData {
        withUnsafeBytes(of: value) {
            data.replaceSubrange(at ..< at + MemoryLayout<T>.size, with: Data($0))
        }
        return self
    }
    public func putRaw<T>(_ value: T) -> SequentialData {
        if currentIndex == data.count {
            withUnsafeBytes(of: value) {
                data.append(contentsOf: Data($0))
            }
            currentIndex += MemoryLayout<T>.size
        } else {
            let at = currentIndex
            withUnsafeBytes(of: value) {
                data.replaceSubrange(at ..< at + MemoryLayout<T>.size, with: Data($0))
            }
            currentIndex += MemoryLayout<T>.size
        }
        return self
    }
    public func getRaw<T>(_ type: T.Type, at: Int) -> T {
        return data.withUnsafeBytes {
            $0.load(fromByteOffset: at, as: T.self)
        }
    }
    public func getRaw<T>(_ type: T.Type) -> T {
        let at = currentIndex
        let result = data.withUnsafeBytes {
            $0.load(fromByteOffset: at, as: T.self)
        }
        currentIndex += MemoryLayout<T>.size
        return result
    }
    
    public func get<T: Bittable>(_ type: T.Type, at: Int) -> T {
        return T(bitPattern: getRaw(T.BitSafe.self, at: at))
    }
    public func get<T: Bittable>(_ type: T.Type) -> T {
        return T(bitPattern: getRaw(T.BitSafe.self))
    }
    public func put<T: Bittable>(_ value: T) -> SequentialData {
        return putRaw(value.bitPattern)
    }
    public func put<T: Bittable>(_ value: T, at: Int) -> SequentialData {
        return putRaw(value.bitPattern)
    }
}

public protocol Bittable {
    associatedtype BitSafe
    var bitPattern: BitSafe { get }
    init(bitPattern: BitSafe)
}
extension Int8: Bittable {
    public typealias BitSafe = Self
    public var bitPattern: BitSafe { return bigEndian }
    public init(bitPattern: BitSafe) { self = bitPattern.bigEndian }
}
extension Int16: Bittable {
    public typealias BitSafe = Self
    public var bitPattern: BitSafe { return bigEndian }
    public init(bitPattern: BitSafe) { self = bitPattern.bigEndian }
}
extension Int32: Bittable {
    public typealias BitSafe = Self
    public var bitPattern: BitSafe { return bigEndian }
    public init(bitPattern: BitSafe) { self = bitPattern.bigEndian }
}
extension Int64: Bittable {
    public typealias BitSafe = Self
    public var bitPattern: BitSafe { return bigEndian }
    public init(bitPattern: BitSafe) { self = bitPattern.bigEndian }
}
extension Int: Bittable {
    public typealias BitSafe = Self
    public var bitPattern: BitSafe { return bigEndian }
    public init(bitPattern: BitSafe) { self = bitPattern.bigEndian }
}
extension UInt8: Bittable {
    public typealias BitSafe = Self
    public var bitPattern: BitSafe { return bigEndian }
    public init(bitPattern: BitSafe) { self = bitPattern.bigEndian }
}
extension UInt16: Bittable {
    public typealias BitSafe = Self
    public var bitPattern: BitSafe { return bigEndian }
    public init(bitPattern: BitSafe) { self = bitPattern.bigEndian }
}
extension UInt32: Bittable {
    public typealias BitSafe = Self
    public var bitPattern: BitSafe { return bigEndian }
    public init(bitPattern: BitSafe) { self = bitPattern.bigEndian }
}
extension UInt64: Bittable {
    public typealias BitSafe = Self
    public var bitPattern: BitSafe { return bigEndian }
    public init(bitPattern: BitSafe) { self = bitPattern.bigEndian }
}
extension UInt: Bittable {
    public typealias BitSafe = Self
    public var bitPattern: BitSafe { return bigEndian }
    public init(bitPattern: BitSafe) { self = bitPattern.bigEndian }
}
extension Bool: Bittable {
    public typealias BitSafe = UInt8
    public var bitPattern: BitSafe { return self ? 0x1 : 0x0 }
    public init(bitPattern: BitSafe) { self = bitPattern != 0x0 }
}
extension Float: Bittable {
    public typealias BitSafe = UInt32
}
extension Double: Bittable {
    public typealias BitSafe = UInt64
}
