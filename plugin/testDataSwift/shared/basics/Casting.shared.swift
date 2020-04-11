//Package: com.test
//Converted using Khrysalis2

import Foundation



public func main() -> Void {
    var value: Any?  = nil
    var asString = value as? String
    value = "XP"
    var forced = value as! String
    print(value ?? "Nope")
    print(forced)
}
 
