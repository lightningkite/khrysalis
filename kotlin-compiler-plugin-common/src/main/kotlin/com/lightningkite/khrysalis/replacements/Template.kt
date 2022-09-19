package com.lightningkite.khrysalis.replacements

data class Template(
    val parts: List<TemplatePart>,
    val imports: List<Import> = listOf()
) : Iterable<TemplatePart> {

    private fun getParts(part: TemplatePart): Sequence<TemplatePart> = sequenceOf(part) + when(part){
        is TemplatePart.LambdaParameterContents -> part.paramMap.asSequence().flatMap { it.allParts }
        else -> emptySequence()
    }
    val allParts: Sequence<TemplatePart> get() = parts.asSequence().flatMap { getParts(it) }

    override fun iterator(): Iterator<TemplatePart> = parts.iterator()

    val isThisDot: Boolean get() {
        return parts.getOrNull(0) is TemplatePart.Receiver && parts.getOrNull(1)
            .let { it is TemplatePart.Text && it.string.startsWith('.') }
    }

    override fun toString(): String {
        if(imports.isNotEmpty()){
            return parts.joinToString("") + "  --  " + imports.joinToString()
        }
        return parts.joinToString("")
    }

    fun replace(value: String, with: String): Template {
        return this.copy(parts = parts.map {
            if(it is TemplatePart.Text)
                TemplatePart.Text(it.string.replace(value, with))
            else it
        })
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
                firstChar == 'R' && text.getOrNull(1)?.isDigit() == true -> TemplatePart.ReifiedTypeParameterByIndex(
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
        fun fromString(text: String, imports: List<Import> = listOf(), subMaps: Map<Int, List<Template>> = mapOf()): Template {
            return Template(partsFromString(text, subMaps), imports)
        }
    }
}