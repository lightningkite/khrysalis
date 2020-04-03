package com.test

abstract class AbstractBoi {
    abstract val dumb: Int
}

data class NonAbstract(
    val something: Int
) : AbstractBoi() {
    override val dumb: Int
        get() = something
}