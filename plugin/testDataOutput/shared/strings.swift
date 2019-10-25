//Package: com.lightningkite.kwift.views.shared
//Converted using Kwift2

import Foundation



public final class ManyThings: Equatable, Hashable {
    
    public var x: Int32
    public var y: Int32
    public var z: Int32
    public static func == (lhs: ManyThings, rhs: ManyThings) -> Bool {
        return lhs.x == rhs.x &&
            lhs.y == rhs.y &&
            lhs.z == rhs.z
    }
    public func hash(into hasher: inout Hasher) {
        hasher.combine(x)
        hasher.combine(y)
        hasher.combine(z)
    }
    public func copy(
        x: (Int32)? = nil,
        y: (Int32)? = nil,
        z: (Int32)? = nil
    ) -> ManyThings {
        return ManyThings(
            x: x ?? self.x,
            y: y ?? self.y,
            z: z ?? self.z
        )
    }
    
    public func stringy() -> Void {
        return "\(x)\(y)\(z)"
    }
    
    public init(x: Int32, y: Int32, z: Int32) {
        self.x = x
        self.y = y
        self.z = z
    }
    convenience public init(_ x: Int32, _ y: Int32, _ z: Int32) {
        self.init(x: x, y: y, z: z)
    }
    
}
 
 

public protocol ViewString {
    
    func get(dependency: ViewDependency) -> String
    func get(_ dependency: ViewDependency) -> String
}
 
 

public class ViewStringRaw: ViewString {
    
    public var string: String
    
    
    public func get(dependency: ViewDependency) -> String {
        return string
    }
    public func get(_ dependency: ViewDependency) -> String {
        return get(dependency: dependency)
    }
    
    public init(string: String) {
        self.string = string
    }
    convenience public init(_ string: String) {
        self.init(string: string)
    }
}
 
 

public class ViewStringResource: ViewString {
    
    public var resource: StringResource
    
    
    public func get(dependency: ViewDependency) -> String {
        return dependency.getString(resource)
    }
    public func get(_ dependency: ViewDependency) -> String {
        return get(dependency: dependency)
    }
    
    public init(resource: StringResource) {
        self.resource = resource
    }
    convenience public init(_ resource: StringResource) {
        self.init(resource: resource)
    }
}
 
 

public class ViewStringTemplate: ViewString {
    
    public var template: ViewString
    public var arguments: Array<Any?>
    
    
    public func get(dependency: ViewDependency) -> String {
        var template = template.get(dependency)
        var arguments = arguments.map{ (it) in 
            ( it as? ViewString )?.get(dependency) ?? it
        }
        return template.formatList(arguments)
    }
    public func get(_ dependency: ViewDependency) -> String {
        return get(dependency: dependency)
    }
    
    public init(template: ViewString, arguments: Array<Any?>) {
        self.template = template
        self.arguments = arguments
    }
    convenience public init(_ template: ViewString, _ arguments: Array<Any?>) {
        self.init(template: template, arguments: arguments)
    }
}
 
 

public class ViewStringComplex: ViewString {
    
    public var getter:  (ViewDependency) -> String
    
    
    public func get(dependency: ViewDependency) -> String {
        return getter(dependency)
    }
    public func get(_ dependency: ViewDependency) -> String {
        return get(dependency: dependency)
    }
    
    public init(getter: @escaping (ViewDependency) -> String) {
        self.getter = getter
    }
    convenience public init(_ getter: @escaping (ViewDependency) -> String) {
        self.init(getter: getter)
    }
}
 
 

public func ViewStringList(others: Array<ViewString>) -> Void {
    return ViewStringComplex{ (dependency) in 
        others.joinToString("\n") { () in 
            it.get(dependency)
        }
    }
}
public func ViewStringList(_ others: Array<ViewString>) -> Void {
    return ViewStringList(others: others)
}
 
 

extension Array where Element: ViewString {
    public func joinToString(separator: String = "\n") -> Void {
        return ViewStringComplex{ (dependency) in 
            joinToString(separator) { () in 
                it.get(dependency)
            }
        }
    }
    public func joinToString(_ separator: String) -> Void {
        return joinToString(separator: separator)
    }
}
 
