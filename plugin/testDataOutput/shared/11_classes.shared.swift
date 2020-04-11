//Package: null
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



public class Empty {
    
    
    
    public init() {
    }
}
 
 

public class Empty2 {
    
    
    
    public init() {
        print("Hello!")
    }
}
 
 

public class Empty3 {
    
    
    
    public init() {
        print("Hello ")
        print("world!")
    }
}
 
 

public class HasInput {
    
    
    
    public init(name: String, ignored: Bool) {
        print("Customer initialized with value \(name)")
    }
    convenience public init(_ name: String, _ ignored: Bool) {
        self.init(name: name, ignored: ignored)
    }
}
 
 

public class HasSavedInput {
    
    public var name: String
    
    
    public init(name: String, ignored: Bool) {
        self.name = name
        print("Customer initialized with value \(name)")
    }
    convenience public init(_ name: String, _ ignored: Bool) {
        self.init(name: name, ignored: ignored)
    }
}
 
 

public class HasSavedInput2 {
    
    public var name: String
    
    
    public init(name: String, ignored: Bool) {
        self.name = name
        print("Customer initialized with value \(name)")
    }
    convenience public init(_ name: String, _ ignored: Bool) {
        self.init(name: name, ignored: ignored)
    }
}
 
 
 
 

public func main(args: Array<String>) -> Void {
    Empty()
    Empty2()
    Empty3()
    HasInput(name: "Bob", ignored: false)
    HasSavedInput(name: "Dan", ignored: false)
    HasSavedInput2(name: "funky thing", ignored: false)
}
public func main(_ args: Array<String>) -> Void {
    return main(args: args)
}
 
