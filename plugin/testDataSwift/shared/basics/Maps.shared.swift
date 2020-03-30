//Package: com.test
//Converted using Khrysalis2

import Foundation



public func main() -> Void {
    var map: Dictionary<Int32, String> = [1: "A", 2: "B", 3: "C"]
    assert(map[ 1 ] == "A")
    assert(map[ 2 ] == "B")
    assert(map[ 3 ] == "C")
    assert(map[ 0 ] == nil)
    var mutableMap: Dictionary<Int32, String> = [1: "A", 2: "B", 3: "C"]
    assert(mutableMap[ 1 ] == "A")
    assert(mutableMap[ 2 ] == "B")
    assert(mutableMap[ 3 ] == "C")
    assert(mutableMap[ 0 ] == nil)
    mutableMap[ 0 ] = "-"
    assert(mutableMap[ 0 ] == "-")
    mutableMap.remove(0)
    assert(mutableMap[ 0 ] == nil)
    mutableMap.put(0, "x")
    assert(mutableMap[ 0 ] == "x")
}
 
