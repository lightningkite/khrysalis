//Package: com.test
//Converted using Khrysalis2

import Foundation



public class Record {
    
    
    public var x: Int32
    public var y: String
    
    public func test() -> Void {
        print("Test run")
    }
    
    public init() {
        let x: Int32 = 0
        self.x = x
        let y: String = ""
        self.y = y
        print("Record created: \(x), \(y)")
    }
}
 
 

public func main() -> Void {
    var record = Record()
    record.x = 3
    record.y = "Hello"
    if record.x == 3 {
        record.y = "Set"
    }
    record.test()
    print("x: \(record.x), y: \(record.y)")
}
 
