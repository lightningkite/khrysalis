package com.lightningkite.khrysalis.preparse

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.lightningkite.khrysalis.utils.Versioned
import java.io.File

data class PreparseData(
    val interfaces: Map<String, InterfaceData> = mapOf(),
    val declarations: Map<String, String> = mapOf()
) {
    companion object {
        fun from(file: File): PreparseData {
            return jacksonObjectMapper().readValue<Versioned<Map<String, FileCache>>>(file).value.values.asSequence().map { it.data }.merged()
        }
    }
}

operator fun PreparseData.plus(other: PreparseData): PreparseData = sequenceOf(this, other).merged()

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
