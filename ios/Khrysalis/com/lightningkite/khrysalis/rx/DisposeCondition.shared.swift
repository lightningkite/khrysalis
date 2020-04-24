//Package: com.lightningkite.khrysalis.rx
//Converted using Khrysalis2

import Foundation
import Khrysalis
import RxSwift
import RxRelay



public class DisposeCondition {
    
    public var call:  (Disposable) -> Void
    
    
    public init(call: @escaping (Disposable) -> Void) {
        self.call = call
    }
    convenience public init(_ call: @escaping (Disposable) -> Void) {
        self.init(call: call)
    }
}
 
 

extension DisposeCondition {
    public func and(other: DisposeCondition) -> DisposeCondition {
        return andAllDisposeConditions([self, other])
    }
    public func and(_ other: DisposeCondition) -> DisposeCondition {
        return and(other: other)
    }
}
 
 

public func andAllDisposeConditions(list: Array<DisposeCondition>) -> DisposeCondition {
    return DisposeCondition{ (it) in 
        var disposalsLeft = list.size
        
        for item in list {
            item.call(DisposableLambda{ () in 
                disposalsLeft -= 1
                if disposalsLeft == 0 {
                    it.dispose()
                }
            })
        }
    }
}
public func andAllDisposeConditions(_ list: Array<DisposeCondition>) -> DisposeCondition {
    return andAllDisposeConditions(list: list)
}
 
 

extension DisposeCondition {
    public func or(other: DisposeCondition) -> DisposeCondition {
        return DisposeCondition{ (it) in 
            self.call(it)
            other.call(it)
        }
    }
    public func or(_ other: DisposeCondition) -> DisposeCondition {
        return or(other: other)
    }
}
 
