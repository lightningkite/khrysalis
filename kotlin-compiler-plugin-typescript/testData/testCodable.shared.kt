package com.test.codable

import com.lightningkite.khrysalis.Codable
import com.test.annot.JsonProperty

data class Point(@JsonProperty("x_better") val x: Double, val y: Double): Codable
data class Box<T>(val description: String, var item: T): Codable
enum class CodableEnum {
    @JsonProperty("VeryA") A
}