package com.test

import com.lightningkite.khrysalis.Codable
import com.lightningkite.khrysalis.AnyHashable

private data class CodablePoint(val x: Double, val y: Double): Codable
private data class CodableBox<T>(val desc: String, val item: T): Codable where T: Codable, T: AnyHashable