package com.lightningkite.khrysalis.typescript.replacements

sealed class TemplatePart {
    class Text(val string: String) : TemplatePart()
    object Receiver : TemplatePart()
    object DispatchReceiver : TemplatePart()
    object ExtensionReceiver : TemplatePart()
    object Value : TemplatePart()
    object AllParameters: TemplatePart()
    object OperatorToken: TemplatePart()
    data class Parameter(val name: String) : TemplatePart()
    data class TypeParameter(val name: String) : TemplatePart()
    data class ParameterByIndex(val index: Int) : TemplatePart()
    data class TypeParameterByIndex(val index: Int) : TemplatePart()
    data class Import(val path: String, val identifier: String, val asName: String? = null): TemplatePart()
}