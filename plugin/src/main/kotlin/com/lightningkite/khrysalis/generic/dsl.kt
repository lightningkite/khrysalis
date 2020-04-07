package com.lightningkite.khrysalis.generic

import com.lightningkite.khrysalis.ios.swift.oneOnly
import com.lightningkite.khrysalis.ios.swift.parentOfType
import com.lightningkite.khrysalis.utils.forEachBetween
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNode
import org.jetbrains.kotlin.KotlinLexer
import org.jetbrains.kotlin.KotlinParser.*

typealias TranslationalCondition<T> = TranslatorContext<T>.() -> Boolean

inline fun <reified T : ParseTree, reified BOUNDARY : ParseTree> ParserRuleContext.parentOfType(boundaryCountMax: Int = 0): T? {
    var current = this
    var boundaryCount = 0
    while (true) {
        current = this.parent as? ParserRuleContext ?: return null
        if (current is BOUNDARY) {
            boundaryCount++
            if (boundaryCount > boundaryCountMax) {
                return null
            }
        } else if (current is T) {
            return current
        }
    }
}

inline fun <reified T : ParseTree> ParserRuleContext.just(): T? {
    var current: ParseTree = this
    while (true) {
        if (this.childCount != 1) return null
        current = this.children[0]
        if (current is T) {
            return current
        }
    }
}

fun ParserRuleContext.just(type: Int): TerminalNode? {
    var current: ParseTree = this
    while (true) {
        if (this.childCount != 1) return null
        current = this.children[0]
        if (current is TerminalNode && current.symbol.type == type) {
            return current
        }
    }
}

fun ConjunctionContext.isIfDirect() =
    this.parentOfType<IfExpressionContext>()?.expression()?.just<ConjunctionContext>() sameAs this

infix fun ParseTree?.sameAs(other: ParseTree?) =
    this != null && other != null && (this == other || this.sourceInterval.a == other.sourceInterval.a && this.sourceInterval.b == other.sourceInterval.b)

//fun test() {
//    with(Translator(SourceLanguage.kotlin)) {
//        handle<IfExpressionContext> {
//            -"if "
//            -rule.expression()
//            -rule.controlStructureBody(0)
//            rule.controlStructureBody(1)?.let {
//                -" else "
//                -it
//            }
//        }
//        handle<ConjunctionContext>(
//            condition = { rule.isIfDirect() },
//            priority = 10,
//            action = {
//                rule.equality().forEachBetween(
//                    forItem = { -it },
//                    between = { -", " }
//                )
//            }
//        )
//
//        handle<InfixOperationContext>(
//            condition = {
//                rule.parentOfType<ConjunctionContext>()?.isIfDirect() == true &&
//                        rule.isOperator() != null &&
//                        rule.elvisExpression().size == 2 &&
//                        rule.elvisExpression(0).just<SimpleIdentifierContext>() != null
//            },
//            priority = 12,
//            action = {
//                -"let "
//                -rule.elvisExpression(0)
//                -" = "
//                -rule.elvisExpression(0)
//                -" as? "
//                -rule.elvisExpression(1)
//            }
//        )
//
//        handle<EqualityContext>(
//            condition = {
//                rule.parentOfType<ConjunctionContext>()?.isIfDirect() == true &&
//                        rule.equalityOperator()?.oneOnly()?.EXCL_EQ() != null &&
//                        rule.comparison(1).just(KotlinLexer.NullLiteral) != null
//            },
//            priority = 12,
//            action = {
//                -"let "
//                -rule.comparison(0)
//                -" = "
//                -rule.comparison(0)
//            }
//        )
//    }
//}

/*

handle<SomethingContext> {
    +"Thing"
    +"Another THing"
    +part()
    +part().part()
    part().forEach {
        +part()
    }
}

handle<SomeContext>(
    condition = { part() != null },
    priority = 20,
    action = {
        +part()
    }
)



 */
