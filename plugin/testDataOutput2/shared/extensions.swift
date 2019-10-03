//Package: com.test
//Converted using Kwift2

import Foundation



public protocol SomeInterface {
}
 
 

extension Int32 {
    public func extensionPlusOne() -> Int32 {
        return self + 1
    }
}
 
 

extension Array {
    public func estimateWork() -> Int32 {
        return self.size
    }
}
 
 

extension Array where OVERRIDE == SomeInterface {
    public func estimateWork2() -> Int32 {
        return self.size
    }
}
 
 

extension Array where OVERRIDE: SomeInterface {
    public func estimateWork3() -> Int32 {
        return self.size
    }
}
 
 

extension Array where S ==  SomeInterface {
    public func estimateWork4() -> Int32 {
        return self.size
    }
}
 
 

extension Array where S:  SomeInterface {
    public func estimateWork5() -> Int32 {
        return self.size
    }
}
 
 

extension Array {
    public func estimateWork<A>(method: A, how: (Element, A) -> Int32) -> Int32 {
        var amount = 0
        
        for item in self {
            amount += how(item, method)
        }
        return amount
    }
    public func estimateWork<A>(_ method: A, _ how: (Element, A) -> Int32) -> Int32 {
        return estimateWork(method: method, how: how)
    }
}
 
