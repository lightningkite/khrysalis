//Package: com.test
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



public func asdf() -> Void {
    a.onChange.add(listener: weakLambda{ (aValue) in 
        var bValue = bWeak?.value
        if let bValue = bValue {
            self.value = calculation(aValue, bValue)
        }
    })
}
 
