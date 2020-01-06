//Package: com.lightningkite.kwift.observables
//Converted using Kwift2

import Foundation



public class NeverEvent<T>: Event<T> {
    
    
    
    override public func add(listener: @escaping (T) -> Bool) -> Close {
        return Close({ () in 
        })
    }
    
    override public init() {
        super.init()
    }
}
 
