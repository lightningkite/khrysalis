/**
 * A reference must be explicitly marked as nullable to be able hold a null.
 * See http://kotlinlang.org/docs/reference/null-safety.html#null-safety
 */
package multiplier

fun main(args: Array<String>) {
    val x: Int? = null

    //With Braces
    if (x != null) {
        println("x: ${x}")
    } else {
        println("X is null")
    }

    //Without braces
    if (x != null) println("x: ${x}") else println("X is null")
}
