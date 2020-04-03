//Package: com.test
//Converted using Khrysalis2

import Foundation



public func main() -> Void {
    var number: Int32 = 0
    number += {if true {
        return 1
    } else {
        return 0
    }}()
    var letCheckValue: String?  = nil
    number += { () in 
        if let it = (letCheckValue) {
            return 2
        }
        return nil
    }() ?? 0
    print(number)
}
 
