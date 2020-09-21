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
    var value = value
    action(&value)
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

public extension String {
    init(kotlin: Any) {
        if let opt = kotlin as? OptionalProtocol {
            self.init(kotlin: opt.finalValue)
        } else {
            self.init(describing: kotlin)
        }
    }
}

public struct Pair<A, B> {
    public let first: A
    public let second: B
    public init(first: A, second: B) {
        self.first = first
        self.second = second
    }
    public init(_ first: A, _ second: B) {
        self.first = first
        self.second = second
    }
    public func toTuple() -> (A, B) { return (first, second) }
}

extension Pair: Encodable where A: Encodable, B: Encodable { }
extension Pair: Decodable where A: Decodable, B: Decodable { }
extension Pair: Equatable where A: Equatable, B: Equatable { }
extension Pair: Hashable where A: Hashable, B: Hashable { }

public struct Triple<A, B, C> {
    public let first: A
    public let second: B
    public let third: C
    public init(first: A, second: B, third: C) {
        self.first = first
        self.second = second
        self.third = third
    }
    public init(_ first: A, _ second: B, _ third: C) {
        self.first = first
        self.second = second
        self.third = third
    }
    public func toTuple() -> (A, B, C) { return (first, second, third) }
}

extension Triple: Encodable where A: Encodable, B: Encodable, C: Encodable { }
extension Triple: Decodable where A: Decodable, B: Decodable, C: Decodable { }
extension Triple: Equatable where A: Equatable, B: Equatable, C: Equatable { }
extension Triple: Hashable where A: Hashable, B: Hashable, C: Hashable { }

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
    func distinctBy<U: Hashable>(selector: (Iterator.Element)->U) -> [Iterator.Element] {
        var seen: [U: Bool] = [:]
        return self.filter { seen.updateValue(true, forKey: selector($0)) == nil }
    }
    func chunked(_ size: Int) -> Array<Array<Element>> { return chunked(size: size) }
    func chunked(size: Int) -> Array<Array<Element>> {
        var output = Array<Array<Element>>()
        var current = Array<Element>()
        for item in self {
            current.append(item)
            if current.count >= size {
                output.append(current)
                current = Array()
            }
        }
        if !current.isEmpty {
            output.append(current)
        }
        return output
    }
}

public func makeComparator<T>(function: @escaping (T, T)->Int) -> Comparator {
    return { (a: Any, b: Any) in
        if let a = a as? T {
            if let b = b as? T {
                let num = function(a, b)
                if num > 0 {
                    return .orderedDescending
                } else if num < 0 {
                    return .orderedAscending
                } else {
                    return .orderedSame
                }
            } else {
                return .orderedDescending
            }
        } else {
            return .orderedAscending
        }
    }
}

public extension Sequence where Iterator.Element: Hashable {
    func distinct() -> [Iterator.Element] {
        var seen: [Iterator.Element: Bool] = [:]
        return self.filter { seen.updateValue(true, forKey: $0) == nil }
    }
}

public extension Array {

    func getOrNull(index: Int) -> Element? {
        if index >= count { return nil }
        return self[index]
    }
    
    func forEachIndexed(_ action: (_ index:Int, Element) -> Void){
        for index in 0..<self.count{
            action(Int(index), self[index])
        }
    }
    
