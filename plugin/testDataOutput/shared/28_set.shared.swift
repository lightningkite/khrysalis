//Package: null
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



public func main(args: Array<String>) -> Void {
    var hashSet0 = Set(String.self)
    var linkedHashSet0 = LinkedHashSet(String.self)
    var set0 = emptySet(String.self)
    var set1 = [1]
    var set2 = [1, 2, 1]
    var hashSet = [1]
    var linkedSet = linkedSetOf(2)
    var mutableSet = [3]
    print("\(set1) (1)")
    print("\(set2) (1, 2)")
    print("\(set2.size) (2)")
    hashSet.add(2)
    linkedSet.addAll(linkedSet)
    mutableSet.remove(3)
    print(hashSet)
    if hashSet.size != 2 {
        print("ERROR: hashSet.size")
    }
    print(linkedSet)
    if linkedSet.size != 1 {
        print("ERROR: linkedSet.size")
    }
    print(mutableSet)
    if mutableSet.size != 0 {
        print("ERROR: mutableSet.size")
    }
    if !set0.isEmpty() {
        print("ERROR: set0.isEmpty()")
    }
    print("\(hashSet) ([2, 1])")
    
    for k in set2 {
        print("\(k)")
    }
}
public func main(_ args: Array<String>) -> Void {
    return main(args: args)
}
 
