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
    takesLambda({ [unowned self] (integer, someValue) in 
        print("Hello!")
    })
}
 
 

public func takesLambda(lambda: @escaping (_ integer: Int32, _ someValue: String) -> Void) -> Void {
}
 
 

public func takesPainfulLambda(lambda: @escaping (_ integer: Int32, _ someValue: @escaping (String) -> Void) -> Void) -> Void {
}
 
 

public func takesEscapingLambda(lambda: @escaping (_ integer: Int32, _ someValue: String) -> Void) -> Void {
}
 
 

public func takesEscapingLambda2(lambda: (@escaping (_ integer: Int32, _ someValue: String) -> Void)) -> Void {
}
 
 

public class ConstructorHasEscaping {
    
    public var lambda:  (_ integer: Int32, _ someValue: String) -> Void
    
    
    public init(lambda: @escaping (_ integer: Int32, _ someValue: String) -> Void) {
        self.lambda = lambda
    }
    convenience public init(_ lambda: @escaping (_ integer: Int32, _ someValue: String) -> Void) {
        self.init(lambda: lambda)
    }
}
 
 
 

public class ConstructorHasEscaping2 {
    
    public var lambda: (@escaping (_ integer: Int32, _ someValue: String) -> Void)
    
    
    public init(lambda: (@escaping (_ integer: Int32, _ someValue: String) -> Void)) {
        self.lambda = lambda
    }
    convenience public init(_ lambda: (@escaping (_ integer: Int32, _ someValue: String) -> Void)) {
        self.init(lambda: lambda)
    }
}
 
 

public class HeavenHelpUsAll {
    
    public var lambda:  (_ integer: Int32, _ someValue: @escaping (Int32) -> Void) -> Void
    
    
    public init(lambda: @escaping (_ integer: Int32, _ someValue: @escaping (Int32) -> Void) -> Void) {
        self.lambda = lambda
    }
    convenience public init(_ lambda: @escaping (_ integer: Int32, _ someValue: @escaping (Int32) -> Void) -> Void) {
        self.init(lambda: lambda)
    }
}
 
