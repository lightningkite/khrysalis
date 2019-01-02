package com.lightningkite.kwift

import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.misc.Interval
import org.antlr.v4.runtime.tree.TerminalNode
import org.jetbrains.kotlin.KotlinParser
import org.jetbrains.kotlin.KotlinParserBaseListener

open class RewriteListener(val tokenStream: CommonTokenStream, val parser: KotlinParser) : KotlinParserBaseListener() {

    data class Section(var text: String, var spacingBefore: String = "", var rule: Int = 0){
        fun toOutputString(): String = spacingBefore + text
    }

    val ruleNames = parser.ruleNames
    val layers = ArrayList<ArrayList<Section>>().apply {
        add(ArrayList())
    }
    val terminalRewrites = HashMap<Int, (String) -> String>()

    var overridden: Section? = null
    val default: Section get() = layers.last().joinClean()

    fun get(rule: Int): Section? = layers.last().find { it.rule == rule }
    fun gets(rule: Int): List<Section> = layers.last().filter { it.rule == rule }

    fun text(rule: Int): String? = get(rule)?.text
    fun texts(rule: Int): List<String> = gets(rule).map { it.text }

    var lastPosition = 0

    override fun enterEveryRule(ctx: ParserRuleContext) {
        layers.add(ArrayList())
    }

    open fun String.whitespaceReplacements(): String {
        return this
    }

    var generateCommentTokens = false

    override fun visitTerminal(node: TerminalNode) {

        layers.last().add(
            Section(
                rule = -node.symbol.type,
                text = terminalRewrites[node.symbol.type]?.invoke(node.text) ?: node.text,
                spacingBefore = tokenStream.getHiddenTokensToLeft(node.symbol.tokenIndex)
                    ?.joinToString("") { it.text }?.whitespaceReplacements() ?: ""
            )
        )
        lastPosition = node.symbol.tokenIndex

    }

    override fun exitEveryRule(ctx: ParserRuleContext) {
        val toAdd = overridden ?: default
        overridden = null
        layers.removeAt(layers.lastIndex)

        toAdd.rule = ctx.ruleIndex
        layers.last().add(toAdd)
    }
}

fun Iterable<RewriteListener.Section>.joinClean(): RewriteListener.Section {
    return this.asSequence().joinClean()
}

fun Sequence<RewriteListener.Section>.joinClean(): RewriteListener.Section {
    return RewriteListener.Section(
        text = buildString {
            var first = true
            this@joinClean.forEach {
                if(first){
                    first = false
                    append(it.text)
                } else {
                    append(it.toOutputString())
                }
            }
        },
        spacingBefore = this.firstOrNull()?.spacingBefore ?: ""
    )
}
