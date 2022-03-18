package com.lightningkite.khrysalis.intellij

import com.intellij.openapi.module.Module
import com.intellij.openapi.util.Disposer
import io.reactivex.rxjava3.disposables.CompositeDisposable
import org.jetbrains.kotlin.descriptors.DeclarationDescriptorWithSource
import org.jetbrains.kotlin.idea.configuration.externalProjectId
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.source.PsiSourceFile
import java.util.*

//class KhrysalisPluginService

private val disposableRemoved = WeakHashMap<com.intellij.openapi.Disposable, CompositeDisposable>()
val com.intellij.openapi.Disposable.removed: CompositeDisposable
    get() = disposableRemoved.getOrPut(this) {
        val c = CompositeDisposable()
        Disposer.register(this) {
            c.dispose()
        }
        c
    }

val Module.gradleName: String get() {
    return this.name.removeSuffix(".main").removeSuffix(".test")
}