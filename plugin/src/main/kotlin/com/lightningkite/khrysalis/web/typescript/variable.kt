package com.lightningkite.khrysalis.web.typescript

import com.lightningkite.khrysalis.generic.line
import com.lightningkite.khrysalis.generic.parentOfType
import com.lightningkite.khrysalis.ios.swift.parentIfType
import com.lightningkite.khrysalis.utils.forEachBetween
import com.lightningkite.khrysalis.web.typescript.actuals.visibility
import org.jetbrains.kotlin.KotlinParser
import java.util.concurrent.atomic.AtomicInteger

val uniqueNumber = AtomicInteger(0)

fun TypescriptTranslator.registerVariable() {

    handle<KotlinParser.PropertyDeclarationContext>(
        condition = {
            typedRule.parentIfType<KotlinParser.DeclarationContext>()
                ?.parentIfType<KotlinParser.ClassMemberDeclarationContext>() != null
        },
        priority = 100,
        action = {
            typedRule.visibility().let {
                -it.name.toLowerCase()
                -" "
            }
            -typedRule.variableDeclaration()
            -";\n"
        }
    )
    handle<KotlinParser.PropertyDeclarationContext>(
        condition = {
            typedRule.parentIfType<KotlinParser.DeclarationContext>()
                ?.parentIfType<KotlinParser.ClassMemberDeclarationContext>()
                ?.parentIfType<KotlinParser.ClassMemberDeclarationsContext>()
                ?.parentIfType<KotlinParser.ClassBodyContext>()
                ?.parentIfType<KotlinParser.CompanionObjectContext>() != null
        },
        priority = 101,
        action = {
            typedRule.visibility().let {
                -it.name.toLowerCase()
                -" "
            }
            -"static "
            -typedRule.variableDeclaration()
            typedRule.expression()?.let{
                -" = "
                -it
            }
            -";\n"
        }
    )

    handle<KotlinParser.AssignmentContext>(
        condition = {
            typedRule.directlyAssignableExpression()
                ?.assignableSuffix()
                ?.navigationSuffix()
                ?.memberAccessOperator()
                ?.safeNav() != null
        },
        priority = 10
    ) {
        val name = "tempVarSet" + uniqueNumber.getAndIncrement().toString()
        -"const "
        -name
        -" = "
        -typedRule.directlyAssignableExpression().postfixUnaryExpression()
        -";\n"

        -"if ("
        -name
        -" !== null"
        -") {\n"

        -name
        -"."
        -typedRule.directlyAssignableExpression().assignableSuffix().navigationSuffix().simpleIdentifier()
        -" "
        -(typedRule.ASSIGNMENT() ?: -typedRule.assignmentAndOperator())
        -" "
        -typedRule.expression()
        -"\n"

        -"}\n"
    }

    //OPTIONS FOR LET ??
    //val temp = y; if(temp !== null) ... else ...
    //let(y, (x)=>{}, ()=>{})
}
