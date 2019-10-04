//Package: com.lightningkite.interfaceTest
//Converted using Kwift2

import Foundation



public protocol MyInterface {
    
    func bar() -> String
    
    var x: Int32 { get set }
    
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
    
    
    
    public func bar() -> String {
        return "2-Implementation"
    }
    public var x: Int32 {
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
    
    open init() {
    }
}
 
 
 
 

open class Parent {
    
    
    
    public func three() -> Int32 {
        return 3
    }
    public var x: Int32 {
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
    public var four
    
    open init() {
        let four = 4
        self.four = four
    }
}
 
 

public class Child: Parent, MyInterface {
    
    
    public var five
    
    public func six() -> String {
        return "6"
    }
    
    public func bar() -> String {
        return "2-Child"
    }
    
    override open init() {
        let five = 5
        self.five = five
        super.init()
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
 
