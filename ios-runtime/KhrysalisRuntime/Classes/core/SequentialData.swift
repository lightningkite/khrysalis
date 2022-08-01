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
    
    @discardableResult
    public func putRaw<T>(_ value: T, at: Int) -> SequentialData {
        withUnsafeBytes(of: value) {
            data.replaceSubrange(at ..< at + MemoryLayout<T>.size, with: Data($0))
        }
        return self
    }
    @discardableResult
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
        return Data(data[at..<at+MemoryLayout<T>.size]).withUnsafeBytes {
            $0.load(as: T.self)
        }
    }
    public func getRaw<T>(_ type: T.Type) -> T {
        let result = getRaw(type, at: self.currentIndex)
        currentIndex += MemoryLayout<T>.size
        return result
    }
    
    public func get<T: Bittable>(_ type: T.Type, at: Int) -> T {
        return T(bitPattern: getRaw(T.BitSafe.self, at: at))
    }
    public func get<T: Bittable>(_ type: T.Type) -> T {
        return T(bitPattern: getRaw(T.BitSafe.self))
    }
    @discardableResult
    public func put<T: Bittable>(_ value: T) -> SequentialData {
        return putRaw(value.bitPattern)
    }
    @discardableResult
    public func put<T: Bittable>(_ value: T, at: Int) -> SequentialData {
        return putRaw(value.bitPattern)
    }

    public func get(length: Int, at: Int) -> Data {
        return self.data[at ..< at + length]
    }
    public func get(length: Int) -> Data {
        let result = self.data[self.currentIndex ..< self.currentIndex + length]
        self.currentIndex += length
        return result
    }
    public func get(into: inout Data) {
        into = self.data[self.currentIndex ..< self.currentIndex + into.count]
        self.currentIndex += into.count
    }
    public func get(into: inout Data, at: Int) {
        into = self.data[at ..< at + into.count]
    }
    @discardableResult
    public func put(_ data: Data) -> SequentialData {
        put(data, at: self.currentIndex)
        self.currentIndex += data.count
        return self
    }
    @discardableResult
    public func put(_ data: Data, at: Int) -> SequentialData {
        if at == self.data.count {
            self.data.append(data)
        } else {
            self.data.replaceSubrange(at ..< at + data.count, with: data)
        }
        return self
    }

    public func flip() {
        currentIndex = 0
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
