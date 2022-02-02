@file:SharedCode

package com.test.codable

import com.lightningkite.khrysalis.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.serialization.builtins.*

@Serializable
data class Point(val x: Double, val y: Double)
@Serializable
data class Box<T : IsCodableAndHashable>(val description: String, var item: T)
@Serializable
enum class CodableEnum {
    A
}

fun main(vararg args: String) {
    val x: KSerializer<Point> = Point.serializer()
    val y: KSerializer<List<Point>> = ListSerializer(Point.serializer())
    val z: KSerializer<Box<Point>> = Box.serializer(Point.serializer())

    val j: Json = Json {}
    j.encodeToString(x, Point(x = 1.0, y = 2.0))
    j.decodeFromString(x, """{"x": 1.0, "y": 2.0}""")
    j.encodeToString(y, listOf(Point(x = 1.0, y = 2.0)))
    j.decodeFromString(y, """[{"x": 1.0, "y": 2.0}]""")
    j.encodeToString(Point(x = 1.0, y = 2.0))
    val pt: Point = j.decodeFromString("""{"x": 1.0, "y": 2.0}""")
    j.encodeToString(listOf(Point(x = 1.0, y = 2.0)))
    val pts: List<Point> = j.decodeFromString("""[{"x": 1.0, "y": 2.0}]""")

    val serializer = Point.serializer()
}