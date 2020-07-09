package com.test

import com.test.magicVariable
import kotlin.math.absoluteValue
import com.lightningkite.khrysalis.escaping

private val annotationsLambda: @escaping() ()->Unit = {}
private fun annotationsLambdaUseTest(lambda: @escaping() ()->Unit) {
    lambda()
}