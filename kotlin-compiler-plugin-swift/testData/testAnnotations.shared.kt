package com.test

import com.test.magicVariable
import kotlin.math.absoluteValue
import com.lightningkite.khrysalis.*

private val annotationsLambda: @escaping() ()->Unit = {}
private fun annotationsLambdaUseTest(lambda: @Escaping() ()->Unit) {
    lambda()
}
private fun Int.annotationsLambdaWeirdness(l: @Escaping() ()->Unit = {}){
    l()
}