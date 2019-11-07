//Package: com.test
//Converted using Kwift2

import Foundation



public class Thing: Equatable, Hashable {
    
    public var value: Int32
    public var sub: Thing? 
    
    public static func == (lhs: Thing, rhs: Thing) -> Bool {
        return lhs.value == rhs.value &&
            lhs.sub == rhs.sub
    }
    public func hash(into hasher: inout Hasher) {
        hasher.combine(value)
        hasher.combine(sub)
    }
    public func copy(
        value: (Int32)? = nil,
        sub: (Thing? )? = nil
    ) -> Thing {
        return Thing(
            value: value ?? self.value,
            sub: sub ?? self.sub
        )
    }
    
    
    public func test() -> Void {
        return ()
    }
    
    public func getSub(action: () -> Void) -> Thing?  {
        return sub
    }
    
    public func getSub() -> Thing?  {
        return sub
    }
    
    public init(value: Int32 = 0, sub: Thing?  = nil) {
        self.value = value
        self.sub = sub
    }
    convenience public init(_ value: Int32, _ sub: Thing?  = nil) {
        self.init(value: value, sub: sub)
    }
}
 
 

public func main() -> Void {
    var thing = Thing(0, Thing(1, Thing(2, nil)))
    print((thing.sub?.sub)?.value)
    thing?.test()
    (thing?.sub)?.test()
    (thing?.getSub{ () in 
        print("Hello")
    })?.test()
    (thing?.getSub())?.test()
}
 
