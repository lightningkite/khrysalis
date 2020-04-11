//Package: com.test
//Converted using Khrysalis2

import Foundation



public class Thing {
    
    
    public var thing: Any
    public var lambda: () -> Void {
        get {
            return { [unowned self] () in 
                print("Hello!")
            }
        }
    }
    
    public init(input: Any) {
        let thing: Any = input
        self.thing = thing
    }
    convenience public init(_ input: Any) {
        self.init(input: input)
    }
}
 
 

public class DummyObject {
    
    
    
    public init() {
    }
}
 
 

public func main() -> Void {
    Thing(DummyObject()).lambda()
}
 
