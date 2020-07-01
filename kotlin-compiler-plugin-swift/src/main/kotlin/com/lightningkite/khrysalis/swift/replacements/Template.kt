package com.lightningkite.khrysalis.swift.replacements

data class Template(val parts: List<TemplatePart>) : Iterable<TemplatePart> {
    override fun iterator(): Iterator<TemplatePart> = parts.iterator()

    override fun toString(): String {
        return parts.joinToString("")
    }

    companion object {
        val tagRegex = Regex("""~([a-zA-Z0-9*]*)~""")
        fun fromString(text: String, imports: List<TemplatePart.Import> = listOf(), subMaps: Map<Int, List<List<TemplatePart>>> = mapOf()): Template {
            val tags = tagRegex.findAll(text).map {
                val tag = it.groupValues[1]
                val firstChar = tag.firstOrNull() ?: return@map TemplatePart.Text("~")
                when {
                    tag == "this" -> TemplatePart.Receiver
                    tag == "*" -> TemplatePart.AllParameters
                    tag == "thisExtension" -> TemplatePart.ExtensionReceiver
                    tag == "thisDispatch" -> TemplatePart.DispatchReceiver
                    tag == "value" -> TemplatePart.Value
                    tag == "operatorToken" -> TemplatePart.OperatorToken
                    firstChar.isDigit() -> TemplatePart.ParameterByIndex(tag.toInt())
                    firstChar == 'T' && tag.getOrNull(1)?.isDigit() == true -> TemplatePart.TypeParameterByIndex(
                        tag.drop(1).toInt()
                    )
                    firstChar == 'L' && tag.getOrNull(1)?.isDigit() == true -> {
                        val index = tag.drop(1).toInt()
                        TemplatePart.LambdaParameterContents(
                            TemplatePart.ParameterByIndex(index),
                            subMaps[index] ?: listOf()
                        )
                    }
                    firstChar.isUpperCase() -> TemplatePart.TypeParameter(tag)
                    else -> TemplatePart.Parameter(tag)
                }
            }.toList()
            val other = text.split(tagRegex).map {
                TemplatePart.Text(
                    it
                )
            }
            return Template(
                imports + other
                    .withIndex()
                    .flatMap { (index, it) ->
                        listOf(
                            it,
                            tags.getOrElse(index) {
                                TemplatePart.Text(
                                    ""
                                )
                            })
                    }
                    .filter { it !is TemplatePart.Text || it.string.isNotEmpty() }
            )
        }
    }
}