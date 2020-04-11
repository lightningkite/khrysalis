//Package: null
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



public func main(args: Array<String>) -> Void {
    var double: Double = 64.64
    var float: Float = Float(32.32)
    var long: Int64 = 64
    var int: Int32 = 32
    var short: Int16 = 16
    var byte: Int8 = 8
    var double2 = 64.64
    var float2 = Float(32.32)
    var long2 = 64
    var int2 = 32
    if double != double2 {
        print("double error")
    }
    if float != float2 {
        print("float error")
    }
    if long != long2 {
        print("long error")
    }
    if int != int2 {
        print("int error")
    }
    var sum = double + float + long + int + short + byte + double2 + float2 + long2 + int2
    if Math.abs(sum - 409.9199993896484) > 0.01 {
        print("sum error")
    }
    if Math.max(100, long) != 100 {
        print("max error")
    }
    if Math.min(Float(32), float) != Float(32) {
        print("min error")
    }
    print("finished")
}
public func main(_ args: Array<String>) -> Void {
    return main(args: args)
}
 
