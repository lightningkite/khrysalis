//Package: com.test
//Converted using Khrysalis2

import Foundation



public class HasLambda {
    
    public var action:  () -> Void
    
    
    public func invoke() -> Void {
        action()
    }
    
    public init(action: @escaping () -> Void = { () in 
    }) {
        self.action = action
    }
    convenience public init(_ action: @escaping () -> Void) {
        self.init(action: action)
    }
}
 
 
public var globalThing: () -> Void = { () in 
}

public func doThing(action: @escaping () -> Void) -> Void {
    globalThing = action
}
 
 

public func main() -> Void {
    HasLambda{ () in 
        print("Hello world!")
    }.invoke()
    doThing{ () in 
        print("Hello world 2!")
    }
    globalThing()
}
 
