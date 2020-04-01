package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.swift.parentIfType
import com.lightningkite.khrysalis.swift.registerFile
import com.lightningkite.khrysalis.utils.forEachBetween
import org.jetbrains.kotlin.KotlinParser

fun TypescriptAltListener.registerControl() {

    this.handle<KotlinParser.ForStatementContext> {
        line {
            append("for (const ")
            write(it.variableDeclaration().simpleIdentifier())
            append(" of ")
            write(it.expression())
            append(") {")
        }
        tab {
            write(it.controlStructureBody())
        }
        line("}")
    }

    handle<KotlinParser.ControlStructureBodyContext> {
        it.block()?.let {
            write(it.statements())
        } ?: it.statement()?.let {
            write(it)
        }
    }

    handle<KotlinParser.StatementsContext> {
        it.statement().forEach {
            write(it)
        }
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
