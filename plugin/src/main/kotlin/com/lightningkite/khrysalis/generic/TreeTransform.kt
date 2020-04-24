package com.lightningkite.khrysalis.generic

import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.tree.TerminalNode
import org.antlr.v4.runtime.tree.TerminalNodeImpl
import org.jetbrains.kotlin.KotlinParser


inline fun <reified T : ParserRuleContext> ParserRuleContext.addChild(configure: T.() -> Unit = {}): T {
    val newElement = T::class.java.getConstructor(ParserRuleContext::class.java, Int::class.java).newInstance(this, 0)
        .apply(configure)
    addChild(
        newElement
    )
    return newElement
}

fun TerminalNode.copy(): TerminalNode = TerminalNodeImpl(this.symbol)
fun <T : ParserRuleContext> T.copy(under: ParserRuleContext? = null): T {
    return this::class.java.getConstructor(ParserRuleContext::class.java, Int::class.java).newInstance(under, this.invokingState)
        .apply {
            this@copy.children?.let {
                this.children = ArrayList()
                it.forEach { child ->
                    when (child) {
                        is ParserRuleContext -> this.addChild(child.copy(this))
                        is TerminalNode -> this.addChild(child.copy())
                    }
                }
            }
        }
}

inline fun <reified This : ParserRuleContext, reified T : ParserRuleContext> This.set(index: Int, setTo: T): This {
    var current = 0
    for (childIndex in this.children.indices) {
        if (children[childIndex] is T) {
            if (current == index) {
                this.children[childIndex] = setTo
                break
            }
            current++
        }
    }
    return this
}

class VirtualToken(val typeRaw: Int, val textRaw: String) : Token {
    override fun getTokenSource(): TokenSource? = null
    override fun getType(): Int = typeRaw
    override fun getStopIndex(): Int = -1
    override fun getText(): String = textRaw
    override fun getChannel(): Int = -1
    override fun getTokenIndex(): Int = -1
    override fun getCharPositionInLine(): Int = -1
    override fun getStartIndex(): Int = -1
    override fun getLine(): Int = -1
    override fun getInputStream(): CharStream = ANTLRInputStream(text)
}

fun KotlinParser.ExpressionContext.addReturn(parent: ParserRuleContext) = KotlinParser.ExpressionContext(parent, 0).apply {
    addChild<KotlinParser.DisjunctionContext>()
        .addChild<KotlinParser.ConjunctionContext>()
        .addChild<KotlinParser.EqualityContext>()
        .addChild<KotlinParser.ComparisonContext>()
        .addChild<KotlinParser.InfixOperationContext>()
        .addChild<KotlinParser.ElvisExpressionContext>()
        .addChild<KotlinParser.InfixFunctionCallContext>()
        .addChild<KotlinParser.RangeExpressionContext>()
        .addChild<KotlinParser.AdditiveExpressionContext>()
        .addChild<KotlinParser.MultiplicativeExpressionContext>()
        .addChild<KotlinParser.AsExpressionContext>()
        .addChild<KotlinParser.PrefixUnaryExpressionContext>()
        .addChild<KotlinParser.PostfixUnaryExpressionContext>()
        .addChild<KotlinParser.PrimaryExpressionContext>()
        .addChild<KotlinParser.JumpExpressionContext> {
            addChild(
                VirtualToken(
                    KotlinParser.RETURN,
                    "return"
                )
            )
            this@addReturn.parent = this
            addChild(this@addReturn)
        }
}
