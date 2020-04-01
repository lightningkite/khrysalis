package com.lightningkite.khrysalis.preparse

data class PreparseData(
    val interfaces: Map<String, InterfaceData> = mapOf(),
    val declarations: Map<String, String> = mapOf()
)

fun Sequence<PreparseData>.merged() = PreparseData(
    interfaces = HashMap<String, InterfaceData>().apply {
        for(item in this@merged){
            putAll(item.interfaces)
        }
    },
    declarations = HashMap<String, String>().apply {
        for(item in this@merged){
            putAll(item.declarations)
        }
    }
)

fun Sequence<PreparseData>.mergedInterfaces() = HashMap<String, InterfaceData>().apply {
    for(item in this@mergedInterfaces){
        putAll(item.interfaces)
    }
}

fun Sequence<PreparseData>.mergedDeclarations() = HashMap<String, String>().apply {
    for(item in this@mergedDeclarations){
        putAll(item.declarations)
    }
}
