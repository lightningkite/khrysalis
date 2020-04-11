//Package: com.test
//Converted using Khrysalis2

import Foundation



public enum Singleton {
    static public var x: Int32 = 0
    
    static public func doThing(argA: String, argB: Int32 = 3) -> Void {
        print("Hello World!")
    }
    static public func doThing(_ argA: String, _ argB: Int32 = 3) -> Void {
        return doThing(argA: argA, argB: argB)
    }
}
 
 

public func main() -> Void {
    Singleton.doThing("asdf", 3)
}
 
