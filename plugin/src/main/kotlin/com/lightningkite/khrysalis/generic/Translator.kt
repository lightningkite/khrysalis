package com.lightningkite.khrysalis.generic

import org.antlr.v4.runtime.tree.TerminalNode
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.collections.HashMap

open class Translator(val sourceLanguage: SourceLanguage) {

    val virtualTypeHandlers: HashMap<String, TreeSet<TranslationOption<*>>> =
        HashMap()
    val typeHandlers: HashMap<Class<*>, TreeSet<TranslationOption<*>>> =
        HashMap()
    val tokenHandlers = Array(sourceLanguage.tokenCount) { TreeSet<TranslationOption<TerminalNode>>() }

    inline fun <reified T : Any> handle(
        noinline condition: TranslationalCondition<T> = { true },
        priority: Int = 0,
        noinline action: TranslatorContext<T>.() -> Unit
    ): TranslationOption<T> {
        val set: TreeSet<TranslationOption<*>> =
            typeHandlers.getOrPut(T::class.java) { TreeSet<TranslationOption<*>>() }
        val option = TranslationOption(
                priority = priority,
                condition = condition,
                action = action
            )
        set.add(option)
        return option
    }

    inline fun <reified T : VirtualType> handle(
        type: String,
        noinline condition: TranslationalCondition<T> = { true },
        priority: Int = 0,
        noinline action: TranslatorContext<T>.() -> Unit
    ): TranslationOption<T> {
        val set: TreeSet<TranslationOption<*>> = virtualTypeHandlers.getOrPut(type) { TreeSet<TranslationOption<*>>() }
        val option = TranslationOption(
                priority = priority,
                condition = condition,
                action = action
            )
        set.add(option)
        return option
    }

    fun handle(
        token: Int,
        condition: TranslationalCondition<TerminalNode> = { true },
        priority: Int = 0,
        action: TranslatorContext<TerminalNode>.() -> Unit
    ): TranslationOption<TerminalNode> {
        val option = TranslationOption(
                priority = priority,
                condition = condition,
                action = action
            )
        tokenHandlers[token].add(option)
        return option
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any> getHandlers(): Iterable<TranslationOption<T>> =
        typeHandlers[T::class.java] as TreeSet<TranslationOption<T>>

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getHandlers(type: Class<T>): Iterable<TranslationOption<T>> =
        typeHandlers[type] as? TreeSet<TranslationOption<T>> ?: emptyList()

    fun getHandlers(token: Int): Iterable<TranslationOption<TerminalNode>> = tokenHandlers[token]

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getHandlers(virtualType: String): Iterable<TranslationOption<T>> =
        virtualTypeHandlers[virtualType] as TreeSet<TranslationOption<T>>

    val pool =
        ConcurrentLinkedQueue<TranslatorContext<*>>()
    inline fun <T : Any> context(out: Appendable, rule: T, action: TranslatorContext<T>.() -> Unit) {
        @Suppress("UNCHECKED_CAST")
        val existing: TranslatorContext<*> = pool.poll() ?: TranslatorContext<Any>(this)
        @Suppress("UNCHECKED_CAST") val fixed = (existing as TranslatorContext<T>)
        fixed.out = out
        fixed.rule = rule
        action(fixed)
        pool.add(fixed)
    }
    operator fun <T : Any> invoke(out: Appendable, rule: T) = context(out, rule) { run() }
}
