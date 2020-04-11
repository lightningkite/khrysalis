//Package: com.test
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



public func test() -> Void {
    var lambda = weakLambda{ (value: Int32) in 
        print(value)
    }
    var lambda2 = weakLambda{ (value: Int32, second: String? ) in 
        print(value)
        return 2
    }
}
 
