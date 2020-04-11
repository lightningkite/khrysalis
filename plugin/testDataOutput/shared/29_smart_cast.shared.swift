//Package: null
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



public func main(args: Array<String>) -> Void {
    var x: Int32?  = 1
    var y: Int32 = 2
    var z: Int32?  = nil
    if let x = x, normalExpression == true {
        y += x
    } else {
        print("FAIL: x should not be null")
    }
    if let z = z {
        print("FAIL: z should be null")
        y += z
    }
    if y != 3 {
        print("FAIL: y should be 3, is \(y)")
    }
    if let z = z as? Int {
        print("FAIL: z should be null")
    }
    if let x = x, let z = z as? Int, y != 3 {
        print("CHAOS!s!.s")
    }
    print("Success")
}
public func main(_ args: Array<String>) -> Void {
    return main(args: args)
}
 
