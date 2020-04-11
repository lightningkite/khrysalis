//Package: com.test
//Converted using Khrysalis2

import Foundation



public class SillyBox: Equatable, Hashable {
    
    public var subBox: SillyBox? 
    
    public static func == (lhs: SillyBox, rhs: SillyBox) -> Bool {
        return lhs.subBox == rhs.subBox
    }
    public func hash(into hasher: inout Hasher) {
        hasher.combine(subBox)
    }
    public func copy(
        subBox: (SillyBox? )? = nil
    ) -> SillyBox {
        return SillyBox(
            subBox: subBox ?? self.subBox
        )
    }
    
    
    public init(subBox: SillyBox?  = nil) {
        self.subBox = subBox
    }
    convenience public init(_ subBox: SillyBox? ) {
        self.init(subBox: subBox)
    }
}
 
 

public func main() -> Void {
    var item = SillyBox(SillyBox())
    
    if let it = (item.subBox?.subBox) {
        print("I got a box!") 
    } else {
        print("I didn't get a box...") 
    }
    (item.subBox?.subBox)?.subBox?.subBox = SillyBox()
    
    if let it = (item.subBox?.subBox) {
        print("I got a box!") 
    } else {
        print("I didn't get a box...") 
    }
    item.subBox?.subBox = SillyBox()
    
    if let it = (item.subBox?.subBox) {
        print("I got a box!") 
    } else {
        print("I didn't get a box...") 
    }
}
 
