package com.lightningkite.khrysalis.generic

class TranslationOption<T : Any>(
    val priority: Int,
    val condition: TranslationalCondition<T>,
    val action: TranslatorContext<T>.() -> Unit
) : Comparable<TranslationOption<*>> {
    override fun compareTo(other: TranslationOption< *>): Int = -this.priority.compareTo(other.priority)
}
