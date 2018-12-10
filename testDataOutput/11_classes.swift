/**
 * See https://kotlinlang.org/docs/reference/classes.html
 */

class Empty{init() {
}
}

class Empty2 {

    init() {
 
        print("Hello!")
    }
}

class Empty3 {


    init() {
 
        print("Hello ")
     
        print("world!")
    }
}

class HasInput {

    init(name: String, ignored: Bool) {
 
        print("Customer initialized with value \(name)")
    }
}

class HasSavedInput {

    let name: String
    init( name: String, ignored: Bool) {
        self.name = name 
        print("Customer initialized with value \(name)")
    }
}

class HasSavedInput2 {

    var name: String
    init( name: String, ignored: Bool) {
        self.name = name 
        print("Customer initialized with value \(name)")
    }
}



func main(args: [String]) {
    Empty()
    Empty2()
    Empty3()
    HasInput(name : "Bob", ignored : false)
    HasSavedInput(name : "Dan", ignored : false)
    HasSavedInput2(name : "funky thing", ignored : false)
}
