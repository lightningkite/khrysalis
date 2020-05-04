package com.lightningkite.khrysalis.typescript.replacements

sealed class TemplatePart {
    class Text(val string: String) : TemplatePart()
    object Receiver : TemplatePart()
    object DispatchReceiver : TemplatePart()
    object ExtensionReceiver : TemplatePart()
    object Value : TemplatePart()
    object AllParameters: TemplatePart()
    object OperatorToken: TemplatePart()
    class Parameter(val name: String) : TemplatePart()
    class TypeParameter(val name: String) : TemplatePart()
    class ParameterByIndex(val index: Int) : TemplatePart()
    class TypeParameterByIndex(val index: Int) : TemplatePart()
    class Import(val path: String, val identifier: String, val asName: String? = null): TemplatePart()
}