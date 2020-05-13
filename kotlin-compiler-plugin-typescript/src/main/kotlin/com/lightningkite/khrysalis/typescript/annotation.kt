package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.generic.line
import org.jetbrains.kotlin.psi.KtAnnotation
import org.jetbrains.kotlin.psi.KtAnnotationEntry

fun TypescriptTranslator.registerAnnotation() {
    handle<KtAnnotation> { /*skip*/ }
    handle<KtAnnotationEntry> { /*skip*/ }
}
