import java.util.*

func Float.timesTen() -> Float {
  return this * 10
}

func [Float]().avg
.size




public  {

    let name: String

    let age: Int

    init( name: String, age: Int) {

        self.name = name
        self.age = age
    }
}

func [Person]().countAdults() -> Int {
  var adultCounter = 0
  for  person in this  {
    if  person.age >= 18 {
      adultCounter++
    }
  }
  return adultCounter
}

func main(args: [String]) {
  let list = [Float]()
  list.add(1.5f.timesTen())
  list.add(1f.timesTen())
  list.add(11.858502f)
  list.add(3.1415f)
  let avg1 = list.avg()
  print("avg1 = \(avg1) (should be 10.0)")

  let list2 = LinkedList<Float>()
  list2.add(15.0f)
  list2.add(5.0f)
  print("avg2 = \(list2.avg()) (should be 10.0)")

  let people = [Person]()
  people.add(Person(name : "Steve", age : 14))
  people.add(Person(name : "Bob", age : 16))
  people.add(Person(name : "John", age : 18))
  people.add(Person(name : "Lena", age : 20))
  people.add(Person(name : "Denise", age : 22))
  people.add(Person(name : "Alex", age : 24))
  print("\(people.countAdults()) people may enter the club (should be 4)")
}
