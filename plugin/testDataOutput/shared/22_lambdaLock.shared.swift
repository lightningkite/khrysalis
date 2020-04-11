//Package: null
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



public class Lock {
    
    
    
    public func lock() -> Void {
        print("  locked")
    }
    
    public func unlock() -> Void {
        print("  unlocked")
    }
    
    public init() {
    }
}
 
 

public func lock<T>(lock: Lock, body: () -> T) -> T {
    lock.lock()
    var x = body()
    lock.unlock()
    return x
}
public func lock<T>(_ lock: Lock, _ body: () -> T) -> T {
    return lock(lock: lock, body: body)
}
 
 

public func main(args: Array<String>) -> Void {
    var lockObj = Lock()
    print("before lock")
    lock(lockObj){ () in 
        print("    currently locked")
    }
    print("after lock")
}
public func main(_ args: Array<String>) -> Void {
    return main(args: args)
}
 
