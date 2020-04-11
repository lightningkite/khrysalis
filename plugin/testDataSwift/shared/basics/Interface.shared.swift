//Package: com.test
//Converted using Khrysalis2

import Foundation



public protocol MyInterface {
    
    var x: String { get }
    
    func y(string: String) -> String
    func y(_ string: String) -> String
}

public extension MyInterface {
    public var x: String {
        get {
            return ""
        }
    }
    
    func y(string: String) -> String {
        return x + string
    }
    func y(_ string: String) -> String {
        return y(string: string)
    }
}
 
 

public class ImplOverX: MyInterface {
    
    
    public var x: String {
        get {
            return "Hello!"
        }
    }
    
    public init() {
    }
}
 
 

public class ImplOverY: MyInterface {
    
    
    
    public func y(string: String) -> String {
        return "\(x)!"
    }
    public func y(_ string: String) -> String {
        return y(string: string)
    }
    
    public init() {
    }
}
 
 

public class ImplBoth: MyInterface {
    
    
    public var x: String {
        get {
            return "Hello!"
        }
    }
    
    public func y(string: String) -> String {
        return "\(x)!"
    }
    public func y(_ string: String) -> String {
        return y(string: string)
    }
    
    public init() {
    }
}
 
 

public func main() -> Void {
    var items: Array<MyInterface> = [ImplBoth(), ImplOverX(), ImplOverY()]
    
    for item in items {
        print(item.x)
        print(item.y("Input"))
    }
}
 
