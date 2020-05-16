package com.test.codable

import com.lightningkite.khrysalis.Codable

data class Point(val x: Double, val y: Double): Codable
data class Box<T>(val description: String, val item: T): Codable