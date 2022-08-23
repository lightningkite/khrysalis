package com.lightningkite.khrysalis.replacements

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.util.*

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(
        FunctionReplacement::class,
        name = "call"
    ),
    JsonSubTypes.Type(
        GetReplacement::class,
        name = "get"
    ),
    JsonSubTypes.Type(
        SetReplacement::class,
        name = "set"
    ),
    JsonSubTypes.Type(
        TypeReplacement::class,
        name = "type"
    ),
    JsonSubTypes.Type(
        TypeRefReplacement::class,
        name = "typeRef"
    ),
    JsonSubTypes.Type(
        TypeReifiedReplacement::class,
        name = "typeReified"
    ),
    JsonSubTypes.Type(
        CastRule::class,
        name = "cast"
    )
)
interface ReplacementRule : Comparable<ReplacementRule> {
    @get:JsonIgnore() val priority: Int get() = 0
    val debug: Boolean get() = false
    override fun compareTo(other: ReplacementRule): Int {
        var result = other.priority.compareTo(this.priority)
        if(result == 0){
            result = this.hashCode().compareTo(other.hashCode())
        }
        return result
    }
    fun merge(other: ReplacementRule): Boolean {
        return false
    }
}

fun <E: ReplacementRule> TreeSet<E>.merge(item: E) {
    for(existing in this){
        if(existing.merge(item)) return
    }
    add(item)
}