    func plus(_ element: Element) -> Array<Element> {
        var copy = self
        copy.append(element)
        return copy
    }
    func withoutIndex(_ index: Int) -> Array<Element> {
        var copy = self
        copy.remove(at: index)
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
    mutating func remove(element: Element) {
        remove(element)
    }
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
    func minus(element: Element) -> Array<Element> {
        return self.minus(element)
    }
}


public func deferComparison<T, C: Comparable>(_ get: @escaping (T) -> C?) -> (T, T) -> Bool {
    return { (a, b) in
        guard let va = get(a) else { return true }
        guard let vb = get(b) else { return false }
        return va < vb
    }
}
public func deferComparisonDescending<T, C: Comparable>(_ get: @escaping (T) -> C?) -> (T, T) -> Bool {
    return { (a, b) in
        guard let va = get(a) else { return false }
        guard let vb = get(b) else { return true }
        return va > vb
    }
}

public extension Collection {
    func find(_ predicate: (Element) -> Bool) -> Element? {
        return first(where: predicate)
    }
    static func +(first: Self, second: Element) -> Array<Element> {
        return first + [second]
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

public extension Comparable {
    func compareTo(_ other: Self) -> Int {
        if self > other {
            return ComparisonResult.orderedDescending.rawValue
        } else if self == other {
            return ComparisonResult.orderedSame.rawValue
        } else {
            return ComparisonResult.orderedAscending.rawValue
        }
    }
}

public func dictionaryOf<A, B>(_ contents: Pair<A, B>...) -> Dictionary<A, B> {
    return Dictionary(contents.map { $0.toTuple() }, uniquingKeysWith: { (_, a) in a })
}
public func dictionaryFrom<A, B>(_ contents: Array<Pair<A, B>>) -> Dictionary<A, B> {
    return Dictionary(contents.map { $0.toTuple() }, uniquingKeysWith: { (_, a) in a })
}
public extension Dictionary {
    
    mutating func putAll(from: Dictionary<Key, Value>) {
        for (key, value) in from {
            self[key] = value
        }
    }
    
    static func +(lhs: Self, rhs: Self) -> Self {
        var copy = lhs
        copy.putAll(from: rhs)
        return copy
    }

    func plus(map: Self) -> Self {
        var copy = self
        copy.putAll(from: map)
        return copy
    }
    
    mutating func getOrPut(key: Key, defaultValue: ()->Value) -> Value {
        if let value = self[key] {
            return value
        } else {
            let newValue = defaultValue()
            self[key] = newValue
            return newValue
        }
    }
    
    func minus(key:Key) -> Dictionary {
        var temp = self
        temp.removeValue(forKey: key)
        return temp
    }
}

public extension String {
    subscript(i: Int) -> Character {
        return self[index(startIndex, offsetBy: Int(i))]
    }

    func getOrNull(index: Int) -> Character? {
        if index >= count { return nil }
        return self[index]
    }

    func substring(_ startIndex: Int, _ endIndex: Int? = nil) -> String {
        if startIndex > self.count { return "" }
        if let endIndex = endIndex, startIndex >= endIndex { return "" }
        let s = self.index(self.startIndex, offsetBy: Int(startIndex))
        let e = self.index(self.startIndex, offsetBy: Int(endIndex ?? self.count))
        return String(self[s..<e])
    }
    func substring(startIndex: Int, endIndex: Int? = nil) -> String {
        return substring(startIndex, endIndex)
    }
    func contains(_ string: String) -> Bool {
        if string.isEmpty { return true }
        return self.range(of: string) != nil
    }
    func replace(_ target: String, _ withString: String) -> String {
        return self.replacingOccurrences(of: target, with: withString)
    }

    func removePrefix(prefix: String) -> String {
        if starts(with: prefix) {
            return substring(prefix.count)
        } else {
            return self
        }
    }
    
    func removeSuffix(suffix: String) -> String {
        if hasSuffix(suffix) {
            return substring(0, self.count - suffix.count)
        } else {
            return self
        }
    }
    
    func substringBefore(delimiter: String, missingDelimiterValue: String? = nil) -> String {
        let index = self.indexOf(string: delimiter)
        if index != -1 {
            return substring(0, index)
        } else {
            return missingDelimiterValue ?? self
        }
    }
    
    func substringAfter(delimiter: String, missingDelimiterValue: String? = nil) -> String {
        let index = self.indexOf(string: delimiter)
        if index != -1 {
            return substring(index + delimiter.count)
        } else {
            return missingDelimiterValue ?? self
        }
    }
    
    func substringBeforeLast(delimiter: String, missingDelimiterValue: String? = nil) -> String {
        let index = self.lastIndexOf(string: delimiter)
        if index != -1 {
            return substring(0, index)
        } else {
            return missingDelimiterValue ?? self
        }
    }
    
    func substringAfterLast(delimiter: String, missingDelimiterValue: String? = nil) -> String {
        let index = self.lastIndexOf(string: delimiter)
        if index != -1 {
            return substring(index + delimiter.count)
        } else {
            return missingDelimiterValue ?? self
        }
    }
    
    func remove(_ sequence:String) -> String{
        let temp = self
        return temp.replacingOccurrences(of: sequence, with:"")
    }
    
    func remove(sequence:String) -> String{
        return remove(sequence)
    }
    
    func drop(n:Int) -> String{
        if n >= self.count{
            return ""
        } else{
            return substring(startIndex:n)
        }
    }
    func drop(_ n: Int) -> String{
        return drop(n:n)
    }
}

public extension Character{
    func isDigit()->Bool{
        return self.isNumber
    }
}

public extension StringProtocol {
    func indexOf(string: Self, startIndex: Int = 0, ignoreCase: Bool = true) -> Int {
        if string.isEmpty { return 0 }
        var options: String.CompareOptions = [.literal]
        if ignoreCase {
            options = [.literal, .caseInsensitive]
        }
        if let index = range(of: string, options: options, range: self.index(self.startIndex, offsetBy: startIndex)..<self.endIndex)?.lowerBound {
            return Int(distance(from: self.startIndex, to: index))
        } else {
            return -1
        }
    }
    
    func lastIndexOf(string: Self, startIndex: Int = 0, ignoreCase: Bool = true) -> Int {
        if string.isEmpty { return 0 }
        var options: String.CompareOptions = [.literal, .backwards]
        if ignoreCase {
            options = [.literal, .caseInsensitive, .backwards]
        }
        if let index = range(of: string, options: options, range: self.index(self.startIndex, offsetBy: startIndex)..<self.endIndex)?.lowerBound {
            return Int(distance(from: self.startIndex, to: index))
        } else {
            return -1
        }
    }
    func indexOfAny(chars: Array<Character>, startIndex: Int = 0, ignoreCase: Bool = true) -> Int {
        var options: String.CompareOptions = [.literal]
        if ignoreCase {
            options = [.literal, .caseInsensitive]
        }
        if let index = rangeOfCharacter(from: CharacterSet(chars.flatMap { $0.unicodeScalars }), options: options, range: self.index(self.startIndex, offsetBy: startIndex)..<self.endIndex)?.lowerBound {
            return Int(distance(from: self.startIndex, to: index))
        } else {
            return -1
        }
    }

    func lastIndexOfAny(chars: Array<Character>, startIndex: Int = 0, ignoreCase: Bool = true) -> Int {
        var options: String.CompareOptions = [.literal, .backwards]
        if ignoreCase {
            options = [.literal, .caseInsensitive, .backwards]
        }
        if let index = rangeOfCharacter(from: CharacterSet(chars.flatMap { $0.unicodeScalars }), options: options, range: self.index(self.startIndex, offsetBy: startIndex)..<self.endIndex)?.lowerBound {
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

open class Exception: Error {
    public let message: String
    public let cause: Exception?
    public init(_ message: String = "?", _ cause: Exception? = nil) {
        self.message = message
        self.cause = cause
    }
}

public class IllegalStateException: Exception {}
public class IllegalArgumentException: Exception {}
public class NoSuchElementException: Exception {}

public extension Error {
    func printStackTrace(){
        print(self.localizedDescription)
    }
}

public extension CaseIterable {
    /// A collection of all values of this type.
    static func values() -> Array<Self> {
        return Array(self.allCases)
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


