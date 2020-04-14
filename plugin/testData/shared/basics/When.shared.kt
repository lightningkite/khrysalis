package com.test

fun main() {
    var number: Int = 0
    var value = 43.toInt()
    when (value) {
        1 -> number += 1
        2 -> {
            number += 2
            number += 3
        }
        43 -> {
            number += 4
        }
        else -> {
            number -= 99
        }
    }
    when {
        value == 42 -> number += 8
        value == 41 -> {
            number += 16
        }
        value == 43, value == 44 -> number += 32
        else -> { }
    }
    println(number)

    number = when {
        value == 42 -> 8
        value == 41 -> {
            16
        }
        value == 43, value == 44 ->  32
        else -> { 0}
    }
    println(number)

    println(
        when (value) {
            0 -> "Hi"
            else -> {
                "Nope"
            }
        }
    )
}
