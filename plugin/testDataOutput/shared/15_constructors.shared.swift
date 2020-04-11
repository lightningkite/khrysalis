//Package: null
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



public class User {
    
    public var name: String
    public var admin: Bool
    
    
    public func printHello() -> Void {
        if !admin {
            print("Hello " + name)
        } else {
            print("You are an admin, \(name)")
        }
    }
    
    public init(name: String, admin: Bool = false) {
        self.name = name
        self.admin = admin
    }
    convenience public init(_ name: String, _ admin: Bool = false) {
        self.init(name: name, admin: admin)
    }
}
 
 

public class House {
    
    public var name: String
    
    public var address
    
    public init(name: String, addressPrefix: String) {
        self.name = name
        let address = ""
        self.address = address
        self.address = "\(addressPrefix): \(name)"
    }
    convenience public init(_ name: String, _ addressPrefix: String) {
        self.init(name: name, addressPrefix: addressPrefix)
    }
}
 
 

public func main(args: Array<String>) -> Void {
    var bob = User(name: "Bob")
    bob.printHello()
    var john = User(name: "John", admin: true)
    john.printHello()
    var house = House(name: "Bridge", addressPrefix: "Name")
    print(house.address + " (" + house.name + ")")
}
public func main(_ args: Array<String>) -> Void {
    return main(args: args)
}
 
