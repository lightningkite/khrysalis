// Simple Interface

public interface MyInterface {

  init() {

  }
  func bar() -> String
  var x: Int = 0
  /*fun foo(): Int {
    return 1
  }*/
}

public class Implementation : MyInterface {

  init() {

  }
   func bar() -> String {
    return "2-Implementation"
  }
}

// Interface + Inheritance

public open class Parent {

  init() {

  }
  func three() -> Int {
    return 3
  }

  var four = 4
}

public class Child: Parent(), MyInterface {

  init() {

      super.init()
  }
  let five = 5
  func six() -> String {
    return "6"
  }
   func bar() -> String {
    return "2-Child"
  }
}

// Abstract Interface + Inheritance

public abstract class AbstractParent {

  init() {

  }
  abstract func three() -> Int

  var four = 4
}

public abstract class AbstractChild: Parent() {

    init() {

        super.init()
    }
}

public class NonAbstractChild: AbstractChild(), MyInterface {

  init() {

      super.init()
  }
   func bar() -> String {
    return "2-NonAbstractChild"
  }
}

func main(args: [String]) {
  // Simple Interface
  let obj = Implementation()
  print(obj.foo())
  print(obj.bar())

  // Interface + Inheritance
  let child = Child()
  print(child.foo())
  print(child.bar())
  print(child.three())
  print(child.four)
  print(child.five)
  print(child.six())

  // Abstract Interface + Inheritance
  let naChild = NonAbstractChild()
  print(naChild.foo())
  print(naChild.bar())
  print(naChild.three())
  print(naChild.four)
}
