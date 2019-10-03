//Package: com.lightningkite.interfaceTest
//Converted using Kwift2

import Foundation


public protocol MyInterface {
    
    func bar() -> String
    
    var x: Int { get set }
    
    var y: String { get }
    
    var z: Float { get }
}

public extension MyInterface {
    public var z: Float {
        get {
            return 0
        }
    }
}
 
 
public class Implementation: MyInterface {
    
    
    public init() {
    }
    
    
    public func bar() -> String {
        return "2-Implementation"
    }
    public var x: Int {
        get {
            return 2
        }
        set(value) {
        }
    }
    public var y: String {
        get {
            return "Hello World!"
        }
    }
}
 
 
 
 
public class Parent {
    
    
    public init() {
    }
    
    
    public func three() -> Int {
        return 3
    }
    public var x: Int {
        get {
            return 2
        }
        set(value) {
        }
    }
    public var y: String {
        get {
            return "Hello World!"
        }
    }
    public var four = 4
}
 
 
public class Child: Parent, MyInterface {
    
    
    override public init() {
        super.init()
    }
    
    public var five = 5
    
    public func six() -> String {
        return "6"
    }
    
    public func bar() -> String {
        return "2-Child"
    }
}
 
 
public func main(args: Array<String>) -> Void {
    var obj = Implementation()
    print(obj.foo())
    print(obj.bar())
    var child = Child()
    print(child.foo())
    print(child.bar())
    print(child.three())
    print(child.four)
    print(child.five)
    print(child.six())
}
public func main(_ args: Array<String>) -> Void {
    return main(args: args)
}
 
