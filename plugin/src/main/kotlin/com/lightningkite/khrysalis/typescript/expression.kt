package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.utils.forEachBetween
import com.lightningkite.khrysalis.utils.forEachBetweenIndexed
import org.jetbrains.kotlin.KotlinParser

fun TypescriptAltListener.registerExpression() {

    handle<KotlinParser.InfixFunctionCallContext> { item ->
        var lastIndex = 0
        item.rangeExpression()?.forEachBetweenIndexed(
            forItem = { index, expression ->
                lastIndex = index
                write(item.rangeExpression(index))
            },
            between = {
                if (lastIndex == 0) {
                    direct.append(".")
                } else {
                    direct.append(").")
                }
                write(item.simpleIdentifier(lastIndex))
                direct.append('(')
            }
        )
        if (item.rangeExpression().size > 1) {
            direct.append(")")
        }
    }
    handle<KotlinParser.ValueArgumentsContext> {
        val sampleHasNoLabel = it.valueArgument().firstOrNull()?.MULT() == null
        if (it.valueArgument().any { (it.MULT() == null) != sampleHasNoLabel }) {
            println("WARNING: Function call at line ${it.start.line} has some arguments with keys and some without.  This is not supported by the standard function definition converter.")
        }
        direct.append("(")
        it.valueArgument().forEachBetween(
            forItem = { write(it) },
            between = { direct.append(", ") }
        )
        direct.append(")")
    }
    handle<KotlinParser.ValueArgumentContext> {
        write(it.expression())
    }

    handle<KotlinParser.AsExpressionContext> { ctx ->
        ctx.asOperator().zip(ctx.type()).takeUnless { it.isEmpty() }?.let {
            it.asReversed().forEachIndexed { index, (op, type) ->
                if (op.AS_SAFE() != null) {
                    direct.append("((): ")
                    write(type)
                    direct.append(" | null => { const _item: any = ")
                }
            }
            write(ctx.prefixUnaryExpression())
            it.forEachIndexed { index, (op, type) ->
                if (op.AS_SAFE() != null) {
                    val basicType = type.getBasicType()
                    if(basicType.startsWith('*')) {
                        direct.append("; if(typeof _item == \"${basicType.drop(1)}\") return _item; else return null })()")
                    } else {
                        resolve(basicType).firstOrNull()?.let {
                            direct.append("; if (_item.implementsInterface${it.name}) return _item; else return null })()")
                        } ?: run {
                            direct.append("; if (_item instanceof ")
                            write(type)
                            direct.append(") return _item; else return null })()")
                        }
                    }
                } else {
                    direct.append(" as ")
                    write(type)
                }
            }
        } ?: run {
            write(ctx.prefixUnaryExpression())
        }
    }
}

fun KotlinParser.TypeContext.getBasicType(): String {
    this.functionType()?.let { throw IllegalArgumentException("Cannot do a safe cast to uncheckable function type.") }
    this.nullableType()?.let { return it.typeReference()?.getBasicType() ?: it.parenthesizedType()?.getBasicType()!! }
    this.typeReference()?.let { return it.getBasicType() }
    this.parenthesizedType()?.let { return it.getBasicType() }
    throw IllegalStateException()
}

fun KotlinParser.TypeReferenceContext.getBasicType(): String {
    return when (val raw = this.userType().text) {
        "Boolean" -> "*boolean"
        "Char",
        "String" -> "*string"
        "Byte",
        "Short",
        "Int",
        "Float",
        "Double",
        "Long" -> "*number"
        else -> raw
    }
}

fun KotlinParser.ParenthesizedTypeContext.getBasicType(): String {
    return this.type().getBasicType()
}
