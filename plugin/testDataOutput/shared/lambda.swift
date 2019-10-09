//Package: com.test
//Converted using Kwift2

import Foundation



public func lambdaTest() -> Void {
    takesLambda(lambda: { (integer, someValue) in 
        print("Hello!")
    })
    takesLambda{ (integer, someValue) in 
        print("Hello!")
    }
}
 
 

public func takesLambda(lambda: (integer: Int32, someValue: String) -> Void) -> Void {
}
 
 

public func takesEscapingLambda(lambda: @escaping (integer: Int32, someValue: String) -> Void) -> Void {
}
 
 

public func takesEscapingLambda2(lambda: (@escaping (integer: Int32, someValue: String) -> Void)) -> Void {
}
 
 

public class ConstructorHasEscaping {
    
    public var lambda:  (integer: Int32, someValue: String) -> Void
    
    
    public init(lambda: @escaping (integer: Int32, someValue: String) -> Void) {
        self.lambda = lambda
    }
    convenience public init(_ lambda: @escaping (integer: Int32, someValue: String) -> Void) {
        self.init(lambda: lambda)
    }
}
 
 
 

public class ConstructorHasEscaping2 {
    
    public var lambda: ( (integer: Int32, someValue: String) -> Void)
    
    
    public init(lambda: (@escaping (integer: Int32, someValue: String) -> Void)) {
        self.lambda = lambda
    }
    convenience public init(_ lambda: (@escaping (integer: Int32, someValue: String) -> Void)) {
        self.init(lambda: lambda)
    }
}
 
