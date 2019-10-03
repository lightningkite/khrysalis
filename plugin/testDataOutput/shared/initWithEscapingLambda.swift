import Foundation
//package com.test


public final class InitWithEscapingLambda: Equatable, Hashable{
    public var listener: ()->Void
    
    init( listener: @escaping ()->Void ) {
        self.listener = listener
    }
    convenience init(
        _ listener: @escaping ()->Void
    ){ 
        self.init(
            listener: listener
        ) 
    }
    
    public static func == (lhs: InitWithEscapingLambda, rhs: InitWithEscapingLambda) -> Bool {
        return 
        lhs.listener == rhs.listener
    }
    
    
    public var hashValue: Int {
        return 
        listener.hashValue
    }
    
    
    public func copy(
        listener: (()->Void)? = nil
    ) -> InitWithEscapingLambda {
        return InitWithEscapingLambda(
            listener: listener ?? self.listener
        )
    }
    
}
