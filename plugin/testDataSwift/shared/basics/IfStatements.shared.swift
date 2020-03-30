//Package: com.test
//Converted using Khrysalis2

import Foundation



public func main() -> Void {
    var number: Int32 = 0
    if true {
        number += 1
    } else {
        number -= 1
    }
    if false {
        number += 2
    } else {
        number -= 2
    }
    var thing: String?  = nil
    
    if let it = (thing) {
        number += 4 
    } else {
        number -= 4 
    }
    thing = "Hello"
    
    if let it = (thing) {
        number += 8 
    } else {
        number -= 8 
    }
    
    if let it = (thing?.substringBefore(",")) {
        number += 16 
    } else {
        number -= 16 
    }
    if let thing = thing {
        print(thing)
        number += 32
    }
    if let thing = thing as? String {
        print(thing)
        number += 64
    }
    print(number)
}
 
