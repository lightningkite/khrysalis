package com.lightningkite.khrysalis.swift.replacements

sealed class TemplatePart {
    class Text(val string: String) : TemplatePart() {
        override fun toString(): String = string
    }
    object Receiver : TemplatePart() { override fun toString() = "~this~" }
    object DispatchReceiver : TemplatePart() { override fun toString() = "~thisDispatch~" }
    object ExtensionReceiver : TemplatePart() { override fun toString() = "~thisExtension~" }
    object Value : TemplatePart() { override fun toString() = "~value~" }
    object AllParameters: TemplatePart() { override fun toString() = "~*~" }
    object OperatorToken: TemplatePart() { override fun toString() = "~operatorToken~" }
    data class Parameter(val name: String) : TemplatePart(), TemplatePartIsParameter {
        override fun toString(): String = "~$name~"
    }
    data class TypeParameter(val name: String) : TemplatePart() {
        override fun toString(): String = "~$name~"
    }
    data class LambdaParameterContents(
        val pointer: TemplatePartIsParameter,
        val paramMap: List<List<TemplatePart>>
    ) : TemplatePart() {
        override fun toString(): String = when(pointer){
            is ParameterByIndex -> "~L${pointer.index}~"
            is Parameter -> "~L${pointer.name}~"
            else -> "~L?~"
        }
    }
    data class ParameterByIndex(val index: Int) : TemplatePart(), TemplatePartIsParameter {
        override fun toString(): String = "~$index~"
    }
    data class TypeParameterByIndex(val index: Int) : TemplatePart() {
        override fun toString(): String = "~T$index~"
    }
    data class Import(val module: String): TemplatePart() {
        override fun toString(): String {
            return "<import $module>"
        }
    }

    interface TemplatePartIsParameter
}