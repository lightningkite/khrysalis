package com.lightningkite.kwift.swift

fun String.retabSwift(): String {
    val tabs = HashSet<Int>()
    var pastChar = ' '
    var braces = 0
    var bracesModifiedThisLine = false
    var tabsAdded = false
    return buildString {
        fun addTabs(){
            if(!tabsAdded) {
                tabsAdded = true
//                append(tabs.size.toString().padEnd(4))
                repeat(tabs.size * 4){
                    append(' ')
                }
            }
        }
        loop@for (char in this@retabSwift) {
            when(char) {
                ' ' -> if(pastChar != ' '){
                    append(char)
                    pastChar = ' '
                    continue@loop
                }
                '{', '(', '[' -> {
                    addTabs()
                    append(char)
                    braces++
                }
                '}', ')', ']' -> {
                    if(tabs.remove(braces)) {
                    }
                    braces--
                    addTabs()
                    append(char)
                }
                '\r' -> {
                    append(char)
                    continue@loop
                }
                '\n' -> {
                    append(char)
                    if(braces != 0) {
                        tabs.add(braces)
                    }
                    tabsAdded = false
                    pastChar = ' '
                    bracesModifiedThisLine = false
                    continue@loop
                }
                else -> {
                    addTabs()
                    append(char)
                }
            }
            pastChar = char
        }
    }
}
