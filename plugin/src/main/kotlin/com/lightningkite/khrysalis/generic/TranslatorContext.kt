package com.lightningkite.khrysalis.generic

import com.lightningkite.khrysalis.utils.forEachBetween
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.TerminalNode
import java.util.*

class TranslatorContext<T : Any>(
    val translator: Translator
) {
    lateinit var out: Appendable
    lateinit var rule: T
    var option: TranslationOption<T>? = null

    fun run(afterPriority: Int = Int.MAX_VALUE) {
        @Suppress("UNCHECKED_CAST")
        when (val rule = rule) {
            is VirtualType -> translator.getHandlers<T>(rule.type) as Iterable<TranslationOption<T>>
            is TerminalNode -> {
                val type = rule.symbol.type
                if(type == -1) return
                translator.getHandlers(type) as Iterable<TranslationOption<T>>
            }
            else -> translator.getHandlers(rule::class.java) as Iterable<TranslationOption<T>>
        }.asSequence()
            .dropWhile { it.priority >= afterPriority }
            .filter { it.condition.invoke(this) }
            .firstOrNull()
            ?.let {
                this.option = it
                it.action.invoke(this)
            } ?: emitDefault(rule)
    }

    fun doSuper() {
        run(option?.priority ?: 0)
    }

    fun emit(item: Any?) {
        if(item == null) return
        translator.context(out, item) {
            run()
        }
    }

    fun emit(item: String) = out.append(item)

    inline operator fun Any?.unaryPlus() = emit(this)
    inline operator fun Any?.unaryMinus() = emit(this)
    inline operator fun String.unaryPlus() = out.append(this)
    inline operator fun String.unaryMinus() = out.append(this)
    inline fun line(string: String) = out.appendln(string)
    inline fun line() = out.appendln()

    fun emitDefault(item: Any, divider: String = "") {
        when (val rule = rule) {
            is VirtualType -> rule.parts.forEachBetween(
                forItem = { +it },
                between = { +divider }
            )
            is ParserRuleContext -> (0 until rule.childCount).forEachBetween(
                forItem = { +rule.getChild(it) },
                between = { +divider }
            )
            is TerminalNode -> +rule.text
            else -> +(item.toString())
        }
    }

    fun emitDefault(item: Any, divider: String = "", filter: (Any) -> Boolean) {
        when (val rule = rule) {
            is VirtualType -> rule.parts.filter(filter).forEachBetween(
                forItem = { +it },
                between = { +divider }
            )
            is ParserRuleContext -> (0 until rule.childCount).asSequence().map { rule.getChild(it) }.filter(filter)
                .forEachBetween(
                    forItem = { +it },
                    between = { +divider }
                )
            is TerminalNode -> +rule.text
            else -> +(item.toString())
        }
    }

    fun write(item: Any) = emit(item)
    inline fun line(action: Appendable.() -> Unit) {
        action(out)
        line()
    }

    inline fun <Sub: Any> childContext(value: Sub, action: TranslatorContext<Sub>.() -> Unit) {
        translator.context<Sub>(out, value, action)
    }
}
