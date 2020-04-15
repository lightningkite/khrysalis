package com.lightningkite.khrysalis.web.typescript

import com.lightningkite.khrysalis.generic.PartialTranslatorByType
import com.lightningkite.khrysalis.generic.emitDefault
import com.lightningkite.khrysalis.generic.line
import com.lightningkite.khrysalis.ios.swift.actuals.visibility
import com.lightningkite.khrysalis.utils.forEachBetween
import org.antlr.v4.runtime.ParserRuleContext
import org.jetbrains.kotlin.KotlinParser
import java.lang.Appendable

fun PartialTranslatorByType<Appendable, Unit, ParserRuleContext>.ContextByType<*>.emitWithoutTypeArguments(ctx: KotlinParser.UserTypeContext) {
    ctx.simpleUserType().forEachBetween(
        forItem = { -it.simpleIdentifier() },
        between = { -"." }
    )
}

fun KotlinParser.UserTypeContext.withoutArgumentText(): String = simpleUserType().joinToString(".") { it.simpleIdentifier().text }
fun KotlinParser.UserTypeContext.translatedTextWithoutArguments(
    ctx: PartialTranslatorByType<Appendable, Unit, ParserRuleContext>.ContextByType<*>
): String = simpleUserType().joinToString(".") {
    buildString { ctx.partialTranslator.parent!!.translate(it, this) }
}

fun KotlinParser.TypeContext.simpleUserTypeContext(): KotlinParser.SimpleUserTypeContext? = this.typeReference()?.userType()?.simpleUserType(0)
    ?: this.nullableType()?.let {
        it.parenthesizedType()?.type()?.simpleUserTypeContext() ?: it.typeReference()?.userType()?.simpleUserType(0)
    } ?: this.parenthesizedType()?.type()?.simpleUserTypeContext()

fun KotlinParser.TypeContext.userTypeContext(): KotlinParser.UserTypeContext? = this.typeReference()?.userType()
    ?: this.nullableType()?.let {
        it.parenthesizedType()?.type()?.userTypeContext() ?: it.typeReference()?.userType()
    } ?: this.parenthesizedType()?.type()?.userTypeContext()

val primitiveTypes = setOf("Number", "String", "Boolean", "Unit", "Any" )

fun KotlinParser.TypeContext.isPrimitive(): Boolean = this.typeReference()?.userType()?.simpleUserType(0)?.let { it.text in primitiveTypes }
    ?: this.nullableType()?.let {
        it.parenthesizedType()?.type()?.isPrimitive() ?: it.typeReference()?.userType()?.simpleUserType(0)?.let { it.text in primitiveTypes }
    } ?: this.parenthesizedType()?.type()?.isPrimitive()
    ?: false

fun TypescriptTranslator.registerType(){

    handle<KotlinParser.TypeAliasContext> {
        val rule = typedRule
        val item = rule
        val name = item.simpleIdentifier().text
        line {
            if(item.visibility().isExposed){
                -("export ")
            }
            -("type ")
            -(name)
            item.typeParameters()?.let {
                write(it)
            }
            -(" = ")
            write(item.type())
            -(';')
        }
        line {
            if(item.visibility().isExposed){
                -("export ")
            }
            -("let ")
            -(name)
            -(" = ")
            when(val t = item.type().userTypeContext()!!.translatedTextWithoutArguments(this)){
                in primitiveTypes -> -t.capitalize()
                null -> throw IllegalArgumentException("Cannot do a typealias to a function type")
                else -> -t
            }
            -(';')
        }
    }
    handle<KotlinParser.FunctionTypeContext> {
        val rule = typedRule
        -"("
        rule.functionTypeParameters().parameter().forEachBetween(
            forItem = { -it },
            between = { -", " }
        )
        -") => "
        -rule.type()
    }
    handle<KotlinParser.UserTypeContext> { emitDefault(rule, "") }
    handle<KotlinParser.NullableTypeContext> {
        val rule = typedRule
        -(rule.typeReference() ?: rule.parenthesizedType())
        -" | null"
    }
    handle<KotlinParser.SimpleUserTypeContext> {
        val rule = typedRule
        val name = typeReplacements[rule.simpleIdentifier().text] ?: rule.simpleIdentifier().text
        if(name.endsWith("*")){
            -name.removeSuffix("*")
            return@handle
        }
        -name
        -rule.typeArguments()
    }
    handle<KotlinParser.TypeArgumentsContext> {
        val rule = typedRule
        -"<"
        rule.typeProjection().forEachBetween(
            forItem = { -it.type() },
            between = { -", " }
        )
        -">"
    }

    handle<KotlinParser.ParenthesizedTypeContext> { emitDefault(rule, "") }
    handle<KotlinParser.ParenthesizedUserTypeContext> { emitDefault(rule, "") }

    handle<KotlinParser.AsExpressionContext> {
        val rule = typedRule
        rule.asOperator().zip(rule.type()).takeUnless { it.isEmpty() }?.let {
            it.asReversed().forEachIndexed { index, (op, type) ->
                if (op.AS_SAFE() != null) {
                    -("((): ")
                    -(type)
                    -(" | null => { const _item: any = ")
                }
            }
            -(rule.prefixUnaryExpression())
            it.forEachIndexed { index, (op, type) ->
                if (op.AS_SAFE() != null) {
                    if (type.isPrimitive()) {
                        -"; if(typeof _item == \""
                        -type.simpleUserTypeContext()
                        -"\") return _item; else return null })()"
                    } else {
                        resolve(currentFile, type.userTypeContext()!!.translatedTextWithoutArguments(this)).firstOrNull()?.let {
                            -"; if ((_item as any).implementsInterface${it.name}) return _item as "
                            -type
                            -"; else return null })()"
                        } ?: run {
                            -"; if (_item instanceof "
                            -type
                            -") return _item; else return null })()"
                        }
                    }
                } else {
                    -(" as ")
                    -(type)
                }
            }
        } ?: run {
            -(rule.prefixUnaryExpression())
        }
    }
}
