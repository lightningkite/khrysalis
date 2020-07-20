package com.lightningkite.khrysalis.util

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
    var justStartedLine = true

    val tabString = " ".repeat(spaces)
    private fun startLine() {
        flush()
        base.appendln()
        currentLineTabCount = currentTabCount
        justStartedLine = true
    }

    val addIndentChars = setOf('.', '?')

    override fun append(contents: CharSequence): Appendable = append(contents, 0, contents.length)

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
                    justStartedLine = false
                }
                '}', ')', ']' -> {
                    currentEnd = index + 1
                    currentTabCount--
                    justStartedLine = false
                }
                else -> {
                    if(c.isWhitespace() && justStartedLine) {
                        currentStart = index + 1
                    } else {
                        if(justStartedLine && c in addIndentChars){
                            currentLine.append(tabString)
                        }
                        justStartedLine = false
                    }
                    currentEnd = index + 1
                }
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
                justStartedLine = false
            }
            '}', ')', ']' -> {
                currentLine.append(c)
                currentTabCount--
                justStartedLine = false
            }
            else -> {
                if(c.isWhitespace() && justStartedLine) {
                    //skip
                } else {
                    if(justStartedLine && c in addIndentChars){
                        currentLine.append(tabString)
                    }
                    justStartedLine = false
                    currentLine.append(c)
                }
            }
        }
        return this
    }

    fun flush() {
        base.append(tabString.repeat(currentLineTabCount.coerceAtLeast(0)))
        base.append(currentLine)
        currentLine.clear()
    }

}
