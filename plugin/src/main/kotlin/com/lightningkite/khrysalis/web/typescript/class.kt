package com.lightningkite.khrysalis.web.typescript

import com.lightningkite.khrysalis.generic.line
import com.lightningkite.khrysalis.ios.swift.parentIfType
import com.lightningkite.khrysalis.utils.forEachBetween
import com.lightningkite.khrysalis.web.typescript.actuals.Visibility
import com.lightningkite.khrysalis.web.typescript.actuals.visibility
import org.antlr.v4.runtime.ParserRuleContext
import org.jetbrains.kotlin.KotlinParser

val skippedExtensions = setOf(
    "AnyObject",
    "AnyHashable",
    "Hashable",
    "Equatable",
    "SomeEnum"
)

fun TypescriptTranslator.registerClass() {

    handle<KotlinParser.ClassDeclarationContext>(
        condition = { typedRule.INTERFACE() != null },
        priority = 100
    ) {
        -"interface "
        -typedRule.simpleIdentifier()
        -typedRule.typeParameters()
        -" "
        typedRule.delegationSpecifiers()?.annotatedDelegationSpecifier()?.asSequence()
            ?.mapNotNull { it.delegationSpecifier()?.userType() }
            ?.filter { it.text in skippedExtensions }
            ?.takeUnless { it.none() }
            ?.let {
                -"extends "
                it.forEachBetween(
                    forItem = { -it },
                    between = { -", " }
                )
                -" "
            }
        -"{\n"

        -"readonly implementsInterface"
        -typedRule.simpleIdentifier()
        -": boolean;\n"

        typedRule.classBody()?.classMemberDeclarations()?.classMemberDeclaration()?.forEach {
            -it
        }

        -"}\n"
    }

    handle<KotlinParser.ClassDeclarationContext> {
        -"class "
        -typedRule.simpleIdentifier()
        -typedRule.typeParameters()
        -" "
        var extendedOne: KotlinParser.ConstructorInvocationContext? = null
        typedRule.delegationSpecifiers()?.annotatedDelegationSpecifier()?.let {
            extendedOne = it.asSequence().mapNotNull {
                it.delegationSpecifier()?.constructorInvocation()
            }.firstOrNull()
            extendedOne?.let {
                -"extends "
                -it.userType()
                -" "
            }
            it.asSequence()
                .mapNotNull { it.delegationSpecifier()?.userType() }
                .filter { it.text in skippedExtensions }
                .takeUnless { it.none() }
                ?.let {
                    -"implements "
                    it.forEachBetween(
                        forItem = { -it },
                        between = { -", " }
                    )
                    -" "
                }
        }
        -"{\n"

        typedRule.delegationSpecifiers()?.annotatedDelegationSpecifier()
            ?.mapNotNull { it.delegationSpecifier()?.userType() }
            ?.filter { it.text in skippedExtensions }
            ?.forEach {
                -"implementsInterface"
                -it.translatedTextWithoutArguments(this).replace(".", "")
                -": boolean = true;\n"

                resolve(currentFile, it.withoutArgumentText()).firstOrNull()?.let {
                    //write missing declarations with defaults
//                    it.
                }
            }

        typedRule.primaryConstructor()?.classParameters()?.classParameter()?.forEach {
            if (it.VAL() != null || it.VAR() != null) {
                -it.simpleIdentifier()
                -": "
                -it.type()
                -";\n"
            }
        }
        -"\n"
        typedRule.primaryConstructor()?.modifiers()?.visibility()?.let {
            -it.name.toLowerCase()
        } ?: run {
            -"public"
        }
        -" constructor("
        typedRule.primaryConstructor()?.classParameters()?.classParameter()?.forEachBetween(
            forItem = {
                -it.simpleIdentifier()
                -": "
                -it.type()
                it.expression()?.let {
                    -" = "
                    -it
                }
            },
            between = { -", " }
        )
        -") {\n"
        extendedOne?.let {
            -"super"
            -it.valueArguments()
            -";\n"
        }
        typedRule.primaryConstructor()?.classParameters()?.classParameter()?.forEach {
            if (it.VAL() != null || it.VAR() != null) {
                -"this."
                -it.simpleIdentifier()
                -" = "
                -it.simpleIdentifier()
                -";\n"
            }
        }
        typedRule.classBody()?.classMemberDeclarations()?.classMemberDeclaration()?.forEach {
            -it.anonymousInitializer()?.block()?.statements()
        }
        -"}\n"

        //TODO: Copy support?
//        if(typedRule.modifiers()?.modifier()?.any { it.classModifier()?.DATA() != null } == true) {
//            -"public copy("
//            -")"
//        }

        -"\n"
        typedRule.classBody()?.classMemberDeclarations()?.classMemberDeclaration()?.forEach {
            -it
        }

        -"}\n"
    }

    handle<KotlinParser.TypeParametersContext> {
        -"<"
        typedRule.typeParameter().forEachBetween(
            forItem = {
                -(it.simpleIdentifier())
                it.type()?.let {
                    -(" extends ")
                    -(it)
                }
            },
            between = { -(", ") }
        )
        -">"
    }

    handle<KotlinParser.AnonymousInitializerContext> { /*suppress*/ }

    handle<KotlinParser.CompanionObjectContext> {
        typedRule.classBody()?.classMemberDeclarations()?.classMemberDeclaration()?.forEach {
            -it.declaration()
        }
    }
}
