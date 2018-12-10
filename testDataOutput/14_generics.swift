public class Box<T> {

  init(t: T) {
 
    value = t
  
  }
  var value: T // value = t won't work, as this must happen in swift inside constructor

  func doNothing(value2: T) -> T {
    return value
  }


}

public class BigBox<T> {

  init(t: T) {
 
    value1 = t
    value2 = t
  
  }
  var value1: T
  let value2: T


}

func main(args: [String]) {
  let box: Box<Int> = Box<Int>(t : 1)
  print(box)

  let box2 = Box(t : 2)
  print(box2)

  print(box.doNothing(value2 : 4))

  let bigBox = BigBox(t : 3)
  print(bigBox.value1 + bigBox.value2)
}
