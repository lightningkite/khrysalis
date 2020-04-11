//Package: null
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



open class Base {
    
    
    
    open func v() -> Void {
        print("ERROR-Open")
    }
    
    public func nv() -> Void {
        print("4")
    }
    
    public init() {
        print("1")
    }
}
 
 

open class Derived: Base {
    
    
    
    override public func v() -> Void {
        print("ERROR-Derived")
    }
    
    public func x() -> Void {
        print("6")
    }
    
    override public init() {
        super.init()
        print("2")
    }
}
 
 

public class Derived2: Derived {
    
    
    
    override public func v() -> Void {
        print("5")
    }
    
    public func y() -> Void {
        print("7")
    }
    
    override public init() {
        super.init()
        print("3")
    }
}
 
 

public func main(args: Array<String>) -> Void {
    var x = Derived2()
    x.nv()
    x.v()
    x.x()
    x.y()
}
public func main(_ args: Array<String>) -> Void {
    return main(args: args)
}
 
