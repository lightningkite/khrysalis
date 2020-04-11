//Package: com.test
//Converted using Khrysalis2

import Foundation



extension Int32 {
    public func doubleAdd(other: Int32) -> Int32 {
        return self + other + other
    }
    public func doubleAdd(_ other: Int32) -> Int32 {
        return doubleAdd(other: other)
    }
}
 
 

extension Int32 {
    public var half: Int32 {
        get {
            return self / 2
        }
    }
}
 
 

func /(self: String, other: String) -> String { return self.div(other: other) }
extension String {
    public func div(other: String) -> String {
        return self + "/" + other
    }
    public func div(_ other: String) -> String {
        return div(other: other)
    }
}
 
 

public func main() -> Void {
    print(3.toInt().doubleAdd(5))
    print(18.toInt().half)
    print("a" / "b")
}
 
