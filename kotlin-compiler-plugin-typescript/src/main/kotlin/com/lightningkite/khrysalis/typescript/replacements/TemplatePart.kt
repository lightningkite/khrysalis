package com.lightningkite.khrysalis.typescript.replacements

sealed class TemplatePart {
    class Text(val string: String) : TemplatePart()
    object Receiver : TemplatePart()
    object DispatchReceiver : TemplatePart()
    object ExtensionReceiver : TemplatePart()
    object Value : TemplatePart()
    object AllParameters: TemplatePart()
    class Parameter(val name: String) : TemplatePart()
    class TypeParameter(val name: String) : TemplatePart()
    class ParameterByIndex(val index: Int) : TemplatePart()
    class TypeParameterByIndex(val index: Int) : TemplatePart()
}