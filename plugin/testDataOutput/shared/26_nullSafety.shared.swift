//Package: null
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



public class Department {
    
    
    public var head: User? 
    
    public init() {
        let head: User?  = nil
        self.head = head
    }
}
 
 

public class User {
    
    public var name: String? 
    
    public var department: Department? 
    
    public init(name: String?  = nil) {
        self.name = name
        let department: Department?  = nil
        self.department = department
    }
    convenience public init(_ name: String? ) {
        self.init(name: name)
    }
}
 
 

public func main(args: Array<String>) -> Void {
    var a: String = "abc"
    var b: String?  = "abc"
    b = nil
    print("a.length = \(a.length) (should be 3)")
    var bl = {if let b = b {
        return b.length
    } else {
        return -1
    }}()
    print("bl = \(bl) (should be -1)")
    if let b = b, b.length > 0 {
        print("ERROR: String of length \(b.length)")
    } else {
        print("OK: Empty string")
    }
    print("\(b?.length) should be null")
    var bob = User(name: "Bob")
    var john = User(name: "John")
    var marketing = Department()
    bob.department = marketing
    marketing.head = john
    print("\((bob.department?.head)?.name) should be John")
    b = "new b value"
    print("\(b!.length) should be 11")
    var aInt: Int32?  = a as? Int32
    print("\(aInt) should be null")
}
public func main(_ args: Array<String>) -> Void {
    return main(args: args)
}
 
