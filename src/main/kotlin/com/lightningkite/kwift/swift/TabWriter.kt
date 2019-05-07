package com.lightningkite.kwift.swift

class TabWriter(val underlying: Appendable) {
    var spaces: Int = 0
    fun line(){
        underlying.append(" ".repeat(spaces))
        underlying.append('\n')
    }
    fun line(text: CharSequence){
        underlying.append(" ".repeat(spaces))
        underlying.append(text)
        underlying.append('\n')
    }
    inline fun line(line: Appendable.()->Unit){
        underlying.append(" ".repeat(spaces))
        underlying.line()
        underlying.append('\n')
    }
    fun tabIn() { spaces += 4 }
    fun tabOut() { spaces -= 4 }
    inline fun tab(spaces: Int = 4, action: ()->Unit) {
        this.spaces += spaces
        action()
        this.spaces -= spaces
    }
}
