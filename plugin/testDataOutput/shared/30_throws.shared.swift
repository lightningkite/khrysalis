//Package: null
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



 public func foo() -> Void {
    throw java.lang.IllegalStateException("Do not call foo")
}
 

 public func bar() -> Void {
    throw java.lang.IllegalStateException("Do not call bar")
}
 

 public func fooReturns() -> String {
    throw java.lang.IllegalStateException("Do not call fooReturns")
}
 

 public func barReturns() -> Int32 {
    throw java.lang.IllegalStateException("Do not call bar")
}
 
 

public func main(args: Array<String>) -> Void {
    do { 
 foo() 
 bar() 
 var x = fooReturns() 
 var y = barReturns() } catch ( e : Swift.Error ) { 
 print("Success") 
 }
}
public func main(_ args: Array<String>) -> Void {
    return main(args: args)
}
 
