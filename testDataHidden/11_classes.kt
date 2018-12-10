/**
 * See https://kotlinlang.org/docs/reference/classes.html
 */

class Empty

class Empty2 {
    init {
        println("Hello!")
    }
}

class Empty3 {
    init {
        println("Hello ")
    }
    init {
        println("world!")
    }
}

class HasInput(name: String, ignored: Boolean) {
    init {
        println("Customer initialized with value ${name}")
    }
}

class HasSavedInput(val name: String, ignored: Boolean) {
    init {
        println("Customer initialized with value ${name}")
    }
}

class HasSavedInput2(var name: String, ignored: Boolean) {
    init {
        println("Customer initialized with value ${name}")
    }
}



fun main(args: Array<String>) {
    Empty()
    Empty2()
    Empty3()
    HasInput(name = "Bob", ignored = false)
    HasSavedInput(name = "Dan", ignored = false)
    HasSavedInput2(name = "funky thing", ignored = false)
}
