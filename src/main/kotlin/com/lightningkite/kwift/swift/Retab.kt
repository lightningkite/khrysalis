package com.lightningkite.kwift.swift

private val spaceRegex = Regex("\\s+")
fun String.retabSwift(): String {
    var braceLevel = 0
    var previousMin = 0
    val braceLevelsRepresentedByTab = ArrayList<Int>()
    return this.lineSequence()
        .joinToString("\n") { line ->
            val startBraceLevel = braceLevel
            var minBraces: Int = braceLevel
            for (c in line) {
                when (c) {
                    '{', '(', '[' -> braceLevel++
                    '}', ')', ']' -> {
                        braceLevel--
                        if (braceLevel < minBraces) {
                            minBraces = braceLevel
                        }
                    }
                }
            }
            val endBraceLevel = braceLevel

            if (minBraces > previousMin) {
                braceLevelsRepresentedByTab.add(minBraces)
            }
            if (minBraces < previousMin) {
                braceLevelsRepresentedByTab.removeAll { it > minBraces }
            }
//            println("hey $startBraceLevel -> $endBraceLevel, min $previousMin -> $minBraces - ${braceLevelsRepresentedByTab.size}")
            previousMin = minBraces
            "    ".repeat(braceLevelsRepresentedByTab.size) + line.trimStart().replace(spaceRegex, " ")
        }
}
//    val tabs = HashSet<Int>()
//    var pastChar = ' '
//    var braces = 0
//    var braceMinimum = 0
//    var tabsAdded = false
//    return buildString {
//        fun addTabs(){
//            if(!tabsAdded) {
//                tabsAdded = true
////                append(tabs.size.toString().padEnd(4))
//                repeat(tabs.size * 4){
//                    append(' ')
//                }
//            }
//        }
//        loop@for (char in this@retabSwift) {
//            when(char) {
//                ' ' -> if(pastChar != ' '){
//                    append(char)
//                    pastChar = ' '
//                    continue@loop
//                }
//                '{', '(', '[' -> {
//                    addTabs()
//                    append(char)
//                    braces++
//                }
//                '}', ')', ']' -> {
//                    if(tabs.remove(braces)) {
//                    }
//                    braces--
//                    braceMinimum = Math.min(braces, braceMinimum)
//                    addTabs()
//                    append(char)
//                }
//                '\r' -> {
//                    append(char)
//                    continue@loop
//                }
//                '\n' -> {
//                    append(char)
//                    if(braceMinimum != 0) {
//                        tabs.add(braceMinimum)
//                    }
//                    braceMinimum = braces
//                    tabsAdded = false
//                    pastChar = ' '
//                    continue@loop
//                }
//                else -> {
//                    addTabs()
//                    append(char)
//                }
//            }
//            pastChar = char
//        }
//    }
//}
