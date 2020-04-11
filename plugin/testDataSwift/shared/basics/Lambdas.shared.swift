//Package: com.test
//Converted using Khrysalis2

import Foundation



public func main() -> Void {
    var lambda: (String, String) -> Void = { (word1, word2) in 
        print("\(word1) \(word2)!")
    }
    lambda("Hello", "world")
}
 
