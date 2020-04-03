//Package: com.test
//Converted using Khrysalis2

import Foundation



public class Record: Equatable, Hashable {
    
    public var x: Int32
    public var y: String
    
    public static func == (lhs: Record, rhs: Record) -> Bool {
        return lhs.x == rhs.x &&
            lhs.y == rhs.y
    }
    public func hash(into hasher: inout Hasher) {
        hasher.combine(x)
        hasher.combine(y)
    }
    public func copy(
        x: (Int32)? = nil,
        y: (String)? = nil
    ) -> Record {
        return Record(
            x: x ?? self.x,
            y: y ?? self.y
        )
    }
    
    
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
    print("x: \(record.x), y: \(record.y)")
    var copy = record.copy(x: 32)
    print("x: \(copy.x), y: \(copy.y)")
    if copy != record {
        print("Not equal")
    }
    record.test()
}
 
