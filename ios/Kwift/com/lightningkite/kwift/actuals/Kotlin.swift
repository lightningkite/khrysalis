//
//  Kotlin.swift
//  PennyProfit
//
//  Created by Joseph Ivie on 12/12/18.
//  Adapted from Kotlift by Moshbit
//

import Foundation

public func TODO(_ message: String = ""){
    //Throw execption
    fatalError(message)
}

public extension Sequence {
    func mapNotNull<OUT>(transform: (Element)->OUT?) -> Array<OUT> {
        var newArray = Array<OUT>()
        for element in self {
            if let transformed = transform(element) {
                newArray.append(transformed)
            }
        }
        return newArray
    }
    func mapIndexed<OUT>(transform: (Int32, Element)->OUT) -> Array<OUT> {
        var newArray = Array<OUT>()
        var index: Int32 = 0
        for element in self {
            newArray.append(transform(index, element))
            index += 1
        }
        return newArray
    }
    func any(predicate: (Element)->Bool) -> Bool {
        for element in self {
            if predicate(element) {
                return true
            }
        }
        return false
    }
    func all(predicate: (Element)->Bool) -> Bool {
        for element in self {
            if !predicate(element) {
                return false
            }
        }
        return true
    }
    func none(predicate: (Element)->Bool) -> Bool {
        for element in self {
            if predicate(element) {
                return false
            }
        }
        return true
    }
}

public extension Array {
    func isEmpty() -> Bool {
        return self.isEmpty
    }
    func isNotEmpty() -> Bool {
        return !self.isEmpty
    }
    var size: Int32 { return Int32(self.count) }
    mutating func add(_ element: Element) {
        self.append(element)
    }
    mutating func add(_ index: Int32, _ element: Element) {
        self.insert(element, at: Int(index))
    }
    mutating func addAll(_ other: Array) {
        for element in other {
            self.append(element)
        }
    }
    func get(_ index: Int32) -> Element {
        return self[Int(index)]
    }
    mutating func clear() {
        self.removeAll()
    }
    mutating func removeAt(_ index: Int32) {
        remove(at: Int(index))
    }
    var lastIndex: Int32 { return Int32(self.count - 1) }
    
    func first() -> Element {
        return first!
    }
    func last() -> Element {
        return last!
    }
    func firstOrNull() -> Element? {
        return first
    }
    func lastOrNull() -> Element? {
        return last
    }
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
    func indexOf(_ element: Element) -> Int32 {
        return Int32(self.firstIndex(where: { sub in
            sub == element
        }) ?? -1)
    }
    static func -(first: Array<Element>, second: Element) -> Array<Element> {
        var copy = first
        copy.remove(second)
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
}

public extension Array {
    subscript(index: Int32) -> Element {
        return self[Int(index)]
    }
}

public extension Array where Element == String {
    func joinToString(_ between: String = ", ") -> String {
        return self.joined(separator: between)
    }
    
}

public extension Dictionary {
    var size: Int32 { return Int32(self.count) }
    mutating func put(_ key: Key, _ value: Value) {
        self[key] = value
    }
    mutating func putAll(_ other: Dictionary) {
        for (key, value) in other {
            self.updateValue(value, forKey: key)
        }
    }
    func isEmpty() -> Bool {
        return self.isEmpty
    }
    mutating func clear() {
        self.removeAll()
    }
    struct Entry<Key, Value> {
        let key: Key
        let value: Value
    }
    func filter(_ predicate: (Entry<Key, Value>) -> Bool) -> Dictionary<Key, Value> {
        return self.filter { (key, value) -> Bool in
            predicate(Entry(key: key, value: value))
        }
    }
}

public extension Set {
    var size: Int32 { return Int32(self.count) }
    mutating func add(_ element: Element) {
        self.insert(element)
    }
    mutating func addAll(_ other: Set) {
        for element in other {
            self.insert(element)
        }
    }
    func isEmpty() -> Bool {
        return self.isEmpty
    }
    mutating func clear() {
        self.removeAll()
    }
}

public extension String {
    var length: Int32 { return Int32(count) }
    
