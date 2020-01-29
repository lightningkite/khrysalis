package com.lightningkite.khrysalis.bytes

import com.lightningkite.khrysalis.PlatformSpecific
import java.nio.*
import java.nio.ByteBuffer

fun Data.buffer(): ByteBuffer {
    return ByteBuffer.wrap(this)
}

fun ByteBuffer.data(): Data = array()

fun ByteBuffer.getUtf8(): String {
    val length = getShort()
    val byteArray = ByteArray(length.toInt())
    this.get(byteArray)
    return byteArray.toString(Charsets.UTF_8)
}

fun ByteBuffer.getUtf8(index: Int): String {
    val beforePosition = position()
    position(index)
    val length = getShort()
    val byteArray = ByteArray(length.toInt())
    this.get(byteArray)
    position(beforePosition)
    return byteArray.toString(Charsets.UTF_8)
}

fun ByteBuffer.putUtf8(string: String): ByteBuffer {
    putShort(string.length.toShort())
    put(string.toByteArray(Charsets.UTF_8))
    return this
}

fun ByteBuffer.putUtf8(index: Int, string: String): ByteBuffer {
    val beforePosition = position()
    position(index)
    putShort(string.length.toShort())
    put(string.toByteArray(Charsets.UTF_8))
    position(beforePosition)
    return this
}

fun ByteBuffer.getSetSizeUtf8(length: Int): String {
    val byteArray = ByteArray(length)
    this.get(byteArray)
    return byteArray.toString(Charsets.UTF_8)
}

fun ByteBuffer.getSetSizeUtf8(length: Int, index: Int): String {
    val beforePosition = position()
    position(index)
    val byteArray = ByteArray(length)
    this.get(byteArray)
    position(beforePosition)
    return byteArray.toString(Charsets.UTF_8)
}

fun ByteBuffer.putSetSizeUtf8(length: Int, string: String): ByteBuffer {
    put(string.take(length).padEnd(length, '\u0000').toByteArray(Charsets.UTF_8))
    return this
}

fun ByteBuffer.putSetSizeUtf8(length: Int, index: Int, string: String): ByteBuffer {
    val beforePosition = position()
    position(index)
    put(string.take(length).padEnd(length, '\u0000').toByteArray(Charsets.UTF_8))
    position(beforePosition)
    return this
}

/* SHARED DECLARATIONS

class ByteBuffer : Comparable<ByteBuffer> {

    open fun put(src: ByteBuffer): ByteBuffer

    open fun put(
        src: Data, offset: Int,
        length: Int
    ): ByteBuffer

    fun put(src: Data): ByteBuffer

    fun hasArray(): Boolean

    fun array(): Data

    fun arrayOffset(): Int

    fun position(newPosition: Int): Buffer

    fun limit(newLimit: Int): Buffer

    fun mark(): Buffer

    fun reset(): Buffer

    fun clear(): Buffer

    fun flip(): Buffer

    fun rewind(): Buffer

    fun compact(): ByteBuffer

    fun order(): ByteOrder

    fun order(bo: ByteOrder): ByteBuffer

    fun get(): Byte
    fun put(b: Byte): ByteBuffer

    operator fun get(i: Int): Byte
    fun put(i: Int, b: Byte): ByteBuffer

    fun getShort(): Short
    fun putShort(i: Short): ByteBuffer

    fun getShort(i: Int): Short
    fun putShort(i: Int, i1: Short): ByteBuffer

    fun getInt(): Int
    fun putInt(i: Int): ByteBuffer

    fun getInt(i: Int): Int
    fun putInt(i: Int, i1: Int): ByteBuffer

    fun getLong(): Long
    fun putLong(l: Long): ByteBuffer

    fun getLong(i: Int): Long
    fun putLong(i: Int, l: Long): ByteBuffer

    fun getFloat(): Float
    fun putFloat(v: Float): ByteBuffer

    fun getFloat(i: Int): Float
    fun putFloat(i: Int, v: Float): ByteBuffer

    fun getDouble(): Double
    fun putDouble(v: Double): ByteBuffer

    fun getDouble(i: Int): Double
    fun putDouble(i: Int, v: Double): ByteBuffer

    companion object {
        fun allocateDirect(capacity: Int): ByteBuffer {}

        fun allocate(capacity: Int): ByteBuffer {}

        fun wrap(
            array: Data,
            offset: Int,
            length: Int
        ): ByteBuffer {}

        fun wrap(array: Data): ByteBuffer {}
    }
}

enum class ByteOrder {
    BIG_ENDIAN,
    LITTLE_ENDIAN
}

*/

