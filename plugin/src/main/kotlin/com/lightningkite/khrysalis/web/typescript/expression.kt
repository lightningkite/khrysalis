package com.lightningkite.khrysalis.web.typescript

import com.lightningkite.khrysalis.ios.swift.parentOfType
import com.lightningkite.khrysalis.utils.camelCase
import com.lightningkite.khrysalis.utils.forEachBetween
import com.lightningkite.khrysalis.utils.forEachBetweenIndexed
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.TerminalNode
import org.jetbrains.kotlin.KotlinParser

fun TypescriptTranslator.registerExpression() {

//    handle<KotlinParser.NavigationSuffixContext> {
//        if (rule.CLASS() != null) {
//            -(".self")
//        } else {
//            emitDefault(rule, "") { child ->
//                when (child) {
//                    is ParserRuleContext -> true
//                    is TerminalNode -> when (child.symbol.type) {
//                        KotlinParser.NL -> false
//                        else -> true
//                    }
//                    else -> true
//                }
//            }
//        }
//    }
//    handle<KotlinParser.InfixFunctionCallContext> {
//        var lastIndex = 0
//        rule.rangeExpression()?.forEachBetweenIndexed(
//            forItem = { index, expression ->
//                lastIndex = index
//                -(rule.rangeExpression(index))
//            },
//            between = {
//                if (lastIndex == 0) {
//                    -(".")
//                } else {
//                    -(").")
//                }
//                -(rule.simpleIdentifier(lastIndex))
//                -('(')
//            }
//        )
//        if (rule.rangeExpression().size > 1) {
//            -(")")
//        }
//    }
//    handle<KotlinParser.ValueArgumentsContext> {
//        val sampleHasNoLabel = rule.valueArgument().firstOrNull()?.MULT() == null
//        if (rule.valueArgument().any { (it.MULT() == null) != sampleHasNoLabel }) {
//            println("WARNING: Function call at line ${rule.start.line} has some arguments with keys and some without.  This is not supported by the standard function definition converter.")
//        }
//        -("(")
//        rule.valueArgument().forEachBetween(
//            forItem = { -(it) },
//            between = { -(", ") }
//        )
//        -(")")
//    }
//    handle<KotlinParser.ValueArgumentContext> {
//        -(rule.expression())
//    }
//
//    handle<KotlinParser.AsExpressionContext> {
//        rule.asOperator().zip(rule.type()).takeUnless { it.isEmpty() }?.let {
//            it.asReversed().forEachIndexed { index, (op, type) ->
//                if (op.AS_SAFE() != null) {
//                    -("((): ")
//                    -(type)
//                    -(" | null => { const _item: any = ")
//                }
//            }
//            -(rule.prefixUnaryExpression())
//            it.forEachIndexed { index, (op, type) ->
//                if (op.AS_SAFE() != null) {
//                    if (type.isPrimitive()) {
//                        -("; if(typeof _item == \"${type.basic()?.text}\") return _item; else return null })()")
//                    } else {
//                        resolve(rule.parentOfType<KotlinParser.KotlinFileContext>()!!, type.basic()!!.text).firstOrNull()?.let {
//                            -("; if (_item.implementsInterface${it.name}) return _item; else return null })()")
//                        } ?: run {
//                            -("; if (_item instanceof ")
//                            -(type)
//                            -(") return _item; else return null })()")
//                        }
//                    }
//                } else {
//                    -(" as ")
//                    -(type)
//                }
//            }
//        } ?: run {
//            -(rule.prefixUnaryExpression())
//        }
//    }
//
//    handle<KotlinParser.PostfixUnaryExpressionContext> {
//
//        val primaryExpression = rule.primaryExpression()
//        val postfixUnarySuffixes = rule.postfixUnarySuffix()
//
//        if (primaryExpression.text == "R") {
//            val suffixes = postfixUnarySuffixes
//            val typeSuffix = suffixes.getOrNull(0)?.text?.removePrefix(".")
//            val resourceSuffix = suffixes.getOrNull(1)?.text?.removePrefix(".")
//            if (typeSuffix != null && resourceSuffix != null) {
//                val fixedSuffix = resourceSuffix.camelCase()
//                when (typeSuffix) {
//                    "string" -> -("ResourcesStrings.$fixedSuffix")
//                    "color" -> -("ResourcesColors.$fixedSuffix")
//                    "drawable" -> -("ResourcesDrawables.$fixedSuffix")
//                    else -> throw IllegalArgumentException("Unrecognized suffix $typeSuffix $resourceSuffix ($fixedSuffix)")
//                }
//                suffixes.drop(2).forEach {
//                    -(it)
//                }
//            }
//            return@handle
//        }
//
//        val lastCallSuffix = postfixUnarySuffixes?.lastOrNull()?.callSuffix()
//        if (lastCallSuffix != null) {
//            val primaryExpressionText = primaryExpression.text.trim()
//            val repl = functionReplacements[primaryExpressionText]
//            if (repl != null) {
//                repl.invoke(this, rule)
//                return@handle
//            }
//        }
//
//        // something(Thing.Subthing().modified)
//        if (primaryExpression.text.first().isUpperCase()) {
//            run breaker@{
//                val items = (listOf<ParserRuleContext>(primaryExpression) + postfixUnarySuffixes)
//                items.forEachIndexed { index, sub ->
//                    (sub as? KotlinParser.PostfixUnarySuffixContext)?.callSuffix()?.let {
//                        if (index + 1 >= items.size) {
//                            -("new ")
//                            items.forEach {
//                                -(it)
//                            }
//                        } else {
//                            -("(new ")
//                            items.subList(0, index + 1).forEach {
//                                -(it)
//                            }
//                            -(")")
//                            items.subList(index + 1, items.size).forEach {
//                                -(it)
//                            }
//                        }
//                        return@handle
//                    }
//                    (sub as? KotlinParser.PostfixUnarySuffixContext)?.navigationSuffix()?.let {
//                        if (it.simpleIdentifier().text.first().isUpperCase()) {
//                            return@forEachIndexed
//                        }
//                    }
//                    (sub as? KotlinParser.PrimaryExpressionContext)?.let { return@forEachIndexed }
//                    return@breaker
//                }
//            }
//        }
//
//        -(primaryExpression)
//        postfixUnarySuffixes.forEach {
//            -(it)
//        }
//    }
//
//    handle<KotlinParser.RangeExpressionContext> {
//        val expressions = rule.additiveExpression()
//        val parts = expressions.subList(1, expressions.size)
//        if (parts.isEmpty()) {
//            -(expressions.first())
//        } else {
//            parts.forEach {
//                -("makeRange(")
//            }
//            -(expressions.first())
//            parts.reversed().forEach {
//                -(", ")
//                -(it)
//                -(")")
//            }
//        }
//    }
}
