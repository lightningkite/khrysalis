package com.lightningkite.kwift.layoutxml



fun String.hashColorToUIColor(): String {
    val withoutHash = removePrefix("#")
    return when (withoutHash.length) {
        3 -> "UIColor(argb: 0xFF${withoutHash[0].toString().repeat(2)}${withoutHash[1].toString().repeat(2)}${withoutHash[2].toString().repeat(
            2
        )})"
        4 -> "UIColor(argb: 0x${withoutHash[0].toString().repeat(2)}${withoutHash[1].toString().repeat(2)}${withoutHash[2].toString().repeat(
            2
        )}${withoutHash[3].toString().repeat(2)})"
        6 -> "UIColor(argb: 0xFF$withoutHash)"
        8 -> "UIColor(argb: 0x$withoutHash)"
        else -> "UIColor.black"
    }
}
