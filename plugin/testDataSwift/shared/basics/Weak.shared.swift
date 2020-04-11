//Package: com.test
//Converted using Khrysalis2

import Foundation



public class SillyBox {
    
    public var x: Int32
    
    
    public init(x: Int32) {
        self.x = x
    }
    convenience public init(_ x: Int32) {
        self.init(x: x)
    }
}
 
 

public class Stuff {
    
    
    public weak var weakBox: SillyBox? 
    
    public init(box: SillyBox) {
        let weakBox: SillyBox?  = box
        self.weakBox = weakBox
    }
    convenience public init(_ box: SillyBox) {
        self.init(box: box)
    }
}
 
 

public func main() -> Void {
    var sillyBox = SillyBox(3)
    var stuff = Stuff(sillyBox)
    print((stuff.weakBox?.x)?.toString() ?? "-")
}
 
