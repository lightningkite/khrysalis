package com.lightningkite.khrysalis.ios.layout2.models

data class IosColor(
    val alpha: Float = 1f,
    val red: Float = 0f,
    val green: Float = 0f,
    val blue: Float = 0f,
    val referenceTo: String? = null
) {
    companion object {
        val transparent = IosColor(0f,0f,0f,0f)
        fun fromHashString(str: String): IosColor? {
            val numbersOnly = str.drop(1)
            return when (numbersOnly.length) {
                3 -> IosColor(
                    red = numbersOnly[0].toString().toInt(16) / 15f,
                    green = numbersOnly[1].toString().toInt(16) / 15f,
                    blue = numbersOnly[2].toString().toInt(16) / 15f
                )
                4 -> IosColor(
                    alpha = numbersOnly[0].toString().toInt(16) / 15f,
                    red = numbersOnly[1].toString().toInt(16) / 15f,
                    green = numbersOnly[2].toString().toInt(16) / 15f,
                    blue = numbersOnly[3].toString().toInt(16) / 15f
                )
                6 -> IosColor(
                    red = numbersOnly.substring(0, 2).toInt(16) / 255f,
                    green = numbersOnly.substring(2, 4).toInt(16) / 255f,
                    blue = numbersOnly.substring(4, 6).toInt(16) / 255f
                )
                8 -> IosColor(
                    alpha = numbersOnly.substring(0, 2).toInt(16) / 255f,
                    red = numbersOnly.substring(2, 4).toInt(16) / 255f,
                    green = numbersOnly.substring(4, 6).toInt(16) / 255f,
                    blue = numbersOnly.substring(6, 8).toInt(16) / 255f
                )
                else -> null
            }
        }
    }

    override fun toString(): String {
        if(referenceTo != null){
            return "R.color.$referenceTo"
        } else {
            return toUIColor()
        }
    }

    fun toUIColor(): String {
        return "UIColor(red: $red, green: $green, blue: $blue, alpha: $alpha)"
    }
}

