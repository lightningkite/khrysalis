package com.lightningkite.khrysalis.formatting

private val extraTabChars = setOf('.', '|', '&')

fun String.retab(tabSize: Int = 4): String {
    val lines = this.lineSequence().map { it.trim() }.toList()
    val tab = " ".repeat(tabSize)
    var currentBraceLevel = 0
    val braceLevels = lines.map {
        var lowest = currentBraceLevel
        for(c in it){
            when(c){
                '{', '[', '(' -> {
                    currentBraceLevel++
                }
                '}', ']', ')' -> {
                    currentBraceLevel--
                    if(lowest > currentBraceLevel){
                        lowest = currentBraceLevel
                    }
                }
            }
        }
        lowest
    }.toMutableList()
    // Find indices of all 2+ increases
    for(index in 1 .. braceLevels.lastIndex){
        val prev = braceLevels[index - 1]
        val now = braceLevels[index]
        val diff = now - prev
        if(diff > 1) {
            //Find min between start and end
            var smallest = now
            for(subIndex in index + 1 .. braceLevels.lastIndex ) {
                val ahead = braceLevels[subIndex]
                if(ahead <= prev) {
                    val collapseAmount = (smallest - prev) - 1
                    if(smallest - prev > 1) {
                        // Decrease brace levels between
                        for(decIndex in index .. subIndex - 1) {
                            braceLevels[decIndex] -= collapseAmount
                        }
                    }
                    break
                }
                smallest = minOf(ahead, smallest)
            }
        }
    }


    return lines.asSequence().zip(braceLevels.asSequence()) { text, level ->
        tab.repeat(level.coerceAtLeast(0) + if(text.firstOrNull() in extraTabChars) 1 else 0) + text
    }.joinToString("\n")
}
