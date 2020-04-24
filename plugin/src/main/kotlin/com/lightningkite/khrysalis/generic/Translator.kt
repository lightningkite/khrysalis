package com.lightningkite.khrysalis.generic

import com.lightningkite.khrysalis.utils.XmlNode
import com.lightningkite.khrysalis.utils.forEachBetween
import com.lightningkite.khrysalis.utils.kabobCase
import com.lightningkite.khrysalis.web.layout.ResultNode
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.TerminalNode
import java.lang.Appendable
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.collections.HashMap

open class Translator(val sourceLanguage: SourceLanguage) : TranslatorInterface<Appendable, Unit> {

    inner class RuleTranslator : PartialTranslatorByType<Appendable, Unit, ParserRuleContext>() {
        override val parent = this@Translator
        override fun emitDefault(identifier: Class<*>, rule: ParserRuleContext, out: Appendable) {
            rule.children?.forEachBetween(
                forItem = {
                    when (it) {
                        is ParserRuleContext -> translate(it, out)
                        is TerminalNode -> token.translate(it, out)
                    }
                },
                between = { out.append(' ') }
            ) ?: out.append(rule.text)
        }
    }

    inner class TokenTranslator : PartialTranslator<Appendable, Unit, TerminalNode, Int>() {
        override val parent = this@Translator
        override fun getIdentifier(rule: TerminalNode): Int = rule.symbol.type
        override fun emitDefault(identifier: Int, rule: TerminalNode, out: Appendable) {
            out.append(rule.text)
        }
    }

    val rule = RuleTranslator()
    val token = TokenTranslator()

    override fun translate(rule: Any, out: Appendable, afterPriority: Int): Unit {
        return when (rule) {
            is String -> {
                out.append(rule)
                Unit
            }
            is Char -> {
                out.append(rule)
                Unit
            }
            is ParserRuleContext -> this.rule.translate(rule, out)
            is TerminalNode -> this.token.translate(rule.symbol.type, rule, out)
            else -> Unit
        }
    }

    inline fun <reified T : ParserRuleContext> handle(
        noinline condition: PartialTranslatorByType<Appendable, Unit, ParserRuleContext>.ContextByType<T>.() -> Boolean = { true },
        priority: Int = 0,
        noinline action: PartialTranslatorByType<Appendable, Unit, ParserRuleContext>.ContextByType<T>.() -> Unit
    ): PartialTranslator<Appendable, Unit, ParserRuleContext, Class<*>>.Handler =
        rule.handle(condition, priority, action)

    fun handle(
        token: Int,
        condition: PartialTranslator<Appendable, Unit, TerminalNode, Int>.Context.() -> Boolean = { true },
        priority: Int = 0,
        action: PartialTranslator<Appendable, Unit, TerminalNode, Int>.Context.() -> Unit
    ): PartialTranslator<Appendable, Unit, TerminalNode, Int>.Handler = this.token.handle(token, condition, priority, action)

}

fun PartialTranslator<Appendable, Unit, ParserRuleContext, Class<*>>.Context.emitDefault(rule: ParserRuleContext, separator: String = " ", filter: (Any)->Boolean = { true }) {
    rule.children.filter(filter).forEachBetween(
        forItem = { emit(it) },
        between = { out.append(separator) }
    )
}
