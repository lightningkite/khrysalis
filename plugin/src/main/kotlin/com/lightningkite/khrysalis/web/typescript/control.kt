package com.lightningkite.khrysalis.web.typescript

import com.lightningkite.khrysalis.generic.addReturn
import com.lightningkite.khrysalis.generic.set
import com.lightningkite.khrysalis.ios.swift.TabWriter
import com.lightningkite.khrysalis.ios.swift.oneOnly
import com.lightningkite.khrysalis.ios.swift.parentOfType
import com.lightningkite.khrysalis.ios.swift.usedAsStatement
import com.lightningkite.khrysalis.utils.forEachBetween
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNode
import org.antlr.v4.runtime.tree.TerminalNodeImpl
import org.jetbrains.kotlin.KotlinParser
import org.jetbrains.kotlin.KotlinParser.*
import java.lang.IllegalArgumentException

fun TypescriptTranslator.registerControl() {
    handle<ForStatementContext> {
        -"for (const "
        -rule.variableDeclaration()!!
        -" of "
        -rule.expression()
        -") "
        -rule.controlStructureBody()
    }

    handle<BlockContext> {
        -"{\n"
        rule.statements().statement().forEachBetween(
            forItem = { -it },
            between = { -";\n" }
        )
        -"\n}"
    }

    handle<WhileStatementContext> {
        -"while ("
        -rule.expression()
        -") "
        -rule.controlStructureBody()
    }

    handle<IfExpressionContext>(
        condition = { rule.usedAsStatement() },
        priority = 100
    ) {
        -"if ("
        -rule.expression()
        -")"
        -rule.controlStructureBody(0)
        rule.controlStructureBody(1)?.let {
            -" else "
            -it
        }
    }

    val normalWhen = handle<WhenExpressionContext>(
        condition = {
            rule.usedAsStatement() && rule.whenSubject() != null &&
                    rule.whenEntry()
                        .all { it.ELSE() != null || it.whenCondition().all { it.expression() != null } }
        },
        priority = 100
    ) {
        rule.whenSubject().variableDeclaration()?.let {
            -it
            -" = "
            -rule.whenSubject().expression()
            -";\n"
            -"switch("
            -it.simpleIdentifier()
            -") {\n"
        } ?: run {
            -"switch("
            -rule.whenSubject().expression()
            -") {\n"
        }
        for (entry in rule.whenEntry()) {
            entry.ELSE()?.let {
                -"default:\n"
                entry.controlStructureBody()?.let {
                    it.block()?.let {
                        it.statements()?.statement()?.forEach {
                            -it
                            -";\n"
                        }
                    } ?: it.statement()?.let {
                        -it
                        -";\n"
                    }
                }
                -"break;\n"
            } ?: entry.whenCondition().let {
                for (cond in it) {
                    -"case "
                    -cond.expression()
                    -":\n"
                }
                entry.controlStructureBody()?.let {
                    it.block()?.let {
                        it.statements()?.statement()?.forEach {
                            -it
                            -";\n"
                        }
                    } ?: it.statement()?.let {
                        -it
                        -";\n"
                    }
                }
                -"break;\n"
            }
        }
        -"}"
    }

    handle<WhenExpressionContext>(
        condition = {
            rule.whenSubject() != null &&
                    rule.whenEntry()
                        .all { it.ELSE() != null || it.whenCondition().all { it.expression() != null } }
        },
        priority = 50
    ) {
        -"() => {\n"
        this.childContext(rule.apply {
            this.parent = this.parentOfType<StatementContext>() ?: this.parentOfType<KotlinFileContext>()
            this.whenEntry().forEach {
                it.controlStructureBody().statement()?.let { it ->
                    val original = it.expression()
                    it.set(0, original.addReturn(it))
                } ?: it.controlStructureBody().block()?.let {
                    it.statements().statement().last().let {
                        it.set(0, it.expression().addReturn(it))
                    }
                }
            }
        }) {
            normalWhen.action(this)
        }
        -"\n}"
    }
}


