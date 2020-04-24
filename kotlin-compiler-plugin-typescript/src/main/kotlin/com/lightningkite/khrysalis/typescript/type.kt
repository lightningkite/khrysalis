package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.generic.PartialTranslatorByType
import com.lightningkite.khrysalis.generic.line
import com.lightningkite.khrysalis.typescript.replacements.TemplatePart
import org.jetbrains.kotlin.descriptors.leastPermissiveDescriptor
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.lexer.KtModifierKeywordToken
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.referenceExpression
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierTypeOrDefault
import org.jetbrains.kotlin.types.FlexibleType
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.SimpleType
import org.jetbrains.kotlin.types.WrappedType
import java.lang.Appendable
//
//fun PartialTranslatorByType<Appendable, Unit, ParserRuleContext>.ContextByType<*>.emitWithoutTypeArguments(ctx: KotlinParser.UserTypeContext) {
//    ctx.simpleUserType().forEachBetween(
//        forItem = { -it.simpleIdentifier() },
//        between = { -"." }
//    )
//}
//
//fun KotlinParser.UserTypeContext.withoutArgumentText(): String = simpleUserType().joinToString(".") { it.simpleIdentifier().text }
//fun KotlinParser.UserTypeContext.translatedTextWithoutArguments(
//    ctx: PartialTranslatorByType<Appendable, Unit, ParserRuleContext>.ContextByType<*>
//): String = simpleUserType().joinToString(".") {
//    buildString { ctx.partialTranslator.parent!!.translate(it, this) }
//}
//
//fun KotlinParser.TypeContext.simpleUserTypeContext(): KotlinParser.SimpleUserTypeContext? = this.typeReference()?.userType()?.simpleUserType(0)
//    ?: this.nullableType()?.let {
//        it.parenthesizedType()?.type()?.simpleUserTypeContext() ?: it.typeReference()?.userType()?.simpleUserType(0)
//    } ?: this.parenthesizedType()?.type()?.simpleUserTypeContext()
//
//fun KotlinParser.TypeContext.userTypeContext(): KotlinParser.UserTypeContext? = this.typeReference()?.userType()
//    ?: this.nullableType()?.let {
//        it.parenthesizedType()?.type()?.userTypeContext() ?: it.typeReference()?.userType()
//    } ?: this.parenthesizedType()?.type()?.userTypeContext()
//
//val primitiveTypes = setOf("Number", "String", "Boolean", "Unit", "Any" )
//
//fun KotlinParser.TypeContext.isPrimitive(): Boolean = this.typeReference()?.userType()?.simpleUserType(0)?.let { it.text in primitiveTypes }
//    ?: this.nullableType()?.let {
//        it.parenthesizedType()?.type()?.isPrimitive() ?: it.typeReference()?.userType()?.simpleUserType(0)?.let { it.text in primitiveTypes }
//    } ?: this.parenthesizedType()?.type()?.isPrimitive()
//    ?: false

class Box<T>(thing: Int){}
typealias AltBox<T> = com.lightningkite.khrysalis.typescript.Box<T>

