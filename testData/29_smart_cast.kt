import java.util.*

fun main(args: Array<String>) {
  val x: Int? = 1
  var y: Int = 2
  val z: Int? = null

  if (x != null) {
    y += x
  } else {
    println("FAIL: x should not be null")
  }

  if (z != null) {
    println("FAIL: z should be null")
    y += z
  }

  if (y != 3) {
    println("FAIL: y should be 3, is $y")
  }

  if (z is Int) {
    println("FAIL: z should be null")
  }

  if(x != null && z is Int && y != 3) {
    println("CHAOS!s!.s")
  }

  println("Success")
}
