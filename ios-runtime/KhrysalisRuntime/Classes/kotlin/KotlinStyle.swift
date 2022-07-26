import Foundation

public protocol KEquatable: Equatable {
    func equals(other: Any) -> Bool
}
public extension KEquatable {
    static func ==(lhs: Self, rhs: Self) -> Bool {
        return lhs.equals(other: rhs)
    }
}
public protocol KHashable: Hashable {
    func hashCode() -> Int
}
public extension KHashable {
    func hash(into hasher: inout Hasher) {
        hasher.combine(hashCode())
    }
}
public protocol KStringable: CustomStringConvertible {
    func toString() -> String
}
public extension KStringable {
    var description: String {
        return toString()
    }
}


public protocol AnyKeyPathLike {
    func getAnyAny(_ on: Any) -> Any
}
extension AnyKeyPath: AnyKeyPathLike {
    public func getAnyAny(_ on: Any) -> Any {
        return on[keyPath: self]!
    }
}
public protocol PartialKeyPathLike {
    associatedtype Root
    func getAny(_ on: Root) -> Any
}
extension PartialKeyPath: PartialKeyPathLike {
    public func getAny(_ on: Root) -> Any {
        return on[keyPath: self]
    }
}
public protocol KeyPathLike: PartialKeyPathLike {
    associatedtype Value
    func get(_ on: Root) -> Value
}
extension KeyPath: KeyPathLike {
    public func get(_ on: Root) -> Value {
        return on[keyPath: self]
    }
}
public protocol WritableKeyPathLike: KeyPathLike {
    func set(_ on: Root, _ value: Value) -> Root
}
extension WritableKeyPath: WritableKeyPathLike {
    public func set(_ on: Root, _ value: Value) -> Root {
        var newOn = on
        newOn[keyPath: self] = value
        return newOn
    }
}

public protocol AnyPropertyIterable {
    static var anyProperties: Array<AnyPropertyIterableProperty> { get }
}
public protocol PropertyIterable: AnyPropertyIterable {
    static var properties: Array<PartialPropertyIterableProperty<Self>> { get }
}
public protocol AnyPropertyIterableProperty: AnyKeyPathLike {
    var name: String { get }
}
public class PartialPropertyIterableProperty<Root>: AnyPropertyIterableProperty, PartialKeyPathLike, Equatable, Hashable {
    public static func == (lhs: PartialPropertyIterableProperty<Root>, rhs: PartialPropertyIterableProperty<Root>) -> Bool {
        return lhs.name == rhs.name
    }
    public func hash(into hasher: inout Hasher) {
        hasher.combine(name)
    }
    
    public func getAnyAny(_ on: Any) -> Any {
        return (on as! Root)[keyPath: partialPath]
    }
    public func getAny(_ on: Root) -> Any {
        return on[keyPath: partialPath]
    }
    public var name: String { fatalError() }
    public var partialPath: PartialKeyPath<Root> { fatalError() }
}
public class PropertyIterableProperty<Root, Value>: PartialPropertyIterableProperty<Root>, WritableKeyPathLike {
    private var _name: String
    public override var name: String { return _name }
    public let path: KeyPath<Root, Value>
    override public var partialPath: PartialKeyPath<Root> { return path }
    public let setCopy: (Root, Value) -> Root
    public init(name: String, path: KeyPath<Root, Value>, setCopy: @escaping (_ root: Root, _ value: Value) -> Root) {
        self._name = name
        self.path = path
        self.setCopy = setCopy
    }
    
    public func get(_ on: Root) -> Value {
        return on[keyPath: path]
    }
    public func set(_ on: Root, _ value: Value) -> Root {
        return setCopy(on, value)
    }
}
