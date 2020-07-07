package com.test.codable

import com.lightningkite.khrysalis.Codable
import com.lightningkite.khrysalis.AnyHashable

data class Point(val x: Double, val y: Double): Codable
data class Box<T>(val desc: String, val item: T): Codable where T: Codable, T: AnyHashable