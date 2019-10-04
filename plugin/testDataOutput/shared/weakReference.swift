//Package: com.test
//Converted using Kwift2

import Foundation



public func testWeakRef() -> Void {
    var x: Int32 = 0
    weak var weakX: Int32?  = x
    print(weakX)
}
 
