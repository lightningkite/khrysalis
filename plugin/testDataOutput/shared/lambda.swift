import Foundation
//package com.test


func lambdaTest() {
    takesLambda(lambda : { (integer, someValue) in
        print("Hello!")
    })
    takesLambda { (integer, someValue) in
        print("Hello!")
    }
}

func takesLambda(lambda: (Int, String) -> Void) {
    
}

func takesEscapingLambda( lambda: @escaping (Int, String) -> Void) {
    
}

final public class ConstructorHasEscaping {
    
    public var lambda: (Int, String) -> Void
    
    init( lambda: @escaping (Int, String) -> Void) {
        self.lambda = lambda
    }
    convenience init(
        _ lambda: @escaping (Int, String) -> Void
    ){ 
        self.init(
            lambda: lambda
        ) 
    }
    
}
