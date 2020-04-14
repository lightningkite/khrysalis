package com.lightningkite.khrysalis.web.typescript

import com.lightningkite.khrysalis.generic.line
import com.lightningkite.khrysalis.ios.swift.parentIfType
import com.lightningkite.khrysalis.utils.forEachBetween
import com.lightningkite.khrysalis.web.typescript.actuals.Visibility
import com.lightningkite.khrysalis.web.typescript.actuals.visibility
import org.antlr.v4.runtime.ParserRuleContext
import org.jetbrains.kotlin.KotlinParser

fun TypescriptTranslator.registerClass() {
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
        typedRule.primaryConstructor()?.classParameters()?.classParameter()?.forEach {
            if(it.VAL() != null || it.VAR() != null) {
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
            if(it.VAL() != null || it.VAR() != null) {
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

    handle<KotlinParser.AnonymousInitializerContext> { /*suppress*/ }
}
