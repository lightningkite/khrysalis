package com.lightningkite.khrysalis.util

import java.io.PushbackReader
import java.io.Reader


inline fun Int.isReaderEnd() = this == -1 || this == 0xFFFF

inline fun Reader.readString(length: Int): String {
    val builder = StringBuilder()
    kotlin.repeat(length) {
        builder.append(readChar())
    }
    return builder.toString()
}

inline fun Reader.readChar(): Char {
    return read().toChar()
}

inline fun PushbackReader.peekChar(): Char {
    val intChar = read()
    unread(intChar)
    return intChar.toChar()
}

inline fun PushbackReader.isAtEnd(): Boolean {
    val intChar = read()
    unread(intChar)
    return intChar.isReaderEnd()
}

inline fun PushbackReader.peekString(size: Int): String {
    val array = CharArray(size)
    read(array)
    unread(array)
    return String(array)
}

val builder: StringBuilder = StringBuilder()
inline fun PushbackReader.skipWhile(predicate: (Char) -> Boolean) = skipUntil { !predicate(it) }
inline fun PushbackReader.skipUntil(predicate: (Char) -> Boolean) {
    while (true) {
        val intChar = read()
        if (intChar.isReaderEnd() || predicate(intChar.toChar())) {
            unread(intChar)
            return
        }
    }
}

inline fun PushbackReader.readWhile(predicate: (Char) -> Boolean) = readUntil { !predicate(it) }
inline fun PushbackReader.readUntil(predicate: (Char) -> Boolean): String {
    builder.setLength(0)
    while (true) {
        val intChar = read()
        if (intChar.isReaderEnd() || predicate(intChar.toChar())) {
            unread(intChar)
            return builder.toString()
        } else {
            builder.append(intChar.toChar())
        }
    }
}

inline fun PushbackReader.peekWhile(predicate: (Char) -> Boolean) = peekUntil { !predicate(it) }
inline fun PushbackReader.peekUntil(predicate: (Char) -> Boolean): String {
    builder.setLength(0)
    while (true) {
        val intChar = read()
        if (intChar.isReaderEnd() || predicate(intChar.toChar())) {
            unread(intChar)
            unread(builder.toString().toCharArray())
            return builder.toString()
        } else {
            builder.append(intChar.toChar())
        }
    }
}

inline fun PushbackReader.readCheck(data: String): Boolean {
    builder.setLength(0)
    for (char in data) {
        val intChar = read()
        if (intChar.isReaderEnd() || intChar.toChar() != char) {
            unread(intChar)
            unread(builder.toString().toCharArray())
            return false
        } else {
            builder.append(intChar.toChar())
        }
    }
    return true
}

inline fun Reader.skipUntilSkipFinal(predicate: (Char) -> Boolean) {
    while (true) {
        val intChar = read()
        if (intChar.isReaderEnd() || predicate(intChar.toChar())) {
            return
        }
    }
}

inline fun Reader.readUntilSkipFinal(predicate: (Char) -> Boolean): String {
    builder.setLength(0)
    while (true) {
        val intChar = read()
        if (intChar.isReaderEnd() || predicate(intChar.toChar())) {
            return builder.toString()
        } else {
            builder.append(intChar.toChar())
        }
    }
}

inline fun PushbackReader.skipWhitespace() = skipUntil { !it.isWhitespace() }
inline fun PushbackReader.skipWhitespaceAndComments() {
    var inLineComment = false
    var inBlockComment = false
    while (true) {
        val intChar = read()
        if (intChar.isReaderEnd()) {
            unread(intChar)
            return
        }
        val char = intChar.toChar()
        if (char == '/' && peekChar() == '/') {
            inLineComment = true
            skip(1)
            continue
        }
        if (inLineComment && char == '\n') {
            inLineComment = false
            continue
        }
        if (char == '/' && peekChar() == '*') {
            inBlockComment = true
            skip(1)
            continue
        }
        if (inBlockComment && char == '*' && peekChar() == '/') {
            inBlockComment = false
            skip(1)
            continue
        }
        if (!inBlockComment && !inLineComment && !char.isWhitespace()) {
            unread(intChar)
            return
        }
    }
}

inline fun PushbackReader.skipUntil(char: Char) = skipUntil { it == char }
inline fun PushbackReader.readUntil(char: Char) = readUntil { it == char }
inline fun PushbackReader.readUntilEscapable(escapeChar: Char, char: Char): String {
    var prevChar = ' '
    return readUntil {
        val result = it == char && prevChar != escapeChar
        prevChar = it
        result
    }
}

inline fun PushbackReader.readUntilEscapable(escapeChars: Collection<Char>, char: Char): String {
    var prevChar = ' '
    return readUntil {
        val result = it == char && prevChar !in escapeChars
        prevChar = it
        result
    }
}

inline fun PushbackReader.readWord() = readUntil { it !in 'a'..'z' || it !in 'A'..'Z' }

inline fun PushbackReader.readEscaped(escapeChar: Char, endChar: Char): String {
    builder.setLength(0)
    var prevChar = ' '
    while (true) {
        val intChar = read()
        val char = intChar.toChar()
        if (intChar.isReaderEnd()) {
            unread(intChar)
            return builder.toString()
        } else if (char == endChar) {
            if (prevChar == escapeChar) {
                builder.append(endChar)
            } else {
                unread(intChar)
                return builder.toString()
            }
        } else {
            builder.append(char)
        }
    }
}

/**
 * Reads a whole delimited section, accounting for subsections.  Not recommended, but it can do it.  The first character it reads MUST be the start char.
 */
inline fun Reader.readDelimited(startChar: Char, endChar: Char): String {
    var count = 0
    return readUntilSkipFinal {
        if (it == startChar) count++
        else if (it == endChar) {
            count--
            if (count == 0) {
                return@readUntilSkipFinal true
            }
        }
        false
    }
}