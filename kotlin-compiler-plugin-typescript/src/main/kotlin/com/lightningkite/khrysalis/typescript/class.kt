package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.typescript.replacements.TemplatePart
import com.lightningkite.khrysalis.util.allOverridden
import com.lightningkite.khrysalis.util.forEachBetween
import com.lightningkite.khrysalis.util.recursiveChildren
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.codegen.isDefinitelyNotDefaultImplsMethod
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.js.translate.callTranslator.getReturnType
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.*
import org.jetbrains.kotlin.psi2ir.findFirstFunction
import org.jetbrains.kotlin.resolve.calls.tower.isSynthesized
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperInterfaces
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter
import org.jetbrains.kotlin.types.typeUtil.supertypes
import java.util.*
import kotlin.collections.ArrayList


val skippedExtensions = setOf(
    "com.lightningkite.khrysalis.AnyObject",
    "com.lightningkite.khrysalis.AnyHashable",
    "com.lightningkite.khrysalis.Hashable",
    "com.lightningkite.khrysalis.Equatable"
//    "com.lightningkite.khrysalis.SomeEnum"
)

fun MemberDescriptor.description(): String {
    return when (this) {
        is FunctionDescriptor -> this.name.identifier + this.valueParameters.joinToString {
            it.type.getJetTypeFqName(
                true
            )
        }
        is PropertyDescriptor -> this.name.identifier
        else -> return "???"
    }
}

