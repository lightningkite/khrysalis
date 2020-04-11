//Package: com.test
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



public class InitWithEscapingLambda: Equatable, Hashable {
    
    public var listener: () -> Void
    
    public static func == (lhs: InitWithEscapingLambda, rhs: InitWithEscapingLambda) -> Bool {
        return lhs.listener == rhs.listener
    }
    public func hash(into hasher: inout Hasher) {
        hasher.combine(listener)
    }
    public func copy(
        listener: (() -> Void)? = nil
    ) -> InitWithEscapingLambda {
        return InitWithEscapingLambda(
            listener: listener ?? self.listener
        )
    }
    
    
    public init(listener: () -> Void) {
        self.listener = listener
    }
    convenience public init(_ listener: () -> Void) {
        self.init(listener: listener)
    }
}
 
