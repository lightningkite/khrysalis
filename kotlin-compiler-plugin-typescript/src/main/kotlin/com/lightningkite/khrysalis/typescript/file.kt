package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.generic.line
import org.jetbrains.kotlin.psi.KtImportAlias
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtPackageDirective
import com.lightningkite.khrysalis.typescript.replacements.*

fun TypescriptTranslator.registerFile() {
    handle<KtPackageDirective> {
    }
    handle<KtImportDirective> {
    }
}
