//Package: com.test
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



public func testWeakRef() -> Void {
    var x: Int32 = 0
    weak var weakX: Int32?  = x
    print(weakX)
}
 
