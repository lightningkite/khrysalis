//Package: com.test
//Converted using Kwift2

import Foundation


public func lambdaTest() -> Void {
    takesLambda(lambda: { (integer, someValue) in 
        print("Hello!")
    }
)
    takesLambda{ (integer, someValue) in 
        print("Hello!")
    }
}
 
 
public func takesLambda(lambda: (integer: Int, someValue: String) -> Void) -> Void {
}
 
 
public func takesEscapingLambda(lambda: @escaping (integer: Int, someValue: String) -> Void) -> Void {
}
 
 
public func takesEscapingLambda2(lambda: (@escaping (integer: Int, someValue: String) -> Void)) -> Void {
}
 
 
public class ConstructorHasEscaping {
    
    public var lambda:  (integer: Int, someValue: String) -> Void
    
    init(lambda: @escaping (integer: Int, someValue: String) -> Void) {
        self.lambda = lambda
    }
    
}
 
 
 
public class ConstructorHasEscaping2 {
    
    public var lambda: ( (integer: Int, someValue: String) -> Void)
    
    init(lambda: (@escaping (integer: Int, someValue: String) -> Void)) {
        self.lambda = lambda
    }
    
}
 
