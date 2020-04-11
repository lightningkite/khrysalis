//Package: com.test
//Converted using Khrysalis2

import Foundation



open class Record {
    
    
    public var x: Int32
    public var y: String
    
    open func test() -> Void {
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
 
 

public class BetterRecord: Record {
    
    
    public var z: Double
    
    override public func test() -> Void {
        print("Test run \(z.toInt())")
    }
    
    override public init() {
        let z: Double = 0.0
        self.z = z
        super.init()
    }
}
 
 

public func main() -> Void {
    var record = BetterRecord()
    record.x = 3
    record.y = "Hello"
    record.z = 32.1
    if record.x == 3 {
        record.y = "Set"
    }
    record.test()
    print("x: \(record.x), y: \(record.y)")
}
 
