//Package: com.test
//Converted using Khrysalis2

import Foundation



public class NameTag<T: Hashable>: Equatable, Hashable {
    
    public var item: T
    public var name: String
    
    public static func == (lhs: NameTag, rhs: NameTag) -> Bool {
        return lhs.item == rhs.item &&
            lhs.name == rhs.name
    }
    public func hash(into hasher: inout Hasher) {
        hasher.combine(item)
        hasher.combine(name)
    }
    public func copy(
        item: (T)? = nil,
        name: (String)? = nil
    ) -> NameTag {
        return NameTag(
            item: item ?? self.item,
            name: name ?? self.name
        )
    }
    
    
    public init(item: T, name: String) {
        self.item = item
        self.name = name
    }
    convenience public init(_ item: T, _ name: String) {
        self.init(item: item, name: name)
    }
}
 
 

extension NameTag where T: MyInterface {
    public func printInterface() -> Void {
        print("Hello!  I am \(name), I stand for \(item.x).")
    }
}
 
 

public protocol MyInterface {
    
    var x: String { get }
    
    func y(string: String) -> String
    func y(_ string: String) -> String
}

public extension MyInterface {
    public var x: String {
        get {
            return ""
        }
    }
    
    func y(string: String) -> String {
        return x + string
    }
    func y(_ string: String) -> String {
        return y(string: string)
    }
}
 
 

public class ImplOverX: MyInterface, Equatable, Hashable {
    
    public var k: Int32
    
    public static func == (lhs: ImplOverX, rhs: ImplOverX) -> Bool {
        return lhs.k == rhs.k
    }
    public func hash(into hasher: inout Hasher) {
        hasher.combine(k)
    }
    public func copy(
        k: (Int32)? = nil
    ) -> ImplOverX {
        return ImplOverX(
            k: k ?? self.k
        )
    }
    
    public var x: String {
        get {
            return "Hello!"
        }
    }
    
    public init(k: Int32 = 0) {
        self.k = k
    }
    convenience public init(_ k: Int32) {
        self.init(k: k)
    }
}
 
 

public class ImplOverY: MyInterface, Equatable, Hashable {
    
    public var k: Int32
    
    public static func == (lhs: ImplOverY, rhs: ImplOverY) -> Bool {
        return lhs.k == rhs.k
    }
    public func hash(into hasher: inout Hasher) {
        hasher.combine(k)
    }
    public func copy(
        k: (Int32)? = nil
    ) -> ImplOverY {
        return ImplOverY(
            k: k ?? self.k
        )
    }
    
    
    public func y(string: String) -> String {
        return "\(x)!"
    }
    public func y(_ string: String) -> String {
        return y(string: string)
    }
    
    public init(k: Int32 = 0) {
        self.k = k
    }
    convenience public init(_ k: Int32) {
        self.init(k: k)
    }
}
 
 

public class ImplBoth: MyInterface, Equatable, Hashable {
    
    public var k: Int32
    
    public static func == (lhs: ImplBoth, rhs: ImplBoth) -> Bool {
        return lhs.k == rhs.k
    }
    public func hash(into hasher: inout Hasher) {
        hasher.combine(k)
    }
    public func copy(
        k: (Int32)? = nil
    ) -> ImplBoth {
        return ImplBoth(
            k: k ?? self.k
        )
    }
    
    public var x: String {
        get {
            return "Hello!"
        }
    }
    
    public func y(string: String) -> String {
        return "\(x)!"
    }
    public func y(_ string: String) -> String {
        return y(string: string)
    }
    
    public init(k: Int32 = 0) {
        self.k = k
    }
    convenience public init(_ k: Int32) {
        self.init(k: k)
    }
}
 
 

public func main() -> Void {
    NameTag(ImplBoth(), "ImplBoth").printInterface()
    NameTag(ImplOverX(), "ImplOverX").printInterface()
    NameTag(ImplOverY(), "ImplOverY").printInterface()
}
 
 
