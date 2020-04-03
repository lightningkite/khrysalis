//Package: com.lightningkite.khrysalis.async
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



public typealias DRF<T> = DelayedResultFunction<T>
 

public class DelayedResultFunction<T> {
    
    public var value:  (@escaping (T) -> Void) -> Void
    
    
    public func invoke(callback: @escaping (T) -> Void) -> Void {
        value(callback)
    }
    
    public init(value: @escaping (@escaping (T) -> Void) -> Void) {
        self.value = value
    }
    convenience public init(_ value: @escaping (@escaping (T) -> Void) -> Void) {
        self.init(value: value)
    }
}
 
 

public func immediate<T>(action: @escaping () -> T) -> DelayedResultFunction<T> {
    return DelayedResultFunction{ (onResult) in 
        onResult(action())
    }
}
 

public func drfStart<T>(value: T) -> DelayedResultFunction<T> {
    return DelayedResultFunction{ (onResult) in 
        onResult(value)
    }
}
public func drfStart<T>(_ value: T) -> DelayedResultFunction<T> {
    return drfStart(value: value)
}
 
 

extension DelayedResultFunction {
    public func then<B>(next: (T) -> DelayedResultFunction<B>) -> DelayedResultFunction<B> {
        var first = self
        return DelayedResultFunction{ (onResult) in 
            first.invoke{ (input) in 
                next(input).invoke(onResult)
            }
        }
    }
}
 

extension DelayedResultFunction {
    public func thenImmediate<B>(next: (T) -> B) -> DelayedResultFunction<B> {
        var first = self
        return DelayedResultFunction < B > { (onResult) in 
            first.invoke{ (input) in 
                onResult(next(input))
            }
        }
    }
}
 
 

public func test() -> Void {
    drfStart(2).then{ (a) in 
        immediate{ () in 
            4 + a
        }
    }.thenImmediate{ (a) in 
        4 + a
    }.then{ (a) in 
        DelayedResultFunction{ (onResult) in 
            onResult(a.toDouble() + 4.0)
        }
    }.invoke{ () in 
        print(it)
    }
}
 
