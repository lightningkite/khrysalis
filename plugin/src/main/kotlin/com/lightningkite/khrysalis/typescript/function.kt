package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.swift.actuals.visibility
import com.lightningkite.khrysalis.swift.handleLet
import com.lightningkite.khrysalis.swift.parentIfType
import com.lightningkite.khrysalis.swift.writeTypeArguments
import com.lightningkite.khrysalis.utils.camelCase
import com.lightningkite.khrysalis.utils.forEachBetween
import org.jetbrains.kotlin.KotlinParser

fun TypescriptAltListener.registerFunction() {

    this.handle<KotlinParser.FunctionDeclarationContext> {

        fun emitStandardFunction(it: KotlinParser.FunctionDeclarationContext, beforeStart: Appendable.()->Unit){
            line {
                beforeStart()
                write(it.simpleIdentifier())
                it.typeParameters()?.let {
                    append('<')
                    it.typeParameter().forEachBetween(
                        forItem = {
                            write(it.simpleIdentifier())
                            it.type()?.let {
                                append(" extends ")
                                write(it)
                            }
                        },
                        between = { append(", ") }
                    )
                    append('>')
                }
                append("(")
                it.functionValueParameters().functionValueParameter().forEachBetween(
                    forItem = {
                        write(it.parameter().simpleIdentifier())
                        append(": ")
                        write(it.parameter().type())
                        it.expression()?.let {
                            append(" = ")
                            write(it)
                        }
                    },
                    between = { append(", ") }
                )
                append(")")
                it.type()?.let {
                    append(": ")
                    write(it)
                    append(" ")
                }
                it.functionBody()?.let {
                    append("{")
                }
            }
            it.functionBody()?.let {
                tab {
                    it.expression()?.let {
                        line {
                            append("return ")
                            write(it)
                            append(";")
                        }
                    }
                    it.block()?.statements()?.let { write(it) }
                }
                line("}")
            }
        }

        //CASES: Top-level, method, extension, inside other function
        val isTopLevel = it
            .parentIfType<KotlinParser.DeclarationContext>()
            ?.parentIfType<KotlinParser.TopLevelObjectContext>() != null
        val isMethod = it
            .parentIfType<KotlinParser.DeclarationContext>()
            ?.parentIfType<KotlinParser.ClassMemberDeclarationContext>() != null
        val isExtension = it.receiverType() != null

        when {
            isExtension -> {
            }
            isTopLevel -> {
                emitStandardFunction(it){
                    if (it.visibility().isExposed) {
                        append("export ")
                    }
                    append("function ")
                }
            }
            isMethod -> {
            }
            else -> {
                emitStandardFunction(it){
                    append("function ")
                }
            }
        }

    }

    handle<KotlinParser.PostfixUnaryExpressionContext> { item ->

        if(item.primaryExpression().text == "R"){
            val suffixes = item.postfixUnarySuffix()
            val typeSuffix = suffixes.getOrNull(0)?.text?.removePrefix(".")
            val resourceSuffix = suffixes.getOrNull(1)?.text?.removePrefix(".")
            if(typeSuffix != null && resourceSuffix != null) {
                val fixedSuffix = resourceSuffix.camelCase()
                when(typeSuffix){
                    "string" -> direct.append("ResourcesStrings.$fixedSuffix")
                    "color" -> direct.append("ResourcesColors.$fixedSuffix")
                    "drawable" -> direct.append("ResourcesDrawables.$fixedSuffix")
                    else -> throw IllegalArgumentException("Unrecognized suffix $typeSuffix $resourceSuffix ($fixedSuffix)")
                }
                suffixes.drop(2).forEach {
                    write(it)
                }
            }
            return@handle
        }

        val lastCallSuffix = item.postfixUnarySuffix()?.lastOrNull()?.callSuffix()
        if (lastCallSuffix != null) {
            val primaryExpressionText = item.primaryExpression().text.trim()
            val repl = functionReplacements[primaryExpressionText]
            if (repl != null) {
                repl.invoke(this, item)
                return@handle
            }
        }

        write(item.primaryExpression())
        item.postfixUnarySuffix().forEach {
            write(it)
        }
    }

    handle<KotlinParser.StatementsContext> {
        it.statement().forEach {
            line {
                write(it)
                append(';')
            }
        }
    }
}
