//Package: null
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



public class FunkyClass {
    
    
    public var internalString
    public var wrappedProperty: String {
        get {
            return "My string is \(internalString)"
        }
        set(value) {
            internalString = "\(value) - previous=\"\(internalString)\""
        }
    }
    
    public init() {
        let internalString = ""
        self.internalString = internalString
    }
}
 
 
public var computedProperty1: Int32 {
    get {
        var a = 0
        a += 1
        return a
    }
}
 
 
public var computedProperty2: Int32 {
    get {
        return 2
    }
}
 
 
 
 
 
public var _backingProperty: Int32 = 0
public var computedProperty4: Int32 {
    get {
        return 4 + _backingProperty
    }
    set(value) {
        _backingProperty = value
    }
}
 
 
public var computedProperty5: Int32 {
    get {
        return 5 + _backingProperty
    }
    set(value) {
        _backingProperty = value
    }
}
 
 
 
 

public func main(args: Array<String>) -> Void {
    var x = FunkyClass()
    print(x.wrappedProperty)
    x.wrappedProperty = "abc"
    print(x.wrappedProperty)
    x.wrappedProperty = "123"
    print(x.wrappedProperty)
    print(computedProperty1)
    print(computedProperty2)
    print(computedProperty4)
    print(computedProperty5)
    computedProperty4 = 1000
    print(computedProperty4)
    print(computedProperty5)
}
public func main(_ args: Array<String>) -> Void {
    return main(args: args)
}
 
