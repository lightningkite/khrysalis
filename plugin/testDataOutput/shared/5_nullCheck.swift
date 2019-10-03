import Foundation
/**
* A reference must be explicitly marked as nullable to be able hold a null.
* See http://kotlinlang.org/docs/reference/null-safety.html#null-safety
*/
//package multiplier


func main(args: [String]) {
    var x: Int? = nil
    
    //With Braces
    if let x = x {
        print("x: \(x)")
    } else {
        print("X is null")
    }
    
    //Without braces
    if let x = x print("x: \(x)") else println("X is null")
}
func main(_ args: [String]) { main(args: args) }
