//Package: com.test
//Converted using Khrysalis2

import Foundation



public func main() -> Void {
    var number: Int32 = 0
    var value = 43.toInt()
    switch value {
    case 1: number += 1
    case 2:
        number += 2
        number += 3
    case 43:
        number += 4
    default:
        number -= 99
    }
    if value == 42 {
        number += 8
    } else if value == 41 {
        number += 16
    } else if value == 43 {
        number += 32
    } else  {
        }
    print(number)
}
 
