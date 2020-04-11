//Package: null
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



public class Box<T> {
    
    
    public var value: T
    
    public func doNothing(value2: T) -> T {
        return value
    }
    public func doNothing(_ value2: T) -> T {
        return doNothing(value2: value2)
    }
    
    public init(t: T) {
        value = t
    }
    convenience public init(_ t: T) {
        self.init(t: t)
    }
}
 
 

public class BigBox<T> {
    
    
    public var value1: T
    public var value2: T
    
    public init(t: T) {
        value1 = t
        value2 = t
    }
    convenience public init(_ t: T) {
        self.init(t: t)
    }
}
 
 

public func main(args: Array<String>) -> Void {
    var box: Box<Int32> = Box(Int32.self, t: 1)
    print(box)
    var box2 = Box(t: 2)
    print(box2)
    print(box.doNothing(value2: 4))
    var bigBox = BigBox(t: 3)
    print(bigBox.value1 + bigBox.value2)
}
public func main(_ args: Array<String>) -> Void {
    return main(args: args)
}
 
