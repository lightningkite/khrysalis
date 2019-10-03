import Foundation
/**
* See https://kotlinlang.org/docs/reference/control-flow.html
*/

//
func main(args: [String]) {
    cases(x : 1)
    cases(x : 2)
    cases(x : 3)
    cases(x : 4)
}
func main(_ args: [String]) { main(args: args) }

func cases(x: Int) {
    switch (x) {
        case 1:
        print("x == 1")
        case 2:
        print("x == 2")
        default:
        
        print("x is neither 1 nor 2")
        print("x might be something more")
        
        
    }
    
    switch (x) {
        case 0, 1:
        print("x == 0 or x == 1")
        default:
        print("otherwise")
        
    }
    
    when {
        case x > 2:
        print("Bigger than 2")
        case x < 2:
        print("Less than 2")
        case x == 2:
        print("Is 2")
    }
    
    var y: Any = x
    switch(y) {
        case is Int:
        print(y + 2)
        default:
        print(y)
        
    }
}
func cases(_ x: Int) { cases(x: x) }
