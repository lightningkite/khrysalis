package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.generic.line
import org.jetbrains.kotlin.psi.KtAnnotation

fun TypescriptTranslator.registerAnnotation() {
    handle<KtAnnotation> { /*skip*/ }
}
