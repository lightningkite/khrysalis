package com.lightningkite.khrysalis.generic

abstract class PartialTranslatorByType<OUT : Any, RESULT, IN : Any> : PartialTranslator<OUT, RESULT, IN, Class<*>>() {

    inner class ContextByType<T: IN>: Context() {
        @Suppress("UNCHECKED_CAST")
        val value: T get() = rule as T
    }

    override fun makeContext(): Context = ContextByType<IN>()

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : IN> handle(
        noinline condition: ContextByType<T>.() -> Boolean = { true },
        priority: Int = 0,
        noinline action: ContextByType<T>.() -> RESULT
    ): Handler {
        val identifier = T::class.java
        return handle(
            identifier = identifier,
            condition = condition as Context.() -> Boolean,
            priority = priority,
            action = action as Context.() -> RESULT
        )
    }
}
