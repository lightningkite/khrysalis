//Package: null
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



public func main(args: Array<String>) -> Void {
    print(getStringLength(obj: "aaa"))
    print(getStringLength(obj: 1))
}
public func main(_ args: Array<String>) -> Void {
    return main(args: args)
}
 
 

public func getStringLength(obj: Any) -> Int32?  {
    if let obj = obj as? String {
        return obj.length
    }
    return nil
}
public func getStringLength(_ obj: Any) -> Int32?  {
    return getStringLength(obj: obj)
}
 
