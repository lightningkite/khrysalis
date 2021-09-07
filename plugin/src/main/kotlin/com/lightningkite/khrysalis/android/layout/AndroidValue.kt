package com.lightningkite.khrysalis.android.layout

import com.lightningkite.khrysalis.replacements.AttributeReplacement
import java.io.File


sealed interface AndroidValue {
    val type: AttributeReplacement.ValueType
    operator fun get(key: String): String
}
sealed interface AndroidDimensionValue: AndroidValue {
    val measurement: Measurement
    override fun get(key: String): String = measurement[key]
        ?: throw IllegalArgumentException("No key $key for ${this::class.simpleName}")
}
sealed interface AndroidDrawableValue: AndroidValue
sealed interface AndroidColorValue: AndroidDrawableValue {
    val value: ColorInParts
    override fun get(key: String): String = value[key]
        ?: throw IllegalArgumentException("No key $key for ${this::class.simpleName}")
}
sealed interface AndroidStringValue: AndroidValue { val value: String }

data class AndroidFont(
    val family: String,
    val name: String,
    val file: File? = null
): AndroidValue {
    override val type: AttributeReplacement.ValueType
        get() = AttributeReplacement.ValueType.Font

    override fun get(key: String): String = when(key) {
        "family" -> family
        "name" -> name
        "file" -> file?.toString() ?: ""
        else -> throw IllegalArgumentException("No key $key for ${this::class.simpleName}")
    }
}

data class AndroidColor(
    override val value: ColorInParts
): AndroidColorValue {
    override val type: AttributeReplacement.ValueType
        get() = AttributeReplacement.ValueType.Color
}
data class AndroidColorResource(
    val name: String,
    override val value: ColorInParts
): AndroidColorValue {
    override val type: AttributeReplacement.ValueType
        get() = AttributeReplacement.ValueType.ColorResource

    override fun get(key: String): String = when(key) {
        "name" -> name
        else -> super.get(key)
    }
}
data class AndroidColorStateResource(
    val name: String,
    val colors: StateSelector<AndroidColorValue>
): AndroidColorValue {
    override val type: AttributeReplacement.ValueType
        get() = AttributeReplacement.ValueType.ColorStateResource
    override val value: ColorInParts get() = colors.normal.value

    override fun get(key: String): String = when(key) {
        "name" -> name
        else -> when {
            key.startsWith("normal") -> colors.normal[key.substringAfter("normal").decapitalize()]
            key.startsWith("selected") -> colors.selected?.get(key.substringAfter("selected").decapitalize()) ?: ""
            key.startsWith("highlighted") -> colors.highlighted?.get(key.substringAfter("highlighted").decapitalize()) ?: ""
            key.startsWith("disabled") -> colors.disabled?.get(key.substringAfter("disabled").decapitalize()) ?: ""
            key.startsWith("focused") -> colors.focused?.get(key.substringAfter("focused").decapitalize()) ?: ""
            else -> super.get(key)
        }
    }
}
data class AndroidDrawableResource(
    val name: String,
    val files: Map<String, File>
): AndroidDrawableValue {
    override val type: AttributeReplacement.ValueType
        get() = AttributeReplacement.ValueType.DrawableResource

    override fun get(key: String): String = when(key) {
        "name" -> name
        else -> throw IllegalArgumentException("No key $key for ${this::class.simpleName}")
    }
}

data class AndroidLayoutResource(
    val name: String,
    val layout: AndroidLayoutFile
): AndroidValue {
    override val type: AttributeReplacement.ValueType
        get() = AttributeReplacement.ValueType.LayoutResource

    override fun get(key: String): String = when(key) {
        "name" -> name
        else -> throw IllegalArgumentException("No key $key for ${this::class.simpleName}")
    }
}

data class AndroidDimension(
    override val measurement: Measurement
): AndroidDimensionValue {
    override val type: AttributeReplacement.ValueType
        get() = AttributeReplacement.ValueType.Dimension
}
data class AndroidDimensionResource(
    val name: String,
    override val measurement: Measurement
): AndroidDimensionValue {
    override val type: AttributeReplacement.ValueType
        get() = AttributeReplacement.ValueType.DimensionResource

    override fun get(key: String): String = when(key) {
        "name" -> name
        else -> super.get(key)
    }
}

data class AndroidNumber(
    val value: Double
): AndroidValue {
    override val type: AttributeReplacement.ValueType
        get() = AttributeReplacement.ValueType.Number

    override fun get(key: String): String = when(key) {
        "value" -> value.toString()
        else -> throw IllegalArgumentException("No key $key for ${this::class.simpleName}")
    }
}

