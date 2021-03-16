package com.lightningkite.khrysalis.util

interface Appending {
    fun appendTo(appendable: Appendable)
}
fun Appendable.appendPlus(part: Any){
    if(part is Appending) {
        part.appendTo(this)
    } else {
        append(part.toString())
    }
}

class AppendingChain(val parts: ArrayList<Any>): Appending {
    override fun toString(): String = buildString {
        for(part in parts){
            append(part)
        }
    }

    override fun appendTo(appendable: Appendable) {
        for(part in parts){
            appendable.appendPlus(part)
        }
    }
}

/*

fun KtClass.swift(out: Out) {
    when {
        x -> { swiftEnum(); return }
        y -> { swiftDataClass(); return }
    }
    out.appendln(name)
    out.appendln("{")
}


 */