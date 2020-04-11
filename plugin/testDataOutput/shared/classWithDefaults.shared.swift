//Package: com.test
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



public class Something {
    
    
    public var test: Int32
    public weak var weakRef: Int32? 
    
    public init(argument: Int32) {
        let test: Int32 = argument
        self.test = test
        let weakRef: Int32?  = argument
        self.weakRef = weakRef
    }
    convenience public init(_ argument: Int32) {
        self.init(argument: argument)
    }
}
 
