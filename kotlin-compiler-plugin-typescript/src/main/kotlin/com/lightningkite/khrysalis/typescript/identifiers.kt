package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.util.fqNameWithTypeArgs
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.codegen.AccessorForCompanionObjectInstanceFieldDescriptor
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.contracts.parsing.isInvocationKindEnum
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType
import org.jetbrains.kotlin.psi.psiUtil.getTextWithLocation
import org.jetbrains.kotlin.psi.synthetics.SyntheticClassOrObjectDescriptor
import org.jetbrains.kotlin.resolve.calls.smartcasts.MultipleSmartCasts
import com.lightningkite.khrysalis.analysis.*
import org.jetbrains.kotlin.resolve.lazy.descriptors.LazyClassDescriptor

fun TypescriptTranslator.registerIdentifiers(){
//    handle<PsiE> {
//        val rule = typedRule
//        -when(val text = rule.text){
//            "string", "number", "boolean", "any", "void" -> "a" + text.capitalize()
//            else -> text
//        }
//    }
    handle<KtNameReferenceExpression>(
        condition = { typedRule.resolvedReferenceTarget?.let { identifierMappings.containsKey(it) } == true },
        priority = 100_000,
        action = {
            -identifierMappings[typedRule.resolvedReferenceTarget!!]!!
        }
    )
    handle<KtNameReferenceExpression>(
        condition = {
            val resolved = typedRule.resolvedReferenceTarget ?: return@handle false
            resolved is ClassDescriptor && resolved.isCompanionObject && typedRule.text == "Companion"
        },
        priority = 1013,
        action = {
            out.addImport((typedRule.resolvedReferenceTarget!!))
            -typedRule.resolvedReferenceTarget!!.containingDeclaration?.name?.identifier
            -".Companion.INSTANCE"
        }
    )
    handle<KtNameReferenceExpression>(
        condition = {
            val resolved = typedRule.resolvedReferenceTarget ?: return@handle false
            resolved is ClassDescriptor && resolved.isCompanionObject
        },
        priority = 1012,
        action = {
            out.addImport((typedRule.resolvedReferenceTarget!!))
            -typedRule.getIdentifier()
            -".Companion.INSTANCE"
        }
    )
    handle<KtNameReferenceExpression>(
        condition = {
            //Use .INSTANCE in all cases EXCEPT pointing to another type

            // Condition: I am not a receiver pointing to another type
            val parent = typedRule.parent as? KtDotQualifiedExpression
            if(parent?.receiverExpression == typedRule) {
                val next = (parent.selectorExpression as? KtQualifiedExpression)?.selectorExpression as? KtNameReferenceExpression ?: parent.selectorExpression as? KtNameReferenceExpression
                if(next?.resolvedReferenceTarget is ClassDescriptor){
                    return@handle false
                }
            }
            // Condition: I refer to a type
            val resolved = typedRule.resolvedReferenceTarget ?: return@handle false
            resolved is ClassDescriptor
                    && typedRule.resolvedUsedAsExpression == true
                    && resolved.kind != ClassKind.ENUM_ENTRY

        },
        priority = 1011,
        action = {
            out.addImport((typedRule.resolvedReferenceTarget!!))
            -typedRule.getIdentifier()
            -".INSTANCE"
        }
    )
    handle<KtNameReferenceExpression>(
        condition = {
            replacements.getGet(typedRule.resolvedReferenceTarget ?: return@handle false) != null
                    || replacements.getGet(typedRule.resolvedShortReferenceToCompanionObject ?: return@handle false) != null
        },
        priority = 10_011,
        action = {
            val rule = typedRule.resolvedReferenceTarget?.let { replacements.getGet(it) }
                ?: typedRule.resolvedShortReferenceToCompanionObject?.let { replacements.getGet(it) }!!
            emitTemplate(rule.template)
        }
    )

    handle<KtDotQualifiedExpression>(
        condition = {
            val referencedClass = ((typedRule.selectorExpression as? KtNameReferenceExpression)?.resolvedReferenceTarget as? ClassDescriptor) ?: return@handle false
            replacements.getGet(referencedClass) != null
        },
        priority = 10_012,
        action = {
            val referencedClass = ((typedRule.selectorExpression as? KtNameReferenceExpression)?.resolvedReferenceTarget as? ClassDescriptor)!!
            val rule = replacements.getGet(referencedClass)!!
            emitTemplate(rule.template, receiver = typedRule.replacementReceiverExpression)
        }
    )

    //Naked local type references
    handle<KtNameReferenceExpression>(
        condition = {
            val untypedTarget = typedRule.resolvedReferenceTarget
            val target = untypedTarget as? ClassDescriptor ?: (untypedTarget as? ConstructorDescriptor)?.constructedClass ?: return@handle false
            if((typedRule.parent as? KtQualifiedExpression)?.selectorExpression == this) return@handle false
            val context = typedRule.parentOfType<KtClassOrObject>()?.resolvedClass ?: return@handle false
            target.containingDeclaration == context
        },
        priority = 10,
        action = {
            -(typedRule.parentOfType<KtClassOrObject>()?.nameIdentifier)
            -'.'
            -typedRule.getIdentifier()
        }
    )
    handle<KtNameReferenceExpression> {
        -typedRule.getIdentifier()
        typedRule.resolvedReferenceTarget?.let { out.addImport(it) }
    }

    handle<LeafPsiElement>(
        condition = { typedRule.elementType === KtTokens.IDENTIFIER },
        priority = 1,
        action = {
            -typedRule.text.safeJsIdentifier()
        }
    )

    //smartcast
    handle<KtNameReferenceExpression>(
        condition = {
            typedRule.resolvedSmartcast != null
        },
        priority = 4000,
        action = {
            val before = (typedRule.resolvedReferenceTarget as? ValueDescriptor)?.type
            val now = typedRule.resolvedSmartcast!!.defaultType ?: (typedRule.resolvedSmartcast as? MultipleSmartCasts)?.map?.values?.firstOrNull()
            if(before?.fqNameWithTypeArgs == now?.fqNameWithTypeArgs && now?.isMarkedNullable == false){
                doSuper()
                -'!'
            } else if(now != null) {
                -'('
                doSuper()
                -" as "
                -now
                -')'
            } else {
                doSuper()
            }
        }
    )
    handle<KtDotQualifiedExpression>(
        condition = {
            typedRule.resolvedSmartcast != null && typedRule.selectorExpression is KtNameReferenceExpression
        },
        priority = 4000,
        action = {
            val sel = typedRule.selectorExpression as KtNameReferenceExpression
            val before = (sel.resolvedReferenceTarget as? ValueDescriptor)?.type
            val now = typedRule.resolvedSmartcast!!.defaultType ?: (typedRule.resolvedSmartcast as? MultipleSmartCasts)?.map?.values?.firstOrNull()
            if(before?.fqNameWithTypeArgs == now?.fqNameWithTypeArgs && now?.isMarkedNullable == false){
                doSuper()
                -'!'
            } else {
                -'('
                doSuper()
                -" as "
                -now
                -')'
            }
        }
    )
}

fun String.safeJsIdentifier(): String = when(this){
    "number",
    "string",
    "undefined",
    "abstract",
    "arguments",
    "await",
    "boolean",
    "break",
    "byte",
    "case",
    "catch",
    "char",
    "class",
    "const",
    "continue",
    "debugger",
    "default",
    "delete",
    "do",
    "double",
    "else",
    "enum*",
    "eval",
    "export",
    "extends",
    "false",
    "final",
    "finally",
    "float",
    "for",
    "function",
    "goto",
    "if",
    "implements",
    "import",
    "in",
    "instanceof",
    "int",
    "interface",
    "let",
    "long",
    "native",
    "new",
    "null",
    "package",
    "private",
    "protected",
    "public",
    "return",
    "short",
    "static",
    "super",
    "switch",
    "synchronized",
    "this",
    "throw",
    "throws",
    "transient",
    "true",
    "try",
    "typeof",
    "var",
    "void",
    "volatile",
    "while",
    "with",
    "yield" -> "_" + this
    "ImageBitmap" -> "ImageImageBitmap"
    else -> this
}
/*
 */