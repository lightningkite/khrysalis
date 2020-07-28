package com.lightningkite.khrysalis.typescript.replacements

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

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
        AttributeReplacement::class,
        name = "attribute"
    )
)
interface ReplacementRule : Comparable<ReplacementRule> {
    val priority: Int get() = 0
    override fun compareTo(other: ReplacementRule): Int {
        var result = other.priority.compareTo(this.priority)
        if(result == 0){
            result = this.hashCode().compareTo(other.hashCode())
        }
        return result
    }
}