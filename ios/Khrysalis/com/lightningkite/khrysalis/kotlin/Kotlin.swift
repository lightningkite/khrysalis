//
//  Kotlin.swift
//  PennyProfit
//
//  Created by Joseph Ivie on 12/12/18.
//  Adapted from Kotlift by Moshbit
//

import Foundation

public func TODO(_ message: String = "") -> Never {
    //Throw execption
    fatalError(message)
}

public func run<T>(_ action: ()->T)->T {
    return action()
}

public func also<T>(_ value: T, _ action: (inout T)->Void)->T {
    action(value)
    return value
}

public func takeIf<T>(_ value: T, _ condition: (T)->Bool)->T? {
    if condition(value) {
        return value
    }
    return nil
}
public func takeIf<T>(_ value: T?, _ condition: (T)->Bool)->T? {
    if let value = value, condition(value) {
        return value
    }
    return nil
}
public func takeUnless<T>(_ value: T, _ condition: (T)->Bool)->T? {
    if !condition(value) {
        return value
    }
    return nil
}
public func takeUnless<T>(_ value: T?, _ condition: (T)->Bool)->T? {
    if let value = value, !condition(value) {
        return value
    }
    return nil
}

public extension Sequence {
    func first() -> Element {
        for item in self {
            return item
        }
        fatalError("There is no first element, it's empty")
    }
    func firstOrNull() -> Element? {
        for item in self {
            return item
        }
        return nil
    }
    func last() -> Element {
        var current: Element? = nil
        for item in self {
            current = item
        }
        return current!
    }
    func lastOrNull() -> Element? {
        var current: Element? = nil
        for item in self {
            current = item
        }
        return current
    }
}

public extension Array {
    func chunked(_ count: Int) -> Array<Array<Element>> {
        var output = Array<Array<Element>>()
        var chunkIndex = 0
        while chunkIndex < size {
            var subset = Array<Element>()
            for inChunkIndex in 0..<count {
                let index = chunkIndex + inChunkIndex
                if index >= size {
                    break
                }
                subset.add(self[index])
            }
            output.add(subset)
            chunkIndex += count
        }
        return output
    }
    
    func forEachIndexed(_ action: (_ index:Int, Element) -> Void){
        for index in 0..<self.count{
            action(Int(index), self[index])
        }
    }
    
    func plus(_ element: Element) -> Array<Element> {
        var copy = self
        copy.add(element)
        return copy
    }
    func withoutIndex(_ index: Int) -> Array<Element> {
        var copy = self
        copy.removeAt(index)
        return copy
    }
    func sumByDouble(selector: (Element) -> Double)-> Double{
        var sum:Double = 0.0
        for item in self{
            sum += selector(item)
        }
        return sum
    }
    
}

public extension Array where Element: Equatable {
    mutating func remove(_ element: Element) {
        let index = self.firstIndex(where: { sub in
            sub == element
        })
        if let index = index {
            remove(at: index)
        }
    }
    static func -(first: Array<Element>, second: Element) -> Array<Element> {
        var copy = first
        copy.remove(second)
        return copy
    }
    func minus(_ element: Element) -> Array<Element> {
        var copy = self
        copy.remove(element)
        return copy
    }
}

public extension Collection {
    func find(_ predicate: (Element) -> Bool) -> Element? {
        return first(where: predicate)
    }
    static func +(first: Self, second: Element) -> Array<Element> {
        return first + [second]
    }
    func sortedBy<T: Comparable>(get: (Element) -> T) -> Array<Element> {
        return self.sorted(by: { get($0) < get($1) })
    }
    func sortedByDescending<T: Comparable>(get: (Element) -> T) -> Array<Element> {
        return self.sorted(by: { get($0) > get($1) })
    }
    func joinToString(_ separator: String, _ conversion: (Element)->String) -> String {
        return self.map(conversion).joined(separator: separator)
    }
    func joinToString(separator: String = ", ", _ conversion: (Element)->String) -> String {
        return self.map(conversion).joined(separator: separator)
    }
    func count(predicate: (Element)->Bool) -> Int {
        var current: Int = 0
        for item in self {
            if predicate(item) {
                current += 1
            }
        }
        return current
    }
}