/*package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.swift.TabWriter
import com.lightningkite.khrysalis.swift.oneOnly
import com.lightningkite.khrysalis.swift.usedAsStatement
import com.lightningkite.khrysalis.utils.forEachBetween
import org.jetbrains.kotlin.KotlinParser
import java.lang.IllegalArgumentException

fun TypescriptAltListener.registerControl() {
//    var suppressSemi = false
    fun TabWriter.writeBlock(item: KotlinParser.ControlStructureBodyContext) {
        item.block()?.statements()?.statement()?.forEach {
            startLine()
            write(it)
        }
        item.statement()?.let {
            startLine()
            write(it)
        }
    }

    fun TabWriter.writeBlockReturns(item: KotlinParser.ControlStructureBodyContext) {
        item.block()?.statements()?.statement()?.let {
            if (it.size > 1) {
                for (item in it.subList(0, it.size - 1)) {
                    startLine()
                    write(item)
                }
            }
            startLine()
            write(it.last())
        }
        item.statement()?.let {
            startLine()
            direct.append("return ")
            write(it)
        }
    }

    fun TabWriter.writeBlock(item: KotlinParser.ControlStructureBodyContext, returns: Boolean) {
        if (returns) writeBlockReturns(item)
        else writeBlock(item)
    }

    fun TabWriter.writeInsideIf(expression: KotlinParser.EqualityContext) {
        write(expression)
    }
    handle<KotlinParser.IfExpressionContext> { item ->
        val isStatement = item.usedAsStatement()
//        if(isStatement) suppressSemi = true
        if (!isStatement) {
            direct.append("{")
        }
        var current = item
        while (true) {
            direct.append("if (")
            current.expression()?.disjunction()?.conjunction()?.oneOnly()?.let {
                it.equality().forEachBetween(
                    forItem = {
                        writeInsideIf(it)
                    },
                    between = { direct.append(", ") }
                )
            } ?: run {
                write(current.expression())
            }
            direct.append(") {")
            tab {
                current.controlStructureBody(0)?.let {
                    writeBlock(it, !isStatement)
                }
            }
            startLine()
            direct.append("}")

            if (current.ELSE() != null) {
                direct.append(" else ")
                val nextIf = current.controlStructureBody(1)
                    ?.statement()
                    ?.expression()
                    ?.disjunction()
                    ?.conjunction()?.oneOnly()
                    ?.equality()?.oneOnly()
                    ?.comparison()?.oneOnly()
                    ?.infixOperation()?.oneOnly()
                    ?.elvisExpression()?.oneOnly()
                    ?.infixFunctionCall()?.oneOnly()
                    ?.rangeExpression()?.oneOnly()
                    ?.additiveExpression()?.oneOnly()
                    ?.multiplicativeExpression()?.oneOnly()
                    ?.asExpression()?.oneOnly()
                    ?.prefixUnaryExpression()
                    ?.postfixUnaryExpression()
                    ?.primaryExpression()
                    ?.ifExpression()
                if (nextIf != null) {
                    current = nextIf
                } else {
                    direct.append("{")
                    tab {
                        current.controlStructureBody(1)?.let {
                            writeBlock(it, !isStatement)
                        }
                    }
                    startLine()
                    direct.append("}")
                    break
                }
            } else {
                break
            }
        }
        if (!isStatement) {
            direct.append("}()")
        }
    }
    handle<KotlinParser.ForStatementContext> {
        line {
            append("for (const ")
            it.variableDeclaration()?.let {
                write(it)
            } ?: it.multiVariableDeclaration()?.variableDeclaration()?.let { throw IllegalArgumentException("For loops with destructuring not supported in TypeScript") }
            append(" of ")
            write(it.expression())
            append(") {")
        }
        tab {
            write(it.controlStructureBody())
        }
        line("}")
    }
    handle<KotlinParser.WhenExpressionContext> { item ->
        val usedAsStatement = item.usedAsStatement()
//        if(usedAsStatement) suppressSemi = true

        fun writeCaseStatements(controlStructureBodyContext: KotlinParser.ControlStructureBodyContext) {
            controlStructureBodyContext.block()?.statements()?.statement()?.let {
                it.forEach {
                    startLine()
                    write(it)
                }
                line("break")
            }
            controlStructureBodyContext.statement()?.let {
                direct.append(" ")
                write(it)
            }
        }

        item.whenSubject()?.expression()?.let { on ->
            direct.append("switch (")
            write(on)
            direct.append(") {")
            item.whenEntry().forEach {
                if (it.ELSE() != null) {
                    startLine()
                    direct.append("default:")
                    writeCaseStatements(it.controlStructureBody())
                } else {
                    val conditions = it.whenCondition()!!
                    if (conditions.size == 1) {
                        val c = conditions.first()
                        c.expression()?.let { expr ->
                            startLine()
                            direct.append("case ")
                            write(expr)
                            direct.append(":")
                            writeCaseStatements(it.controlStructureBody())
                        }
                        c.rangeTest()?.let { r ->
                            startLine()
                            direct.append("case ")
                            write(r.expression())
                            direct.append(":")
                            writeCaseStatements(it.controlStructureBody())
                        }
                        c.typeTest()?.let { t ->
                            startLine()
                            if(on.text.isNotBlank() && on.text.all { it.isJavaIdentifierPart() }) {
                                direct.append("case let ${on.text} as ")
                            } else {
                                direct.append("case is ")
                            }
                            write(t.type())
                            direct.append(":")
                            writeCaseStatements(it.controlStructureBody())
                        }
                    } else {
                        conditions.forEachBetween(
                            forItem = { c ->
                                c.expression()?.let { expr ->
                                    startLine()
                                    direct.append("case ")
                                    write(expr)
                                    direct.append(":")
                                }
                                c.rangeTest()?.let { r ->
                                    startLine()
                                    direct.append("case ")
                                    write(r.expression())
                                    direct.append(":")
                                }
                                c.typeTest()?.let {
                                    startLine()
                                    direct.append("case is ")
                                    write(it.type())
                                    direct.append(":")
                                }
                            },
                            between = {
                            }
                        )
                        writeCaseStatements(it.controlStructureBody())
                    }
                }
            }
            if(item.whenEntry().none { it.ELSE() != null }) {
                //Swift requires an else no matter what; create an empty one here
                line("default: break;")
            }
            startLine()
            direct.append("}")
        } ?: run {
            item.whenEntry().forEachBetween(
                forItem = {
                    if(it.ELSE() == null) {
                        direct.append("if (")
                        it.whenCondition().forEachBetween(
                            forItem = { cond ->
                                write(cond.expression()!!)
                            },
                            between = { direct.append(" || ") }
                        )
                    }
                    direct.append(") {")
                    tab {
                        startLine()
                        write(it.controlStructureBody())
                    }
                    startLine()
                    direct.append("}")
                },
                between = {
                    direct.append(" else ")
                }
            )
        }

    }
    handle<KotlinParser.ControlStructureBodyContext> { item ->
        item.block()?.statements()?.statement()?.forEach {
            startLine()
            write(it)
        }
        item.statement()?.let {
            startLine()
            write(it)
        }
    }
    handle<KotlinParser.WhileStatementContext> { item ->
        line {
            append("while (")
            write(item.expression())
            append(") {")
        }
        tab {
            startLine()
            write(item.controlStructureBody())
        }
        line("}")
    }

    handle<KotlinParser.StatementContext> {
        it.expression()?.let {
            line {
                write(it)
                append(';')
            }
        } ?: it.assignment()?.let {
            line {
                write(it)
                append(';')
            }
        } ?: it.loopStatement()?.let {
            write(it)
        } ?: it.declaration()?.let {
            line {
                write(it)
                append(';')
            }
        }
    }
}
*/
