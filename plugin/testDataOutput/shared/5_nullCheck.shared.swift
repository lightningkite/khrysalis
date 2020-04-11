//Package: multiplier
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



public func main(args: Array<String>) -> Void {
    var x: Int32?  = nil
    if let x = x {
        print("x: \(x)")
    } else {
        print("X is null")
    }
    if let x = x {
        print("x: \(x)")
    } else {
        print("X is null")
    }
    
    if let it = (x) {
        print("X: \(it)") 
    }
    var xWithOne = { () in 
        if let it = (x) {
            return it + 1
        }
        return nil
    }() ?? 0
    
    if let it = (x) {
        print("X: \(it)") 
    } else {
        print("X is null") 
    }
}
public func main(_ args: Array<String>) -> Void {
    return main(args: args)
}
 
