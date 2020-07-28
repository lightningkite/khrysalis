package com.lightningkite.khrysalis.typescript.replacements

data class Template(val parts: List<TemplatePart>) : Iterable<TemplatePart> {
    override fun iterator(): Iterator<TemplatePart> = parts.iterator()

    override fun toString(): String {
        return parts.joinToString("")
    }

    companion object {
        val tagRegex = Regex("""~([a-zA-Z0-9*]*)~""")
        fun escapedPartFromString(text: String, subMaps: Map<Int, List<Template>> = mapOf()): TemplatePart {
            val firstChar = text.firstOrNull() ?: return TemplatePart.Text("~")
            return when {
                text == "this" -> TemplatePart.Receiver
                text == "*" -> TemplatePart.AllParameters
                text == "thisExtension" -> TemplatePart.ExtensionReceiver
                text == "thisDispatch" -> TemplatePart.DispatchReceiver
                text == "value" -> TemplatePart.Value
                text == "operatorToken" -> TemplatePart.OperatorToken
                firstChar.isDigit() -> TemplatePart.ParameterByIndex(text.toInt())
                firstChar == 'T' && text.getOrNull(1)?.isDigit() == true -> TemplatePart.TypeParameterByIndex(
                    text.drop(1).toInt()
                )
                firstChar == 'L' && text.getOrNull(1)?.isDigit() == true -> {
                    val index = text.drop(1).toInt()
                    TemplatePart.LambdaParameterContents(
                        TemplatePart.ParameterByIndex(index),
                        subMaps[index] ?: listOf()
                    )
                }
                firstChar.isUpperCase() -> TemplatePart.TypeParameter(text)
                else -> TemplatePart.Parameter(text)
            }
        }
        fun partFromString(text: String, subMaps: Map<Int, List<Template>> = mapOf()): TemplatePart {
            if(text.startsWith("~")) {
                return escapedPartFromString(text.trim('~'), subMaps)
            } else {
                return TemplatePart.Text(text)
            }
        }
        fun partsFromString(text: String, subMaps: Map<Int, List<Template>> = mapOf()): List<TemplatePart> {
            val parts = ArrayList<TemplatePart>()
            var startIndex: Int = 0
            var currentIndex: Int = 0
            var inArg = false
            while(currentIndex < text.length){
                val c = text[currentIndex]
                if (c == '~') {
                    val part = text.substring(startIndex, currentIndex)
                    if(inArg){
                        parts += escapedPartFromString(part, subMaps)
                        startIndex = currentIndex + 1
                        inArg = false
                    } else {
                        if(startIndex != currentIndex){
                            parts += TemplatePart.Text(part)
                        }
                        inArg = true
                        startIndex = currentIndex + 1
                    }
                }
                currentIndex++
            }
            if(startIndex != currentIndex){
                val part = text.substring(startIndex, currentIndex)
                if(inArg){
                    parts += escapedPartFromString(part, subMaps)
                } else {
                    if(startIndex != currentIndex){
                        parts += TemplatePart.Text(part)
                    }
                }
            }
            return parts
        }
        fun fromString(text: String, imports: List<TemplatePart.Import> = listOf(), subMaps: Map<Int, List<Template>> = mapOf()): Template {
            return Template(partsFromString(text, subMaps) + imports)
        }
    }
}