package com.lightningkite.khrysalis.generic

import com.lightningkite.khrysalis.replacements.Replacements
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import java.util.*
import kotlin.collections.HashMap

abstract class KotlinTranslator<OUT : Any> {
    val handlers = TypeMultiMap<Handler<*>>()

    abstract fun emitFinalDefault(rule: Any, out: OUT)
    fun translate(
        rule: Any?,
        out: OUT,
        afterRule: Handler<*>? = null
    ) {
        if(rule == null) return
//        handlers[rule::class.java]
//            .map { it.any }
//            .firstOrNull { it.priority < afterPriority && it.condition(ContextByType(out, rule, it)) }
//            ?.let {
//                it.action(ContextByType(out, rule, it))
//            } ?: emitFinalDefault(rule, out)
        handlers[rule::class.java]
            .map { it.any }
            .let{
                if(afterRule == null) it
                else it.dropWhile { it != afterRule }.drop(1)
            }
            .firstOrNull { it.condition(ContextByType(out, rule, it)) }
            ?.let { ruleLock ->
                handlers[rule::class.java]
                    .map { it.any }
                    .let{
                        if(afterRule == null) it
                        else it.dropWhile { it != afterRule }.drop(1)
                    }
                    .filter { it.priority == ruleLock.priority && it.hierarchyHeight == ruleLock.hierarchyHeight && it.condition(ContextByType(out, rule, it)) }
                    .let {
                        it.singleOrNull() ?: throw IllegalStateException("Multiple matching rules of equal priority: ${it.map{ "${it.hierarchyHeight} - ${it.priority} - ${it.action}" }.joinToString()}")
                    }
                    .let {
                        it.action(ContextByType(out, rule, it))
                    }
            }?: emitFinalDefault(rule, out)

    }

    @Suppress("UNCHECKED_CAST")
    fun <T> handle(
        type: Class<T>,
        condition: ContextByType<T>.() -> Boolean = { true },
        priority: Int = 0,
        hierarchyHeight: Int? = null,
        action: ContextByType<T>.() -> Unit
    ): Handler<T> {
        val newHandler = Handler(hierarchyHeight ?: type.superThings().count(), condition, priority, action)
        handlers.insert(type, newHandler)
        return newHandler
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> handle(
        noinline condition: ContextByType<T>.() -> Boolean = { true },
        priority: Int = 0,
        hierarchyHeight: Int? = null,
        noinline action: ContextByType<T>.() -> Unit
    ): Handler<T> = handle(T::class.java, condition, priority, hierarchyHeight, action)

    inner class Handler<T>(
        val hierarchyHeight: Int,
        val condition: ContextByType<T>.() -> Boolean = { true },
        val priority: Int = 0,
        val action: ContextByType<T>.() -> Unit
    ) : Comparable<Handler<*>> {
        private val uuid: UUID = UUID.randomUUID()
        override fun compareTo(other: Handler<*>): Int {
            var result = other.hierarchyHeight.compareTo(this.hierarchyHeight)
            if (result == 0) {
                result = other.priority.compareTo(this.priority)
            }
            if (result == 0) {
                result = this.uuid.compareTo(other.uuid)
            }
            return result
        }
    }
    @Suppress("UNCHECKED_CAST")
    private val Handler<*>.any: Handler<Any> get() = this as Handler<Any>

    inner class ContextByType<T>(val out: OUT, val typedRule: T, val option: Handler<T>) {
        val partialTranslator = this@KotlinTranslator
        var noReuse: Boolean = false
        fun doSuper() {
            translate(typedRule, out, option)
        }
        fun emit(item: Any?) {
            translate(item, out)
        }
        inline operator fun Any?.unaryPlus() = emit(this)
        inline operator fun Any?.unaryMinus() = emit(this)
        inline fun write(item: Any?) = emit(item)
    }
}

class TypeMultiMap<V: Comparable<V>>() {
    private var dirty = true
    private lateinit var cached: MutableMap<Class<*>, Collection<V>>
    private val direct = HashMap<Class<*>, ArrayList<V>>()
    val all: Map<Class<*>, Collection<V>> get() {
        if(dirty) clean()
        return cached
    }
    fun insert(key: Class<*>, value: V) {
        dirty = true
        direct.getOrPut(key) { ArrayList() }.add(value)
    }
    operator fun get(key: Class<*>): Sequence<V> {
        if(dirty) clean()
        return cached.getOrPut(key) {
            key.superThings().asSequence()
                .distinctBy { it }
                .mapNotNull { direct[it] }
                .flatten()
                .toSortedSet()
        }.asSequence()
    }
    private fun clean() {
        cached = direct.keys.associateWith {
            it.superThings().asSequence()
                .distinctBy { it }
                .mapNotNull { direct[it] }
                .flatten()
                .toSortedSet()
        }.toMutableMap()
        dirty = false
    }
}

private fun Class<*>.superThings(): Sequence<Class<*>> {
    val fromInterfaces: Sequence<Class<*>> = interfaces.asSequence().flatMap { it.superThings() }
    val fromSuper: Sequence<Class<*>> = (superclass?.superThings() ?: emptySequence())
    return sequenceOf(this) + fromInterfaces + fromSuper
}