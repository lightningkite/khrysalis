package com.lightningkite.khrysalis.generic

import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.collections.HashMap

interface TranslatorInterface<OUT: Any, RESULT> {
    fun translate(
        rule: Any,
        out: OUT,
        afterPriority: Int = Int.MAX_VALUE
    ): RESULT
}

fun <RESULT, IN : Any, IDENTIFIER> PartialTranslator<Appendable, RESULT, IN, IDENTIFIER>.Context.line() {
    out.appendln()
}
fun <RESULT, IN : Any, IDENTIFIER> PartialTranslator<Appendable, RESULT, IN, IDENTIFIER>.Context.line(text: String) {
    out.appendln(text)
}
inline fun <RESULT, IN : Any, IDENTIFIER> PartialTranslator<Appendable, RESULT, IN, IDENTIFIER>.Context.line(action: ()->Unit) {
    action()
    out.appendln()
}

abstract class PartialTranslator<OUT : Any, RESULT, IN : Any, IDENTIFIER> {

    open val parent: TranslatorInterface<OUT, RESULT>? = null

    abstract fun getIdentifier(rule: IN): IDENTIFIER
    abstract fun emitDefault(identifier: IDENTIFIER, rule: IN, out: OUT): RESULT

    inner class Handler(
        val identifier: IDENTIFIER,
        val condition: Context.() -> Boolean = { true },
        val priority: Int = 0,
        val action: Context.() -> RESULT
    ) : Comparable<Handler> {
        override fun compareTo(other: Handler): Int = -priority.compareTo(other.priority)
    }

    open inner class Context {
        val partialTranslator = this@PartialTranslator
        lateinit var rule: IN
        lateinit var out: OUT
        var option: Handler? = null

        fun doSuper() : RESULT = translate(rule, out, option?.priority ?: Int.MAX_VALUE)
        fun defer(identifier: IDENTIFIER) = translate(identifier, rule, out)

        fun emit(item: IN?): RESULT? {
            if (item == null) return null
            return translate(item, out)
        }

        inline operator fun IN?.unaryPlus() = emit(this)
        inline operator fun IN?.unaryMinus() = emit(this)

        @JvmName("unaryPlusAnyNullable")
        inline operator fun Any?.unaryPlus() = emit(this)
        @JvmName("unaryMinusAnyNullable")
        inline operator fun Any?.unaryMinus() = emit(this)

        inline fun write(item: IN) = emit(item)

        @JvmName("emitAny")
        fun emit(item: Any?): RESULT? {
            if (item == null) return null
            return parent!!.translate(item, out)
        }
    }

    val recycledContexts = ConcurrentLinkedQueue<Context>()
    open fun makeContext(): Context = Context()
    open fun updateContext(
        context: Context,
        rule: IN,
        out: OUT
    ) {
        context.rule = rule
        context.out = out
    }

    inline fun <R> useContext(
        rule: IN,
        out: OUT,
        action: Context.() -> R
    ): R {
        val pulled = recycledContexts.poll() ?: makeContext()
        updateContext(pulled, rule, out)
        val result = action.invoke(pulled)
        recycledContexts.add(pulled)
        return result
    }

    val handlers: HashMap<IDENTIFIER, TreeSet<Handler>> = HashMap()

    fun handle(
        identifier: IDENTIFIER,
        condition: Context.() -> Boolean = { true },
        priority: Int = 0,
        action: Context.() -> RESULT
    ): Handler {
        val option = Handler(
            identifier = identifier,
            priority = priority,
            condition = condition,
            action = action
        )
        handlers.getOrPut(identifier) { TreeSet() }.add(option)
        return option
    }

    open fun translate(
        identifier: IDENTIFIER,
        rule: IN,
        out: OUT,
        afterPriority: Int = Int.MAX_VALUE
    ): RESULT {
        return useContext(rule, out) {
            handlers[identifier]?.asSequence()
                ?.dropWhile { it.priority >= afterPriority }
                ?.filter { it.condition.invoke(this) }
                ?.firstOrNull()
                ?.let {
                    this.option = it
                    it.action.invoke(this)
                } ?: emitDefault(identifier, rule, out)
        }
    }

    fun translate(
        rule: IN,
        out: OUT,
        afterPriority: Int = Int.MAX_VALUE
    ): RESULT = translate(getIdentifier(rule), rule, out, afterPriority)
}