data class AndroidString(
    override val value: String
): AndroidStringValue {
    override val type: AttributeReplacement.ValueType
        get() = AttributeReplacement.ValueType.String
    override fun get(key: String): String = when(key) {
        "value" -> value
        else -> throw IllegalArgumentException("No key $key for ${this::class.simpleName}")
    }
}
data class AndroidStringResource(
    val name: String,
    override val value: String
): AndroidStringValue {
    override val type: AttributeReplacement.ValueType
        get() = AttributeReplacement.ValueType.StringResource

    override fun get(key: String): String = when(key) {
        "name" -> name
        "value" -> value
        else -> throw IllegalArgumentException("No key $key for ${this::class.simpleName}")
    }
}

data class AndroidStyle(
    val name: String,
    val map: Map<String, String>
): AndroidValue {
    override val type: AttributeReplacement.ValueType
        get() = AttributeReplacement.ValueType.Style

    override fun get(key: String): String = when(key) {
        "name" -> name
        else -> throw IllegalArgumentException("No key $key for ${this::class.simpleName}")
    }
}

enum class MeasurementUnit {
    PX, DP, SP
}
data class Measurement(
    val number: Double,
    val unit: MeasurementUnit
) {
    operator fun get(key: String): String? = when(key){
        "number" -> number.toString()
        "unit" -> unit.name.toLowerCase()
        else -> null
    }
}

data class ColorInParts(
    val red: Int = 0,
    val green: Int = 0,
    val blue: Int = 0,
    val alpha: Int = 0xFF
) {
    val web: String get() = "#" + red.toString(16).padStart(2, '0') + green.toString(16).padStart(2, '0') + blue.toString(16).padStart(2, '0') + alpha.toString(16).padStart(2, '0')
    val android: String get() = "#" + alpha.toString(16).padStart(2, '0') + red.toString(16).padStart(2, '0') + green.toString(16).padStart(2, '0') + blue.toString(16).padStart(2, '0')
    val redFloat: Float get() = red / 255f
    val greenFloat: Float get() = green / 255f
    val blueFloat: Float get() = blue / 255f
    val alphaFloat: Float get() = alpha / 255f
    companion object {
        val transparent = ColorInParts(alpha = 0)
        val black = ColorInParts()
        val white = ColorInParts(red = 0xFF, green = 0xFF, blue = 0xFF)
    }
    operator fun get(key: String): String? = when(key){
        "web" -> web
        "android" -> android
        "red" -> red.toString(16).padStart(2, '0')
        "green" -> green.toString(16).padStart(2, '0')
        "blue" -> blue.toString(16).padStart(2, '0')
        "alpha" -> alpha.toString(16).padStart(2, '0')
        "redFloat" -> redFloat.toString()
        "greenFloat" -> greenFloat.toString()
        "blueFloat" -> blueFloat.toString()
        "alphaFloat" -> alphaFloat.toString()
        else -> null
    }
}

fun String.hashColorToParts(): ColorInParts {
    return when (this.length) {
        3 + 1 -> ColorInParts(
            red = (this[1].toString().toInt(16) * 0x11),
            green = (this[2].toString().toInt(16) * 0x11),
            blue = (this[3].toString().toInt(16) * 0x11)
        )
        4 + 1 -> ColorInParts(
            red = (this[2].toString().toInt(16) * 0x11),
            green = (this[3].toString().toInt(16) * 0x11),
            blue = (this[4].toString().toInt(16) * 0x11),
            alpha = (this[1].toString().toInt(16) * 0x11),
        )
        6 + 1 -> ColorInParts(
            red = (this.substring(1, 3).toInt(16)),
            green = (this.substring(3, 5).toInt(16)),
            blue = (this.substring(5, 7).toInt(16))
        )
        8 + 1 -> ColorInParts(
            red = (this.substring(3, 5).toInt(16)),
            green = (this.substring(5, 7).toInt(16)),
            blue = (this.substring(7, 9).toInt(16)),
            alpha = (this.substring(1, 3).toInt(16)),
        )
        else -> ColorInParts()
    }
}

enum class UiState { Normal, Selected, Highlighted, Disabled, Focused }
data class StateSelector<T>(
    val normal: T,
    val selected: T? = null,
    val highlighted: T? = null,
    val disabled: T? = null,
    val focused: T? = null
) {
    val isSet: Boolean get() = selected != null || highlighted != null || disabled != null || focused != null
    val variants: Map<String, T>
        get() = listOf(
            "" to normal,
            "_selected" to selected,
            "_highlighted" to highlighted,
            "_disabled" to disabled,
            "_focused" to focused
        ).filter { it.second != null }.associate { it.first to it.second!! }

    operator fun get(state: UiState): T = when (state) {
        UiState.Normal -> normal
        UiState.Selected -> selected ?: normal
        UiState.Highlighted -> highlighted ?: normal
        UiState.Disabled -> disabled ?: normal
        UiState.Focused -> focused ?: normal
    }

    fun copy(state: UiState, to: T) = when (state) {
        UiState.Normal -> copy(normal = to)
        UiState.Selected -> copy(selected = to)
        UiState.Highlighted -> copy(highlighted = to)
        UiState.Disabled -> copy(disabled = to)
        UiState.Focused -> copy(focused = to)
    }
}
