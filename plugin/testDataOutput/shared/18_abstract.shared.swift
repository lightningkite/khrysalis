//Package: null
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



open class Base {
    
    
    
    open func f() -> Void {
    }
    
    public func g() -> Void {
        print("g() called")
    }
    
    public init() {
    }
}
 
 

open class Derived: Base {
    
    
    
    override open func f() -> Void { fatalError() }
    
    override public init() {
        super.init()
    }
}
 
 

public class Derived2: Base {
    
    
    
    override public func f() -> Void {
        print("f() called")
    }
    
    override public init() {
        super.init()
    }
}
 
 

public func main(args: Array<String>) -> Void {
    Derived2().f()
    Derived2().g()
}
public func main(_ args: Array<String>) -> Void {
    return main(args: args)
}
 
