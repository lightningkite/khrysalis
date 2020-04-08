package com.lightningkite.khrysalis.utils

enum class AlignDirection {
    START, CENTER, END
}

enum class Gravity(val alignDirection: AlignDirection, val useLocal: Boolean) {
    START(AlignDirection.START, false),
    START_LOCAL(AlignDirection.START, true),
    END(AlignDirection.END, false),
    END_LOCAL(AlignDirection.END, true),
    CENTER(AlignDirection.CENTER, false);
}

fun horizontalGravity(text: String): Gravity {
    for (part in text.split('|')) {
        when (part) {
            "left" -> return Gravity.START
            "start" -> return Gravity.START_LOCAL
            "right" -> return Gravity.END
            "end" -> return Gravity.END_LOCAL
            "center_horizontal",
            "center" -> return Gravity.CENTER
        }
    }
    return Gravity.START_LOCAL
}


fun verticalGravity(text: String): Gravity {
    for (part in text.split('|')) {
        when (part) {
            "top" -> return Gravity.START
            "bottom" -> return Gravity.END
            "center_vertical",
            "center" -> return Gravity.CENTER
        }
    }
    return Gravity.START
}
