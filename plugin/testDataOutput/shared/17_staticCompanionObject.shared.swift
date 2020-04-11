//Package: null
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



public class MyClass {
    
    
    
    public func a() -> Int32 {
        return 1
    }
    public var d
    public var g
    
    //Start Companion
    
    static public func b() -> Int32 {
        return 2
    }
    static public var e = 5
    static public var h = 8
    //End Companion
    
    
    public func c() -> Int32 {
        return 3
    }
    public var f
    public var i
    
    public init() {
        let d = 4
        self.d = d
        let g = 7
        self.g = g
        let f = 6
        self.f = f
        let i = 9
        self.i = i
    }
}
 
 

public func main(args: Array<String>) -> Void {
    var obj = MyClass()
    print(obj.a())
    print(MyClass.b())
    print(obj.c())
    print(obj.d)
    print(MyClass.e)
    print(obj.f)
    print(obj.g)
    print(MyClass.h)
    print(obj.i)
}
public func main(_ args: Array<String>) -> Void {
    return main(args: args)
}
 