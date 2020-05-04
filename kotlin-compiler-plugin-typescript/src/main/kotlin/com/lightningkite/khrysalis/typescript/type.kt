package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.generic.PartialTranslator
import com.lightningkite.khrysalis.generic.PartialTranslatorByType
import com.lightningkite.khrysalis.generic.line
import com.lightningkite.khrysalis.typescript.replacements.TemplatePart
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.descriptors.leastPermissiveDescriptor
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.lexer.KtModifierKeywordToken
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.referenceExpression
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierTypeOrDefault
import org.jetbrains.kotlin.types.FlexibleType
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.SimpleType
import org.jetbrains.kotlin.types.WrappedType
import org.jetbrains.kotlin.types.typeUtil.isByte
import org.jetbrains.kotlin.types.typeUtil.isInterface
import kotlin.text.Appendable

private val primitiveTypes = setOf(
    "kotlin.Byte",
    "kotlin.Short",
    "kotlin.Int",
    "kotlin.Long",
    "kotlin.UByte",
    "kotlin.UShort",
    "kotlin.UInt",
    "kotlin.ULong",
    "kotlin.Float",
    "kotlin.Double",
    "kotlin.String",
    "kotlin.Boolean",
    "kotlin.Unit",
    "kotlin.Any"
)
fun KotlinType.isPrimitive() = getJetTypeFqName(false) in primitiveTypes
data class BasicType(val type: KotlinType)

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

    handle<BasicType> {
        when(typedRule.type){
            is WrappedType -> {}
            is SimpleType -> {}
            is FlexibleType -> {}
        }
        -typedRule
            .type
            .getJetTypeFqName(false)
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
                    is TemplatePart.Import -> out.addImport(part)
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

    handle<KotlinType>(
        condition = {
            replacements.getType(typedRule) != null
        },
        priority = 10_000,
        action = {
            val type = typedRule
            val rule = replacements.getType(type)!!
            val baseType = type.constructor
            val typeParametersByName = type.arguments.withIndex().associate { (index, item) -> baseType.parameters[index].name.asString() to item }
            rule.template.parts.forEach { part ->
                when(part){
                    is TemplatePart.Import -> out.addImport(part)
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

    handle<BasicType>(
        condition = {
            replacements.getTypeRef(typedRule.type) != null
        },
        priority = 10_000,
        action = {
            val type = typedRule.type
            val rule = replacements.getType(type)!!
            val baseType = type.constructor
            val typeParametersByName = type.arguments.withIndex().associate { (index, item) -> baseType.parameters[index].name.asString() to item }
            rule.template.parts.forEach { part ->
                when(part){
                    is TemplatePart.Import -> out.addImport(part)
                    is TemplatePart.Text -> -part.string
                    TemplatePart.Receiver -> { }
                    TemplatePart.DispatchReceiver -> { }
                    TemplatePart.ExtensionReceiver -> { }
                    TemplatePart.Value -> { }
                    is TemplatePart.Parameter -> { }
                    is TemplatePart.ParameterByIndex -> { }
                    is TemplatePart.TypeParameter -> { }
                    is TemplatePart.TypeParameterByIndex -> { }
                }
            }
        }
    )

    handle<KtIsExpression> {
        val resolvedType = typedRule.typeReference!!.resolvedType!!
        if(typedRule.isNegated){
            -"!("
        }
        emitIsExpression(typedRule.leftHandSide, resolvedType)
        if(typedRule.isNegated){
            -")"
        }
    }

    handle<KtBinaryExpressionWithTypeRHS>(
        condition = { typedRule.operationReference.getReferencedNameElementType() == KtTokens.AS_SAFE },
        priority = 100,
        action = {
            -"((): "
            -typedRule.right
            -" | null => const _item = "
            -typedRule.left
            -"if("
            emitIsExpression("_item", typedRule.right!!.resolvedType!!)
            -") { return _item as "
            -typedRule.right
            -"; } else { return null; }"
        }
    )
}

fun PartialTranslatorByType<TypescriptFileEmitter, Unit, Any>.ContextByType<*>.emitIsExpression(
    expression: Any?,
    resolvedType: KotlinType
) {
    when {
        resolvedType.isInterface() -> {
            -'('
            -expression
            -" as any).implementsInterface"
            -resolvedType.getJetTypeFqName(false).split('.').joinToString("") { it.capitalize() }
        }
        resolvedType.isPrimitive() -> {
            -"typeof ("
            -expression
            -") == \""
            -resolvedType
            -'"'
        }
        else -> {
            -expression
            -" instanceOf "
            -BasicType(resolvedType)
        }
    }
}
