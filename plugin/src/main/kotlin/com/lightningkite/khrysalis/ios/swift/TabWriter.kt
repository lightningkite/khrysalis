package com.lightningkite.khrysalis.ios.swift

class TabWriter(underlying: Appendable) {

    val direct = LineKnowledgeableAppendable(underlying)

    var spaces: Int = 0
    fun line() {
        ensureNewLine()
        direct.append(" ".repeat(spaces))
        direct.append('\n')
    }

    fun line(text: CharSequence) {
        ensureNewLine()
        direct.append(" ".repeat(spaces))
        direct.append(text)
        direct.append('\n')
    }

    inline fun line(line: Appendable.() -> Unit) {
        ensureNewLine()
        direct.append(" ".repeat(spaces))
        direct.line()
        direct.append('\n')
    }

    fun startLine() {
        if(direct.isOnNewLine){
            direct.append(" ".repeat(spaces))
        } else if(direct.lineIsOnlySpacing){
            return
        } else {
            direct.append('\n')
            direct.append(" ".repeat(spaces))
        }
    }

    fun ensureNewLine() {
        if(!direct.isOnNewLine){
            direct.append('\n')
        }
    }

    fun tabIn() {
        spaces += 4
    }

    fun tabOut() {
        spaces -= 4
    }

    inline fun tab(spaces: Int = 4, action: () -> Unit) {
        this.spaces += spaces
        action()
        this.spaces -= spaces
    }
}

class LineKnowledgeableAppendable(val underlying: Appendable) : Appendable {

    var isOnNewLine = true
    var lineIsOnlySpacing = true

    override fun append(csq: CharSequence): java.lang.Appendable {
        isOnNewLine = csq.endsWith('\n')
        for(index in csq.indices){
            val c = csq[index]
            if(c == '\n'){
                isOnNewLine = true
                lineIsOnlySpacing = true
            } else {
                isOnNewLine = false
            }
            if(!c.isWhitespace()){
                lineIsOnlySpacing = false
            }
        }
        return underlying.append(csq)
    }

    override fun append(csq: CharSequence, start: Int, end: Int): java.lang.Appendable {
        for(index in start until end){
            val c = csq[index]
            if(c == '\n'){
                isOnNewLine = true
                lineIsOnlySpacing = true
            } else {
                isOnNewLine = false
            }
            if(!c.isWhitespace()){
                lineIsOnlySpacing = false
            }
        }
        return underlying.append(csq)
    }

    override fun append(c: Char): java.lang.Appendable {
        if(c == '\n'){
            isOnNewLine = true
            lineIsOnlySpacing = true
        } else {
            isOnNewLine = false
        }
        if(!c.isWhitespace()){
            lineIsOnlySpacing = false
        }
        return underlying.append(c)
    }
}
