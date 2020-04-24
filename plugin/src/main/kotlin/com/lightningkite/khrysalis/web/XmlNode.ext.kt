package com.lightningkite.khrysalis.web

import com.lightningkite.khrysalis.utils.*


var useScssVariables = false

fun XmlNode.attributeAsCssString(key: String): String? = allAttributes[key]?.asCssString()
fun String.asCssString(): String? {
    val value = this
    return when {
        value.startsWith("@") -> {
            val name = value.substringAfter('/').kabobCase()
            if(useScssVariables) {
                "\$string-$name"
            } else {
                "var(--string-$name)"
            }
        }
        else -> "\"$value\""
    }
}

fun XmlNode.attributeAsCssDimension(key: String): String? = allAttributes[key]?.asCssDimension()
fun String.asCssDimension(): String? {
    val value = this
    val numerical = value.filter { it.isDigit() || it == '.' }
    return when {
        value.startsWith("@") -> {
            val name = value.substringAfter('/').kabobCase()
            if(useScssVariables) {
                "\$dimen-$name"
            } else {
                "var(--dimen-$name)"
            }
        }
        value.endsWith("px") -> numerical + "px"
        value.endsWith("dp") -> numerical + "px"
        value.endsWith("dip") -> numerical + "px"
        value.endsWith("sp") -> numerical + "pt"
        else -> "0px"
    }
}

fun XmlNode.attributeAsCssColor(key: String): String? = allAttributes[key]?.asCssColor()
fun String.asCssColor(): String? {
    val value = this
    return when {
        value.startsWith("@") -> {
            val name = value.substringAfter('/').kabobCase()
            if(useScssVariables) {
                if (name.startsWith("set"))
                    "var(--color-$name)"
                else
                    "\$color-$name"
            } else {
                "var(--color-$name)"
            }
        }
        value.startsWith("#") -> {
            when (value.length - 1) {
                3 -> "#" + value[1].toString().repeat(2) + value[2].toString().repeat(2) + value[3].toString()
                    .repeat(2)
                4 -> "#" + value[2].toString().repeat(2) + value[3].toString().repeat(2) + value[4].toString()
                    .repeat(2) + value[1].toString().repeat(2)
                6 -> value
                8 -> "#" + value.drop(3).take(6) + value.drop(1).take(2)
                else -> "#000000"
            }
        }
        else -> "black"
    }
}
