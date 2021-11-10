package com.lightningkite.khrysalis

typealias ColorValue = Int

fun Int.asColor(): ColorValue = this
fun Long.asColor(): ColorValue = this.toInt()
fun colorValue(value: Long): ColorValue = value.toInt()


fun ColorValue.colorAlpha(desiredAlpha: Int): ColorValue = (this and 0xFF000000.toInt() or (desiredAlpha shl 24))