fun TypescriptTranslator.registerType(){

    handle<KtTypeAlias> {
        if(typedRule.visibilityModifierTypeOrDefault().value == "public") {
            -"export "
        }
        -"type "
        -typedRule.nameIdentifier
        -typedRule.typeParameterList
        -" = "
        -typedRule.getTypeReference()
        -";\n"

        if(typedRule.visibilityModifierTypeOrDefault().value == "public") {
            -"export "
        }
        -"let "
        -typedRule.nameIdentifier
        -" = "
        fun handleTypeElement(e: KtTypeElement){
            when(e){
                is KtFunctionType -> {}
                is KtNullableType -> {}
                is KtSelfType -> {}
                is KtUserType -> {
                    e.qualifier?.let {
                        handleTypeElement(it)
                        -'.'
                    }
                    -e.referencedName
                }
            }
        }
        handleTypeElement(typedRule.getTypeReference()!!.typeElement!!)
        -";\n"
    }

    handle<KotlinType> {
        when(typedRule){
            is WrappedType -> {}
            is SimpleType -> {}
            is FlexibleType -> {}
        }
        -typedRule
            .getJetTypeFqName(true)
            .split('.')
            .dropWhile { it.firstOrNull()?.isLowerCase() != false }
            .joinToString(".")
    }

    handle<KtTypeReference>(
        condition = {
            val type = typedRule.resolvedType ?: return@handle false
            replacements.getType(type) != null
        },
        priority = 10_000,
        action = {
            val type = typedRule.resolvedType!!
            val rule = replacements.getType(type)!!
            val baseType = type.constructor
            val typeParametersByName = type.arguments.withIndex().associate { (index, item) -> baseType.parameters[index].name.asString() to item }
            rule.template.parts.forEach { part ->
                when(part){
                    is TemplatePart.Text -> -part.string
                    TemplatePart.Receiver -> { }
                    TemplatePart.DispatchReceiver -> { }
                    TemplatePart.ExtensionReceiver -> { }
                    TemplatePart.Value -> { }
                    is TemplatePart.Parameter -> { }
                    is TemplatePart.ParameterByIndex -> { }
                    is TemplatePart.TypeParameter -> -(typeParametersByName[part.name])
                    is TemplatePart.TypeParameterByIndex -> -(type.arguments.getOrNull(part.index))
                }
            }
        }
    )

//    handle<KotlinParser.TypeAliasContext> {
//        val rule = typedRule
//        val item = rule
//        val name = item.simpleIdentifier().text
//        line {
//            if(item.visibility().isExposed){
//                -("export ")
//            }
//            -("type ")
//            -(name)
//            item.typeParameters()?.let {
//                write(it)
//            }
//            -(" = ")
//            write(item.type())
//            -(';')
//        }
//        line {
//            if(item.visibility().isExposed){
//                -("export ")
//            }
//            -("let ")
//            -(name)
//            -(" = ")
//            when(val t = item.type().userTypeContext()!!.translatedTextWithoutArguments(this)){
//                in primitiveTypes -> -t.capitalize()
//                null -> throw IllegalArgumentException("Cannot do a typealias to a function type")
//                else -> -t
//            }
//            -(';')
//        }
//    }
//    handle<KotlinParser.FunctionTypeContext> {
//        val rule = typedRule
//        -"("
//        rule.functionTypeParameters().parameter().forEachBetween(
//            forItem = { -it },
//            between = { -", " }
//        )
//        -") => "
//        -rule.type()
//    }
//    handle<KotlinParser.UserTypeContext> { emitDefault(rule, "") }
//    handle<KotlinParser.NullableTypeContext> {
//        val rule = typedRule
//        -(rule.typeReference() ?: rule.parenthesizedType())
//        -" | null"
//    }
//    handle<KotlinParser.SimpleUserTypeContext> {
//        val rule = typedRule
//        val name = typeReplacements[rule.simpleIdentifier().text] ?: rule.simpleIdentifier().text
//        if(name.endsWith("*")){
//            -name.removeSuffix("*")
//            return@handle
//        }
//        -name
//        -rule.typeArguments()
//    }
//    handle<KotlinParser.TypeArgumentsContext> {
//        val rule = typedRule
//        -"<"
//        rule.typeProjection().forEachBetween(
//            forItem = { -it.type() },
//            between = { -", " }
//        )
//        -">"
//    }
//
//    handle<KotlinParser.ParenthesizedTypeContext> { emitDefault(rule, "") }
//    handle<KotlinParser.ParenthesizedUserTypeContext> { emitDefault(rule, "") }
//
//    handle<KotlinParser.AsExpressionContext> {
//        val rule = typedRule
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
//                        -"; if(typeof _item == \""
//                        -type.simpleUserTypeContext()
//                        -"\") return _item; else return null })()"
//                    } else {
//                        resolve(currentFile, type.userTypeContext()!!.translatedTextWithoutArguments(this)).firstOrNull()?.let {
//                            -"; if ((_item as any).implementsInterface${it.name}) return _item as "
//                            -type
//                            -"; else return null })()"
//                        } ?: run {
//                            -"; if (_item instanceof "
//                            -type
//                            -") return _item; else return null })()"
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
}
