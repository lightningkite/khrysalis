//Package: null
//Converted using Kwift2

import Foundation



public func main(args: Array<String>) -> Void {
    cases(x: 1)
    cases(x: 2)
    cases(x: 3)
    cases(x: 4)
}
public func main(_ args: Array<String>) -> Void {
    return main(args: args)
}
 
 

public func cases(x: Int32) -> Void {
    switch x {
    case 1: print("x == 1")
    case 2: print("x == 2")
    default:
        print("x is neither 1 nor 2")
        print("x might be something more")
    }
    switch x {
    case 0, 
    case 1: print("x == 0 or x == 1")
    default: print("otherwise")
    }
    if x > 2 {
        print("Bigger than 2")
    } else if x < 2 {
        print("Less than 2")
    } else if x == 2 {
        print("Is 2")
    }
    var y: Any = x
    switch y {
    case let y as Int32: print(y + 2)
    default: print(y)
    }
    switch 21 + 3 {
    case is Int32: print(4)
    default: print(4)
    }
}
public func cases(_ x: Int32) -> Void {
    return cases(x: x)
}
 
