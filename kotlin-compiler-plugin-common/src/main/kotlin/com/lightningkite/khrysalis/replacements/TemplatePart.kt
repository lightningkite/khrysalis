package com.lightningkite.khrysalis.replacements

sealed class TemplatePart {
    abstract class Expression: TemplatePart()
    data class Text(val string: String) : TemplatePart() {
        override fun toString(): String = string
    }
    object Receiver : Expression() { override fun toString() = "~this~" }
    object DispatchReceiver : Expression() { override fun toString() = "~thisDispatch~" }
    object ExtensionReceiver : Expression() { override fun toString() = "~thisExtension~" }
    object Value : Expression() { override fun toString() = "~value~" }
    object AllParameters: TemplatePart() { override fun toString() = "~*~" }
    object OperatorToken: TemplatePart() { override fun toString() = "~operatorToken~" }
    data class Parameter(val name: String) : Expression(), TemplatePartIsParameter {
        override fun toString(): String = "~$name~"
    }
    data class TypeParameter(val name: String) : TemplatePart() {
        override fun toString(): String = "~$name~"
    }
    data class LambdaParameterContents(
        val pointer: TemplatePartIsParameter,
        val paramMap: List<Template>
    ) : TemplatePart() {
        override fun toString(): String = when(pointer){
            is ParameterByIndex -> "~L${pointer.index}~"
            is Parameter -> "~L${pointer.name}~"
            else -> "~L?~"
        }
    }
    data class ParameterByIndex(val index: Int) : Expression(), TemplatePartIsParameter {
        override fun toString(): String = "~$index~"
    }
    data class TypeParameterByIndex(val index: Int) : TemplatePart() {
        override fun toString(): String = "~T$index~"
    }

    interface TemplatePartIsParameter
}