    func substring(_ startIndex: Int32, _ endIndex: Int32? = nil) -> String {
        let s = self.index(self.startIndex, offsetBy: Int(startIndex))
        let e = self.index(self.startIndex, offsetBy: Int(endIndex ?? self.length))
        return String(self[s..<e])
    }
    func substring(_ startIndex: Int32) -> String {
        return substring(startIndex, self.length)
    }
    func contains(_ string: String) -> Bool {
        return self.range(of: string) != nil
    }
    func replace(_ target: String, _ withString: String) -> String {
        return self.replacingOccurrences(of: target, with: withString)
    }
    func isEmpty() -> Bool {
        return isEmpty
    }
    func isBlank() -> Bool {
        return isEmpty || self.allSatisfy { c in c == " " || c == "\n" }
    }
    func isNotEmpty() -> Bool {
        return !isEmpty
    }
    func isNotBlank() -> Bool {
        return !isEmpty && !self.allSatisfy { c in c == " " || c == "\n" }
    }
    
    func trim() -> String {
        return self.trimmingCharacters(in: .whitespaces)
    }
    
    func trim(_ character: Character, _ characters: Character...) -> String {
        var string: String = String(character)
        for c in characters {
            string += String(c)
        }
        return self.trimmingCharacters(in: CharacterSet(charactersIn: string))
    }
    
    func trimIndent() -> String {
        return self
    }
    
    func toLowerCase() -> String {
        return self.lowercased()
    }
    
    func toUpperCase() -> String {
        return self.uppercased()
    }
    
    func startsWith(_ string: String) -> Bool {
        return starts(with: string)
    }
    
    func endsWith(_ string: String) -> Bool {
        let substring = self.substring(self.length - string.length)
        return substring == string
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
    
}


public extension StringProtocol {
    func indexOf(_ string: Self, _ startIndex: Int32 = 0, _ ignoreCase: Bool = true) -> Int32 {
        var options: String.CompareOptions = [.literal]
        if ignoreCase {
            options = [.literal, .caseInsensitive]
        }
        if let index = range(of: string, options: options)?.lowerBound {
            return Int32(distance(from: self.startIndex, to: index))
        } else {
            return -1
        }
    }
    
    func lastIndexOf(_ string: Self, _ startIndex: Int32 = 0, _ ignoreCase: Bool = true) -> Int32 {
        var options: String.CompareOptions = [.literal, .backwards]
        if ignoreCase {
            options = [.literal, .caseInsensitive, .backwards]
        }
        if let index = range(of: string, options: options)?.lowerBound {
            return Int32(distance(from: self.startIndex, to: index))
        } else {
            return -1
        }
        
    }
}


public protocol _StringType { }
extension String: _StringType { }
public extension Array where Element: _StringType {
    func joinToString(_ separator: String = "") -> String {
        var retval = ""
        var first = true
        for rawObject in self {
            let element = rawObject as! String
            if (first) {
                retval += element
            } else {
                retval += separator + element
            }
            first = false
        }
        return retval
    }
}

public extension CustomStringConvertible {
    func toString() -> String {
        return String(describing: self)
    }
}

public extension Optional where Wrapped: CustomStringConvertible {
    func toString() -> String {
        if let thing = self {
            return thing.toString()
        } else {
            return "null"
        }
    }
}

public extension Optional where Wrapped: NSObject {
    func toString() -> String {
        if let thing = self {
            return thing.toString()
        } else {
            return "null"
        }
    }
}

public extension Optional where Wrapped: FixedWidthInteger {
    func toString() -> String {
        if let thing = self {
            return thing.toString()
        } else {
            return "null"
        }
    }
}

public extension Optional where Wrapped == String {
    func toString() -> String {
        if let thing = self {
            return thing.toString()
        } else {
            return "null"
        }
    }
}

public extension NSObject {
    func toString() -> String {
        return String(describing: self)
    }
}

public extension FixedWidthInteger {
    func toString() -> String {
        return String(describing: self)
    }
}

public extension String {
    func toString() -> String {
        return String(describing: self)
    }
}

public class System {
    public static func currentTimeMillis() -> Int64 {
        return (Int64) (NSDate().timeIntervalSince1970 * 1000.0)
    }
}

public enum KotlinStyleError : Error {
    case Exception(message: String)
    case IllegalStateException(message: String)
    case IllegalArgumentException(message: String)
}

public func Exception(_ message: String) -> KotlinStyleError {
    return KotlinStyleError.Exception(message: message)
}

public func IllegalStateException(_ message: String) -> KotlinStyleError {
    return KotlinStyleError.IllegalStateException(message: message)
}

public func IllegalArgumentException(_ message: String) -> KotlinStyleError {
    return KotlinStyleError.IllegalArgumentException(message: message)
}
