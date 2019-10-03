//Package: multiplier
//Converted using Kwift2

import Foundation


public func main(args: Array<String>) -> Void {
    var x: Int ?  = nil
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
    
    if let it = x {
        print("X: \(it)")
    }
    var xWithOne = { () in 
        if let it = x {
            return it + 1
        }
        return nil
    }() ?? 0
    { () in 
        if let it = x {
            return print("X: \(it)")
        }
        return nil
    }() ?? { () in 
    print("X is null")
    }()
}
public func main(_ args: Array<String>) -> Void {
    return main(args: args)
}
 
