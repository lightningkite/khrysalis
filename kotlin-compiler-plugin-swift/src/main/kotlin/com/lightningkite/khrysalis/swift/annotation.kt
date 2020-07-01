package com.lightningkite.khrysalis.swift

import org.jetbrains.kotlin.psi.KtAnnotation
import org.jetbrains.kotlin.psi.KtAnnotationEntry

fun SwiftTranslator.registerAnnotation() {
    handle<KtAnnotation> { /*skip*/ }
    handle<KtAnnotationEntry> { /*skip*/ }
}
