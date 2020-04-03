//Package: com.test
//Converted using Khrysalis2

import Foundation



public func main() -> Void {
    var number: Int32 = 0
    
    while number < 10 {
        number += 1
    }
    
    for i in 0.toInt() ... 20.toInt() {
        number += i
    }
    if number % 2 == 0 {
        number += 1
    } else {
        number -= 1
    }
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
 
