//Package: null
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



extension Float {
    public func timesTen() -> Float {
        return self * 10
    }
}
 
 

extension Array where Element == Float {
    public func avg() -> Float {
        if self.size == 0 {
            return Float(0)
        }
        var sum = Float(0.0)
        
        for item in self {
            sum += item
        }
        return sum / self.size
    }
}
 
 

public class Person {
    
    public var name: String
    public var age: Int32
    
    
    public init(name: String, age: Int32) {
        self.name = name
        self.age = age
    }
    convenience public init(_ name: String, _ age: Int32) {
        self.init(name: name, age: age)
    }
}
 
 

extension Array where Element: Person {
    public func countAdults() -> Int32 {
        var adultCounter = 0
        
        for person in self {
            if person.age >= 18 {
                adultCounter += 1
            }
        }
        return adultCounter
    }
}
 
 

public func main(args: Array<String>) -> Void {
    var list = Array(Float.self)
    list.add(Float(1.5).timesTen())
    list.add(Float(1).timesTen())
    list.add(Float(11.858502))
    list.add(Float(3.1415))
    var avg1 = list.avg()
    print("avg1 = \(avg1) (should be 10.0)")
    var list2 = LinkedList(Float.self)
    list2.add(Float(15.0))
    list2.add(Float(5.0))
    print("avg2 = \(list2.avg()) (should be 10.0)")
    var people = Array(Person.self)
    people.add(Person(name: "Steve", age: 14))
    people.add(Person(name: "Bob", age: 16))
    people.add(Person(name: "John", age: 18))
    people.add(Person(name: "Lena", age: 20))
    people.add(Person(name: "Denise", age: 22))
    people.add(Person(name: "Alex", age: 24))
    print("\(people.countAdults()) people may enter the club (should be 4)")
}
public func main(_ args: Array<String>) -> Void {
    return main(args: args)
}
 
