package com.lightningkite.kwift.views

import com.lightningkite.kwift.*
import com.lightningkite.kwift.views.StringResource

class DjangoErrorTranslator(
    val connectivityErrorResource: StringResource,
    val serverErrorResource: StringResource,
    val otherErrorResource: StringResource
) {

    fun handleNode(builder: StringBuilder, node: Any?) {
        if(node == null) return
        when (node) {
            is JsonMap -> {
                for ((key, value) in node) {
                    handleNode(builder, value)
                }
            }
            is JsonList -> {
                for(value in node){
                    handleNode(builder, value)
                }
            }
            is String -> {
                //Rough check for human-readability - sentences start with uppercase and will have spaces
                if(node.isNotEmpty() && node[0].isUpperCase() && node.contains(" ")) {
                    builder.appendln(node)
                }
            }
        }
    }
    fun parseError(code: Int, error: String?): ViewString? {
        var resultError: ViewString? = null
        when(code / 100){
            0 -> resultError = ViewStringResource(connectivityErrorResource)
            1, 2, 3 -> {}
            4 -> {
                val errorJson = error?.fromJsonStringUntyped()
                if(errorJson != null){
                    val builder = StringBuilder()
                    handleNode(builder, errorJson)
                    resultError = ViewStringRaw(builder.toString())
                } else {
                    resultError = ViewStringRaw(error ?: "")
                }
            }
            5 -> resultError = ViewStringResource(serverErrorResource)
            else -> resultError = ViewStringResource(otherErrorResource)
        }
        return resultError
    }

    fun <T> wrap(
        callback: @escaping() (result: T?, error: ViewString?)->Unit
    ): (code: Int, result: T?, error: String?)->Unit {
        return { code, result, error ->
            callback(result, this.parseError(code, error))
        }
    }

    fun wrapNoResponse(
        callback: @escaping() (error: ViewString?)->Unit
    ): (code: Int, error: String?)->Unit {
        return { code, error ->
            callback(this.parseError(code, error))
        }
    }

}
