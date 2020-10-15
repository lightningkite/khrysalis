package com.test

import com.lightningkite.butterfly.*

private data class CodablePoint(@JsonProperty("better_x") val x: Double, val y: Double): Codable
private data class CodableBox<T>(val desc: String, val item: T): Codable where T: Codable, T: AnyHashable
enum class CodableEnum : Codable {
    @JsonProperty("my_enum") MyEnum
}