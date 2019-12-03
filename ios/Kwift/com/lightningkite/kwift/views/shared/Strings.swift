//Package: com.lightningkite.kwift.views.shared
//Converted using Kwift2

import Foundation



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
    public var arguments: Array<Any>
    
    
    public func get(dependency: ViewDependency) -> String {
        var templateResolved = template.get(dependency)
        var fixedArguments = arguments.map{ (it) in 
            ( it as? ViewString )?.get(dependency) ?? it
        }
        return templateResolved.formatList(fixedArguments)
    }
    public func get(_ dependency: ViewDependency) -> String {
        return get(dependency: dependency)
    }
    
    public init(template: ViewString, arguments: Array<Any>) {
        self.template = template
        self.arguments = arguments
    }
    convenience public init(_ template: ViewString, _ arguments: Array<Any>) {
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
 
 

public class ViewStringList: ViewString {
    
    public var parts: Array<ViewString>
    public var separator: String
    
    
    public func get(dependency: ViewDependency) -> String {
        return parts.joinToString(separator) { (it) in 
            it.get(dependency)
        }
    }
    public func get(_ dependency: ViewDependency) -> String {
        return get(dependency: dependency)
    }
    
    public init(parts: Array<ViewString>, separator: String = "\n") {
        self.parts = parts
        self.separator = separator
    }
    convenience public init(_ parts: Array<ViewString>, _ separator: String = "\n") {
        self.init(parts: parts, separator: separator)
    }
}
 
 

extension Array where Element == ViewString {
    public func joinToViewString(separator: String = "\n") -> ViewString {
        if self.size == 1 {
            return self.first()
        }
        return ViewStringList(self, separator)
    }
    public func joinToViewString(_ separator: String) -> ViewString {
        return joinToViewString(separator: separator)
    }
}
 
 

extension ViewString {
    public func toDebugString() -> String {
        var thing = self
        switch thing {
        case let thing as ViewStringRaw: return thing.string
        case let thing as ViewStringResource: return thing.resource.toString()
        case let thing as ViewStringTemplate: return thing.template.toDebugString() + "(" + thing.arguments.joinToString{ (it) in 
            if let it = it as? ViewString {
                return it.toDebugString()
            } else {
                return "\(it)"
            }
        } + ")"
        case let thing as ViewStringList: return thing.parts.joinToString(thing.separator) { (it) in 
            it.toDebugString()
        }
        case let thing as ViewStringComplex: return "<Complex string \(thing)>"
        default: return "Unknown"
        }
    }
}
 
