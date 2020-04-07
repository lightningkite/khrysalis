package com.lightningkite.khrysalis.generic

import java.lang.StringBuilder
import kotlin.math.min

class SmartTabWriter(val base: Appendable, val spaces: Int = 4): Appendable {

    var currentTabCount: Int = 0
        set(value){
            field = value
            currentLineTabCount = min(currentLineTabCount, currentTabCount)
        }
    var currentLineTabCount: Int = 0
    val currentLine: StringBuilder = StringBuilder()

    val tabString = " ".repeat(spaces)
    private fun startLine() {
        base.append(tabString.repeat(currentLineTabCount.coerceAtLeast(0)))
        base.append(currentLine)
        base.appendln()
        currentLine.clear()
        currentLineTabCount = currentTabCount
    }

    override fun append(contents: CharSequence): Appendable {
        var currentStart: Int = 0
        var currentEnd: Int = 0
        contents.forEachIndexed { index, c ->
            when(c) {
                '\n' -> {
                    if(currentStart != currentEnd) {
                        currentLine.append(contents, currentStart, currentEnd)
                    }
                    currentStart = index + 1
                    currentEnd = currentStart
                    startLine()
                }
                '{', '(', '[' -> {
                    currentEnd = index + 1
                    currentTabCount++
                }
                '}', ')', ']' -> {
                    currentEnd = index + 1
                    currentTabCount--
                }
                else -> currentEnd = index + 1
            }
        }
        if(currentStart != currentEnd) {
            currentLine.append(contents, currentStart, currentEnd)
        }
        return this
    }

    override fun append(contents: CharSequence, startIndex: Int, endIndexExclusive: Int): Appendable {
        var currentStart: Int = startIndex
        var currentEnd: Int = startIndex
        contents.subSequence(startIndex, endIndexExclusive).forEachIndexed { index, c ->
            when(c) {
                '\n' -> {
                    if(currentStart != currentEnd) {
                        currentLine.append(contents, currentStart, currentEnd)
                    }
                    currentStart = index + 1
                    currentEnd = currentStart
                    startLine()
                }
                '{', '(', '[' -> {
                    currentEnd = index + 1
                    currentTabCount++
                }
                '}', ')', ']' -> {
                    currentEnd = index + 1
                    currentTabCount--
                }
                else -> currentEnd = index + 1
            }
        }
        if(currentStart != currentEnd) {
            currentLine.append(contents, currentStart, currentEnd)
        }
        return this
    }

    override fun append(c: Char): Appendable {
        when(c) {
            '\n' -> startLine()
            '{', '(', '[' -> {
                currentLine.append(c)
                currentTabCount++
            }
            '}', ')', ']' -> {
                currentLine.append(c)
                currentTabCount--
            }
            else -> currentLine.append(c)
        }
        return this
    }

}
