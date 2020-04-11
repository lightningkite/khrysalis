//Package: null
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



public func main(args: Array<String>) -> Void {
    do { 
 var x: Int32?  = 42 
 var y: Int32?  = nil 
 var x1 = x ?? 0 
 var y1 = y ?? 0 
 var x2 = x ?? throw Swift.Error("FAIL: Should never happen") 
 var y2 = y ?? throw Swift.Error("SUCCESS: Should happen") 
 print("Fail") 
 } catch ( e : Swift.Error ) { 
 print("Success") 
 }
    var values: Array<Int32?> = [1, 2, nil, nil, 3, 4, nil]
    
    for value in values {
        var a = value ?? continue
        print(a)
    }
}
public func main(_ args: Array<String>) -> Void {
    return main(args: args)
}
 
