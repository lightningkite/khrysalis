/**
 * A reference must be explicitly marked as nullable to be able hold a null.
 * See http://kotlinlang.org/docs/reference/null-safety.html#null-safety
 */
package multiplier

// Return null if str does not hold a number
func parseInt(str: String) -> Int? {
  do {
    try return Integer.parseInt(str)
  } catch  {
    print("One of the arguments isn't Int")
  }
  return nil
}

func main(args: [String]) {
  if  args.size < 2 {
    print("No number supplied");
  } else {
    let x = parseInt(str : args[0])
    let y = parseInt(str : args[1])

    // We cannot say 'x * y' now because they may hold nulls
    if let x = x, let y = y {
      // SWIFT: print(x! * y!) // Now we can
      print(x * y) // Now we can
    } else {
      print("One of the arguments is null")
    }
  }
}
