package com.lightningkite.khrysalis.web.typescript

import com.lightningkite.khrysalis.generic.line
import com.lightningkite.khrysalis.ios.swift.parentIfType
import com.lightningkite.khrysalis.utils.forEachBetween
import org.jetbrains.kotlin.KotlinParser

fun TypescriptTranslator.registerAnnotation() {
    handle<KotlinParser.AnnotationContext> { /*skip*/ }
}
