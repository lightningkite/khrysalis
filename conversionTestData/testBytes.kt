@file:SharedCode
package com.test.bytes

import java.nio.*
import com.lightningkite.khrysalis.*
import java.util.Base64

fun main(){
    println("Executing...")
    val bytes = "This is a test".toByteArray()
    val base64 = Base64.getEncoder().encodeToString(bytes)
    println(base64)
    val reversed = Base64.getDecoder().decode(base64).toString(Charsets.UTF_8)
    println(reversed)
    val firstByte = bytes[0]
    println(firstByte)
    bytes[0] = 66
    println(bytes.toString(Charsets.UTF_8))

    val buffer = ByteBuffer.allocate(128)
    println("Putting values...")
    println("Position: ${buffer.position()}")
    buffer.put(1)
    println("Position: ${buffer.position()}")
    buffer.putShort(2)
    println("Position: ${buffer.position()}")
    buffer.putInt(3)
    println("Position: ${buffer.position()}")
    buffer.putLong(4)
    println("Position: ${buffer.position()}")
    buffer.putFloat(5f)
    println("Position: ${buffer.position()}")
    buffer.putDouble(6.0)
    println("Position: ${buffer.position()}")
    buffer.put(bytes)
    println("Position: ${buffer.position()}")
    buffer.position(0)
    println("Position: ${buffer.position()}")
    println("Getting values...")
    println("Position: ${buffer.position()}")
    println(buffer.get())
    println("Position: ${buffer.position()}")
    println(buffer.getShort())
    println("Position: ${buffer.position()}")
    println(buffer.getInt())
    println("Position: ${buffer.position()}")
    println(buffer.getLong())
    println("Position: ${buffer.position()}")
    println(buffer.getFloat().toInt())
    println("Position: ${buffer.position()}")
    println(buffer.getDouble().toInt())
    println("Position: ${buffer.position()}")
    buffer.get(bytes)
    println(bytes.toString(Charsets.UTF_8))
}