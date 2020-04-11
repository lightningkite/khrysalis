//Package: com.test
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



public class User: Codable, Equatable, Hashable {
    
    public var name: String
    public var address: String
    public var age: Int32
    public var admin: Bool
    
    public static func == (lhs: User, rhs: User) -> Bool {
        return lhs.name == rhs.name &&
            lhs.address == rhs.address &&
            lhs.age == rhs.age &&
            lhs.admin == rhs.admin
    }
    public func hash(into hasher: inout Hasher) {
        hasher.combine(name)
        hasher.combine(address)
        hasher.combine(age)
        hasher.combine(admin)
    }
    public func copy(
        name: (String)? = nil,
        address: (String)? = nil,
        age: (Int32)? = nil,
        admin: (Bool)? = nil
    ) -> User {
        return User(
            name: name ?? self.name,
            address: address ?? self.address,
            age: age ?? self.age,
            admin: admin ?? self.admin
        )
    }
    
    
    public init(name: String = "", address: String = "", age: Int32 = 52, admin: Bool) {
        self.name = name
        self.address = address
        self.age = age
        self.admin = admin
    }
    convenience public init(_ name: String, _ address: String = "", _ age: Int32 = 52, _ admin: Bool) {
        self.init(name: name, address: address, age: age, admin: admin)
    }
    required public init(from decoder: Decoder) throws {
        let values = try decoder.container(keyedBy: CodingKeys.self)
        name = try values.decodeIfPresent(String.self, forKey: .name) ?? ""
        address = try values.decodeIfPresent(String.self, forKey: .address) ?? ""
        age = try values.decodeIfPresent(Int32.self, forKey: .age) ?? 52
        admin = try values.decode(Bool.self, forKey: .admin)
    }
    
    enum CodingKeys: String, CodingKey {
        case name = "name"
        case address = "address"
        case age = "age"
        case admin = "admin"
    }
    
    public func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encode(self.name, forKey: .name)
        try container.encode(self.address, forKey: .address)
        try container.encode(self.age, forKey: .age)
        try container.encode(self.admin, forKey: .admin)
    }
    
}
 
 

public func main(args: Array<String>) -> Void {
    var bob = User(name: "bob", address: "london", age: 45, admin: false)
    print(bob.toString())
    bob.age += 3
    print(bob.toString())
}
public func main(_ args: Array<String>) -> Void {
    return main(args: args)
}
 
