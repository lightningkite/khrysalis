@file:SharedCode
package com.test.codable

import com.lightningkite.butterfly.*
import com.lightningkite.butterfly.Codable

data class Point(val x: Double, val y: Double): Codable
data class Box<T: IsCodableAndHashable>(val description: String, var item: T): Codable
enum class CodableEnum {
    A
}

fun main(vararg args: String){
    println(Point(2.0, 1.0).x.toInt())
}