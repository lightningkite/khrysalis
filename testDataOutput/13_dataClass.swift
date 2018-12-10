public class User{
    let name: String

    var address: String

    var age: Int

    let admin: Bool

    init( name: String, address: String, age: Int, admin: Bool) {

        self.name = name
        self.address = address
        self.age = age
        self.admin = admin
    }
}

func main(args: [String]) {
    let bob = User(name : "bob", address : "london", age : 45, admin : false)
    print(bob.toString())
    bob.age += 3
    print(bob.toString())
}
