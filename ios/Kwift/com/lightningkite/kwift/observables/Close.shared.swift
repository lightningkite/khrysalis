//Package: com.lightningkite.kwift.observables
//Converted using Kwift2

import Foundation



public class Close {
    
    public var close:  () -> Void
    
    
    public init(close: @escaping () -> Void) {
        self.close = close
    }
    convenience public init(_ close: @escaping () -> Void) {
        self.init(close: close)
    }
}
 
