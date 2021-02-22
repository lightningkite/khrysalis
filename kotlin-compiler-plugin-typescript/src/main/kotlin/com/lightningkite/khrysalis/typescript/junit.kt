package com.lightningkite.khrysalis.typescript

import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClass


fun TypescriptTranslator.registerJUnit() {
    val junitAnno = FqName("org.junit.Test")
    handle<KtClass>(
        condition = { typedRule.body?.functions?.any { it.annotationEntries.any { it.resolvedAnnotation?.fqName == junitAnno } } == true },
        priority = 100_000,
        action = {
            -doSuper()
            -'\n'
            -"""describe("${typedRule.name ?: "Unnamed"}", ()=> {"""
            -'\n'
            typedRule.body?.functions
                ?.filter { it.annotationEntries.any { it.resolvedAnnotation?.fqName == junitAnno } }
                ?.forEach {
                    -"""test("${it.name}", () => { new """
                    -typedRule.nameIdentifier
                    -"()."
                    -it.resolvedFunction?.tsName
                    -"() "
                    -"})\n"
                }
            -"})\n"
        }
    )
}