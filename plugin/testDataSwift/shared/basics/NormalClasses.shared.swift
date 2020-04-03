//Package: com.test
//Converted using Khrysalis2

import Foundation



public class Record {
    
    public var x: Int32
    public var y: String
    
    
    public func test() -> Void {
        print("Test run")
    }
    
    public init(x: Int32, y: String) {
        self.x = x
        self.y = y
        print("Record created: \(x), \(y)")
    }
    convenience public init(_ x: Int32, _ y: String) {
        self.init(x: x, y: y)
    }
}
 
 

public func main() -> Void {
    var record = Record(x: 3, y: "Hello")
    if record.x == 3 {
        record.y = "Set"
    }
    record.test()
    print("x: \(record.x), y: \(record.y)")
}
 
