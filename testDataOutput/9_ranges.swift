/**
 * Check if a number lies within a range.
 * Check if a number is out of range.
 * Check if a collection contains an object.
 * See http://kotlinlang.org/docs/reference/ranges.html#ranges
 */

func main(args: [String]) {
    var x = 0
    do {
        try x = Integer.parseInt(args[0])
    } catch  {
    }
    //Check if a number lies within a range:
    let y = 10
    if  x in 1...y - 1 {
        print("OK")
    }

    //Iterate over a range:
    for  a in 1...5  {
        print("\(a) ")
    }

    //Check if a number is out of range:
    print()
    var array = [String]()
    array.add("aaa")
    array.add("bbb")
    array.add("ccc")
}
