open class Base {
    init() {
 
        print("1")
    }
    open func v() {
        print("ERROR-Open")
    }

    func nv() {
        print("4")
    }


}

open class Derived : Base() {
    init() {
        super.init() 
        print("2")
    }
    override func v() {
        print("ERROR-Derived")
    }

    func x() {
        print("6")
    }


}

class Derived2 : Derived() {
    init() {
        super.init() 
        print("3")
    }
    override func v() {
        print("5")
    }

    func y() {
        print("7")
    }


}

func main(args: [String]) {
    let x = Derived2()
    x.nv()
    x.v()
    x.x()
    x.y()
}