fun TypescriptTranslator.registerClass() {

    handle<KtClass>(
        condition = { typedRule.isInterface() },
        priority = 100
    ) {
        -"interface "
        -typedRule.nameIdentifier
        -typedRule.typeParameterList
        -" {\n"
        -typedRule.body
        -"}"
        typedRule.runPostActions()
    }

    handle<KtClass> {
        -"class "
        -typedRule.nameIdentifier
        -typedRule.typeParameterList
        typedRule.superTypeListEntries.mapNotNull { it as? KtSuperTypeCallEntry }.takeUnless { it.isEmpty() }?.let {
            -" extends "
            it.forEachBetween(
                forItem = { -it.resolvedCall?.getReturnType() },
                between = { -", " }
            )
        }
        typedRule.superTypeListEntries.mapNotNull { it as? KtSuperTypeEntry }
            .filter { it.typeReference?.resolvedType?.getJetTypeFqName(false) !in skippedExtensions }
            .takeUnless { it.isEmpty() }?.let {
                -" implements "
                it.forEachBetween(
                    forItem = { -it.typeReference },
                    between = { -", " }
                )
            }
        -" {\n"
        typedRule.superTypeListEntries
            .mapNotNull { it as? KtSuperTypeEntry }
            .filter { it.typeReference?.resolvedType?.getJetTypeFqName(false) !in skippedExtensions }
            .mapNotNull { it.typeReference?.resolvedType }
            .flatMap { listOf(it) + it.supertypes() }
            .map { it.getJetTypeFqName(false) }
            .filter { it != "kotlin.Any" }
            .distinct()
            .takeUnless { it.isEmpty() }
            ?.forEach {
                -"implementsInterface"
                -it.split('.').joinToString("") { it.capitalize() }
                -" = true;\n"
            }
        typedRule.primaryConstructor?.valueParameters?.filter { it.hasValOrVar() }?.forEach {
            -it.visibilityModifierTypeOrDefault().toVisibility()
            -" "
            if ((it.valOrVarKeyword as? LeafPsiElement)?.elementType == KtTokens.VAL_KEYWORD) {
                -"readonly "
            }
            -it.nameIdentifier
            it.typeReference?.let {
                -": "
                -it
            }
            -";\n"
        }
        typedRule.primaryConstructor?.let { cons ->
            -"constructor("
            cons.valueParameters.forEachBetween(
                forItem = { -it },
                between = { -", " }
            )
            -") {\n"
            typedRule.superTypeListEntries.mapNotNull { it as? KtSuperTypeCallEntry }.takeUnless { it.isEmpty() }
                ?.firstOrNull()?.let {
                -"super"
                -it.valueArgumentList
                -";"
            }
            //Parameter assignment first
            cons.valueParameters.asSequence().filter { it.hasValOrVar() }.forEach {
                -"this."
                -it.nameIdentifier
                -" = "
                -it.nameIdentifier
                -";\n"
            }
            //Then, in order, variable initializers and anon initializers
            typedRule.body?.children?.forEach {
                when (it) {
                    is KtProperty -> {
                        it.initializer?.let { init ->
                            -"this."
                            -it.nameIdentifier
                            -" = "
                            -init
                            -";\n"
                        }
                    }
                    is KtAnonymousInitializer -> {
                        val b = it.body
                        if (b is KtBlockExpression) {
                            b.statements.forEach {
                                -it
                                -";\n"
                            }
                        } else {
                            -b
                            -";\n"
                        }
                    }
                }
            }
            -"}\n"
        } ?: run {
            typedRule.superTypeListEntries.mapNotNull { it as? KtSuperTypeCallEntry }.takeUnless { it.isEmpty() }
                ?.firstOrNull()?.let {
                -"constructor() { super"
                -it.valueArgumentList
                -"; }"
            }
        }

        if (typedRule.isData()) {
            //Generate hashCode() if not present
            if (typedRule.body?.declarations?.any { it is FunctionDescriptor && (it as KtDeclaration).name == "hashCode" && it.valueParameters.isEmpty() } != true) {
                -"hashCode(): number {\nlet hash = 17;\n"
                typedRule.primaryConstructor?.valueParameters?.filter { it.hasValOrVar() }?.forEach { param ->
                    val type = param.typeReference?.resolvedType?.getJetTypeFqName(false)
                        ?: throw IllegalArgumentException("No type reference available to generate hashCode() function")
                    replacements.functions[type + ".hashCode"]?.firstOrNull()?.let {
                        -"hash = 31 * hash + "
                        for (part in it.template.parts) {
                            when (part) {
                                is TemplatePart.Import -> out.addImport(part)
                                is TemplatePart.Text -> -part.string
                                TemplatePart.Receiver,
                                TemplatePart.DispatchReceiver,
                                TemplatePart.ExtensionReceiver -> {
                                    -"this."; -param.nameIdentifier
                                }
                                else -> {
                                }
                            }
                        }
                    } ?: run {
                        -"hash = 31 * hash + this."
                        -param.nameIdentifier
                        -".hashCode()"
                    }
                    -";\n"
                }

                -"return hash;\n}\n"
            }

            //Generate equals() if not present
            if (typedRule.body?.declarations?.any { it is FunctionDescriptor && (it as KtDeclaration).name == "equals" && it.valueParameters.size == 1 } != true) {
                -"equals(other: any): boolean { return other instanceof "
                -typedRule.nameIdentifier
                typedRule.primaryConstructor?.valueParameters?.filter { it.hasValOrVar() }?.forEach { param ->
                    -" && "
                    val type = param.typeReference?.resolvedType?.getJetTypeFqName(false)
                        ?: throw IllegalArgumentException("No type reference available to generate hashCode() function")
                    replacements.functions[type + ".equals"]?.firstOrNull()?.let {
                        for (part in it.template.parts) {
                            when (part) {
                                is TemplatePart.Import -> out.addImport(part)
                                is TemplatePart.Text -> -part.string
                                TemplatePart.Receiver,
                                TemplatePart.DispatchReceiver,
                                TemplatePart.ExtensionReceiver -> {
                                    -"this."
                                    -param.nameIdentifier
                                }
                                TemplatePart.AllParameters,
                                is TemplatePart.Parameter,
                                is TemplatePart.ParameterByIndex -> {
                                    -"other."
                                    -param.nameIdentifier
                                }
                                else -> {
                                }
                            }
                        }
                    } ?: run {
                        -"this."
                        -param.nameIdentifier
                        -".equals("
                        -"other."
                        -param.nameIdentifier
                        -")"
                    }
                }
                -" }\n"
            }

            //Generate toString() if not present
            if (typedRule.body?.declarations?.any { it is FunctionDescriptor && (it as KtDeclaration).name == "toString" && it.valueParameters.isEmpty() } != true) {
                -"toString(): string { return "
                -'`'
                -typedRule.nameIdentifier
                -'('
                typedRule.primaryConstructor?.valueParameters?.filter { it.hasValOrVar() }?.forEachBetween(
                    forItem = {
                        -it.nameIdentifier
                        -" = \${this."
                        -it.nameIdentifier
                        -"}"
                    },
                    between = { -", " }
                )
                -')'
                -'`'
                -" }\n"
            }
        }

        -typedRule.body

        typedRule.resolvedClass
            ?.unsubstitutedMemberScope?.getContributedDescriptors(DescriptorKindFilter.CALLABLES) { true }?.asSequence()
            ?.mapNotNull { it as? MemberDescriptor }
            ?.filter { it.source == SourceElement.NO_SOURCE }
            ?.filter {
                when (it) {
                    is PropertyDescriptor -> it.allOverridden()
                        .all { (it.containingDeclaration as? ClassDescriptor)?.kind == ClassKind.INTERFACE }
                    is FunctionDescriptor -> it.allOverridden()
                        .all { (it.containingDeclaration as? ClassDescriptor)?.kind == ClassKind.INTERFACE }
                    else -> false
                }
            }
            ?.forEach {
                when (it) {
                    is PropertyDescriptor -> {
                        -"public get "
                        -it.name.asString()
                        it.typeParameters.takeUnless { it.isEmpty() }?.let {
                            -'<'
                            it.forEachBetween(
                                forItem = { -it.name.asString() /*TODO: Type Argument Limits*/ },
                                between = { -", " }
                            )
                            -'>'
                        }
                        -"(): "
                        -it.type
                        -" { return "
                        -it.allOverridden().first().tsFunctionGetDefaultName
                        -"(this); }\n"

                        if (it.isVar) {
                            -"public set "
                            -it.name.asString()
                            it.typeParameters.takeUnless { it.isEmpty() }?.let {
                                -'<'
                                it.forEachBetween(
                                    forItem = { -it.name.asString() /*TODO: Type Argument Limits*/ },
                                    between = { -", " }
                                )
                                -'>'
                            }
                            -"(value: "
                            -it.type
                            -") { "
                            -it.allOverridden().first().tsFunctionGetDefaultName
                            -"(this, value); }\n"
                        }
                    }
                    is FunctionDescriptor -> {
                        -"public "
                        -it.name.asString()
                        it.typeParameters.takeUnless { it.isEmpty() }?.let {
                            -'<'
                            it.forEachBetween(
                                forItem = { -it.name.asString() /*TODO: Type Argument Limits*/ },
                                between = { -", " }
                            )
                            -'>'
                        }
                        -"("
                        it.valueParameters.forEachBetween(
                            forItem = {
                                -it.name.asString()
                                -": "
                                -it.type
                            },
                            between = { -", " }
                        )
                        -"): "
                        -(it.returnType ?: "void")
                        -" { return "
                        -it.allOverridden().first().tsDefaultName
                        -"(this"
                        it.valueParameters.forEach {
                            -", "
                            -it.name.asString()
                        }
                        -"); }\n"
                    }
                    else -> -"// Insert default for $it\n"
                }
            }

        -"}"
        typedRule.runPostActions()
    }

    handle<KtClassBody> {
        typedRule.allChildren.toList().drop(1).dropLast(1).forEach {
            -it
        }
    }

    handle<KtClassInitializer> { /*skip*/ }

//    handle<KotlinParser.ClassDeclarationContext>(
//        condition = { typedRule.INTERFACE() != null },
//        priority = 100
//    ) {
//        -"interface "
//        -typedRule.simpleIdentifier()
//        -typedRule.typeParameters()
//        -" "
//        typedRule.delegationSpecifiers()?.annotatedDelegationSpecifier()?.asSequence()
//            ?.mapNotNull { it.delegationSpecifier()?.userType() }
//            ?.filter { it.text in skippedExtensions }
//            ?.takeUnless { it.none() }
//            ?.let {
//                -"extends "
//                it.forEachBetween(
//                    forItem = { -it },
//                    between = { -", " }
//                )
//                -" "
//            }
//        -"{\n"
//
//        -"readonly implementsInterface"
//        -typedRule.simpleIdentifier()
//        -": boolean;\n"
//
//        typedRule.classBody()?.classMemberDeclarations()?.classMemberDeclaration()?.forEach {
//            -it
//        }
//
//        -"}\n"
//    }
//
//    handle<KotlinParser.ClassDeclarationContext> {
//        -"class "
//        -typedRule.simpleIdentifier()
//        -typedRule.typeParameters()
//        -" "
//        var extendedOne: KotlinParser.ConstructorInvocationContext? = null
//        typedRule.delegationSpecifiers()?.annotatedDelegationSpecifier()?.let {
//            extendedOne = it.asSequence().mapNotNull {
//                it.delegationSpecifier()?.constructorInvocation()
//            }.firstOrNull()
//            extendedOne?.let {
//                -"extends "
//                -it.userType()
//                -" "
//            }
//            it.asSequence()
//                .mapNotNull { it.delegationSpecifier()?.userType() }
//                .filter { it.text in skippedExtensions }
//                .takeUnless { it.none() }
//                ?.let {
//                    -"implements "
//                    it.forEachBetween(
//                        forItem = { -it },
//                        between = { -", " }
//                    )
//                    -" "
//                }
//        }
//        -"{\n"
//
//        typedRule.delegationSpecifiers()?.annotatedDelegationSpecifier()
//            ?.mapNotNull { it.delegationSpecifier()?.userType() }
//            ?.filter { it.text in skippedExtensions }
//            ?.forEach {
//                -"implementsInterface"
//                -it.translatedTextWithoutArguments(this).replace(".", "")
//                -": boolean = true;\n"
//
////                resolve(currentFile, it.withoutArgumentText()).firstOrNull()?.let { interfaceData ->
////                    //write missing declarations with defaults
////                    typedRule.classBody()
////                        ?.classMemberDeclarations()
////                        ?.classMemberDeclaration()
////                        ?.mapNotNull { it.declaration()?.functionDeclaration() }
////                        ?.filter {
////                            it.simpleIdentifier() !in
////                        }
////                }
//            }
//
//        typedRule.primaryConstructor()?.classParameters()?.classParameter()?.forEach {
//            if (it.VAL() != null || it.VAR() != null) {
//                -it.simpleIdentifier()
//                -": "
//                -it.type()
//                -";\n"
//            }
//        }
//        -"\n"
//        typedRule.primaryConstructor()?.modifiers()?.visibility()?.let {
//            -it.name.toLowerCase()
//        } ?: run {
//            -"public"
//        }
//        -" constructor("
//        typedRule.primaryConstructor()?.classParameters()?.classParameter()?.forEachBetween(
//            forItem = {
//                -it.simpleIdentifier()
//                -": "
//                -it.type()
//                it.expression()?.let {
//                    -" = "
//                    -it
//                }
//            },
//            between = { -", " }
//        )
//        -") {\n"
//        extendedOne?.let {
//            -"super"
//            -it.valueArguments()
//            -";\n"
//        }
//        typedRule.primaryConstructor()?.classParameters()?.classParameter()?.forEach {
//            if (it.VAL() != null || it.VAR() != null) {
//                -"this."
//                -it.simpleIdentifier()
//                -" = "
//                -it.simpleIdentifier()
//                -";\n"
//            }
//        }
//        typedRule.classBody()?.classMemberDeclarations()?.classMemberDeclaration()?.forEach {
//            -it.anonymousInitializer()?.block()?.statements()
//        }
//        -"}\n"
//
//        //TODO: Copy support?
////        if(typedRule.modifiers()?.modifier()?.any { it.classModifier()?.DATA() != null } == true) {
////            -"public copy("
////            -")"
////        }
//
//        -"\n"
//        typedRule.classBody()?.classMemberDeclarations()?.classMemberDeclaration()?.forEach {
//            -it
//        }
//
//        -"}\n"
//    }
//
//    handle<KotlinParser.TypeParametersContext> {
//        -"<"
//        typedRule.typeParameter().forEachBetween(
//            forItem = {
//                -(it.simpleIdentifier())
//                it.type()?.let {
//                    -(" extends ")
//                    -(it)
//                }
//            },
//            between = { -(", ") }
//        )
//        -">"
//    }
//
//    handle<KotlinParser.AnonymousInitializerContext> { /*suppress*/ }
//
//    handle<KotlinParser.CompanionObjectContext> {
//        typedRule.classBody()?.classMemberDeclarations()?.classMemberDeclaration()?.forEach {
//            -it.declaration()
//        }
//    }
}

private val weakKtClassPostActions = WeakHashMap<KtClass, ArrayList<() -> Unit>>()
fun KtClass.runPostActions() {
    weakKtClassPostActions.remove(this)?.forEach { it() }
}

fun KtClass.addPostAction(action: () -> Unit) {
    weakKtClassPostActions.getOrPut(this) { ArrayList() }.add(action)
}