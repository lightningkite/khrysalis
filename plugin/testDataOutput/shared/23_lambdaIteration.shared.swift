//Package: null
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



extension Array {
    public func myMap<R>(transform: (T) -> R) -> Array<R> {
        var result = Array<R>
        
        for item in self {
            result.add(transform(item))
        }
        return result
    }
}
 
 

public func myMax<T>(collection: Collection<T>, less: (T, T) -> Bool) -> T?  {
    var max: T?  = nil
    
    for it in collection {
        if max == nil || less(max, it) {
            max = it
        }
    }
    return max
}
public func myMax<T>(_ collection: Collection<T>, _ less: (T, T) -> Bool) -> T?  {
    return myMax(collection: collection, less: less)
}
 
 

public func main(args: Array<String>) -> Void {
    var ints = [1, 2, 3, 4, 10, 0]
    var ints2 = [1, 2, 3, 4, 10, 0]
    var doubled1 = ints.myMap{ (element) in 
        element * 2
    }
    var doubled2 = ints.myMap{ () in 
        it * 2
    }
    print(ints)
    print(doubled1)
    print(doubled2)
    print(myMax(ints2, less: { (a, b) in 
        a < b
    }))
    print(myMax(doubled1, less: { (a, b) in 
        a < b
    }))
    print(ints.map{ () in 
        "\(it)€"
    })
    print(ints.map({ () in 
        "\(it)€"
    }))
    ints.forEach{ (intVal) in 
        print(intVal)
    }
}
public func main(_ args: Array<String>) -> Void {
    return main(args: args)
}
 