public extension String {
    subscript(i: Int) -> Character {
        return self[index(startIndex, offsetBy: Int(i))]
    }
    
    func substring(_ startIndex: Int, _ endIndex: Int? = nil) -> String {
        let s = self.index(self.startIndex, offsetBy: Int(startIndex))
        let e = self.index(self.startIndex, offsetBy: Int(endIndex ?? self.length))
        return String(self[s..<e])
    }
    func substring(_ startIndex: Int) -> String {
        return substring(startIndex, self.length)
    }
    func contains(_ string: String) -> Bool {
        if string.isEmpty { return true }
        return self.range(of: string) != nil
    }
    func replace(_ target: String, _ withString: String) -> String {
        return self.replacingOccurrences(of: target, with: withString)
    }

    func removePrefix(_ string: String) -> String {
        if startsWith(string) {
            return substring(string.length)
        } else {
            return self
        }
    }
    
    func removeSuffix(_ string: String) -> String {
        if endsWith(string) {
            return substring(0, self.length - string.length)
        } else {
            return self
        }
    }
    
    func substringBefore(_ string: String, _ defaultTo: String? = nil) -> String {
        let index = self.indexOf(string)
        if index != -1 {
            return substring(0, index)
        } else {
            return defaultTo ?? self
        }
    }
    
    func substringAfter(_ string: String, _ defaultTo: String? = nil) -> String {
        let index = self.indexOf(string)
        let array = [1,2,3,4]
        if index != -1 {
            return substring(index + string.length)
        } else {
            return defaultTo ?? self
        }
    }
    
    func substringBeforeLast(_ string: String, _ defaultTo: String? = nil) -> String {
        let index = self.lastIndexOf(string)
        if index != -1 {
            return substring(0, index)
        } else {
            return defaultTo ?? self
        }
    }
    
    func substringAfterLast(_ string: String, _ defaultTo: String? = nil) -> String {
        let index = self.lastIndexOf(string)
        if index != -1 {
            return substring(index + string.length)
        } else {
            return defaultTo ?? self
        }
    }
    
    func remove(_ string:String) -> String{
        let temp = self
        return temp.replacingOccurrences(of: string, with:"")
    }
}


public extension StringProtocol {
    func indexOf(_ string: Self, _ startIndex: Int = 0, _ ignoreCase: Bool = true) -> Int {
        var options: String.CompareOptions = [.literal]
        if ignoreCase {
            options = [.literal, .caseInsensitive]
        }
        if let index = range(of: string, options: options)?.lowerBound {
            return Int(distance(from: self.startIndex, to: index))
        } else {
            return -1
        }
    }
    
    func lastIndexOf(_ string: Self, _ startIndex: Int = 0, _ ignoreCase: Bool = true) -> Int {
        var options: String.CompareOptions = [.literal, .backwards]
        if ignoreCase {
            options = [.literal, .caseInsensitive, .backwards]
        }
        if let index = range(of: string, options: options)?.lowerBound {
            return Int(distance(from: self.startIndex, to: index))
        } else {
            return -1
        }
        
    }
}

public class System {
    public static func currentTimeMillis() -> Int64 {
        return (Int64) (NSDate().timeIntervalSince1970 * 1000.0)
    }
}

public class Exception: Error {
    public let message: String
    public let cause: Exception?
    public init(_ message: String, _ cause: Exception? = nil) {
        self.message = message
        self.cause = cause
    }
}

public class IllegalStateException: Error {}
public class IllegalArgumentException: Error {}

public extension Error {
    func printStackTrace(){
        print(self.localizedDescription)
    }
}

public extension CaseIterable {
    /// A collection of all values of this type.
    static func values() -> Array<Self> {
        return self.allCases.toList()
    }
    static func valueOf(_ string: String) -> Self {
        return values().find { "\($0)" == string }!
    }
}

public extension StringEnum {
    var name: String {
        return "\(self)"
    }
}

public protocol StringEnum {
}

public class WeakReference<T: AnyObject> {
    weak var item: T?
    public init(_ item: T) {
        self.item = item
    }
    public func get() -> T? {
        return item
    }
}
