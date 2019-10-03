//Package: com.test
//Converted using Kwift2

import Foundation


public func testWeakRef() -> Void {
    var x: Int = 0
    weak var weakX: Int ?  = x
    print(weakX)
}
 
