package com.lightningkite.khrysalis.intellij

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.lightningkite.khrysalis.analysis.bindingContext
import com.lightningkite.khrysalis.analysis.resolvedCall
import com.lightningkite.khrysalis.util.fqNameWithoutTypeArgs
import com.lightningkite.khrysalis.util.simplerFqName
import com.lightningkite.khrysalis.util.walkTopDown
import org.jetbrains.kotlin.descriptors.DeclarationDescriptorWithSource
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.idea.configuration.externalProjectId
import org.jetbrains.kotlin.idea.configuration.externalProjectPath
import org.jetbrains.kotlin.idea.intentions.receiverType
import org.jetbrains.kotlin.psi.KtCallElement
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.source.PsiSourceFile
import java.util.ArrayList

class KhrysalisAnnotator : ExternalAnnotator<KhrysalisAnnotator.KtFileAnalyzed, List<KhrysalisAnnotator.NeedsTranslation>>() {
    init {
        Logger.getInstance(this::class.java).warn("KhrysalisAnnotator initialized")
    }

    data class NeedsTranslation(
        val element: KtElement,
        val fqn: String,
        val receiver: String? = null
    )

    private inline fun <reified T> Any?.maybeCast(): T? = this as? T
    private fun DeclarationDescriptorWithSource.isTranslated(): Boolean = source.containingFile.maybeCast<PsiSourceFile>()
        ?.psiFile?.maybeCast<KtFile>()
        ?.annotationEntries?.any { it.name == "SharedCode" } == true
    private fun PsiElement.walkTopDown(): Sequence< PsiElement> {
        return sequenceOf(this) + this.children.flatMap { it.walkTopDown() }
    }

    data class KtFileAnalyzed(val file: KtFile, val context: BindingContext)

    override fun collectInformation(file: PsiFile): KtFileAnalyzed? {
        return (file as? KtFile)?.let {
            KtFileAnalyzed(it, it.analyze())
        }
    }

    override fun doAnnotate(collectedInfo: KtFileAnalyzed): List<NeedsTranslation> = try {
        val module = (collectedInfo.file as PsiElement).module ?: throw IllegalStateException()
        Logger.getInstance(this::class.java).warn("""
            Analyzing ${collectedInfo.file.name}
            name: ${module.name}
            externalProjectPath: ${module.externalProjectPath}
            externalProjectId: ${module.externalProjectId}
            moduleFile: ${module.moduleFile}
            moduleFilePath: ${module.moduleFilePath}
            moduleTypeName: ${module.moduleTypeName}
            """.trimIndent())
        val khrysalisModule = (collectedInfo.file as PsiFile).khrysalisModule ?: run {
            Logger.getInstance(this::class.java).warn("Khrysalis module not found!")
            return listOf()
        }
        val results = ArrayList<NeedsTranslation>()
        val file = collectedInfo.file
        bindingContext = collectedInfo.context

        file.walkTopDown()
            .forEach {
                when (it) {
                    is KtCallElement -> it.resolvedCall?.resultingDescriptor?.let { descriptor ->
                        if (descriptor.isTranslated()) return@forEach
                        if (khrysalisModule.swift.getCall(descriptor) != null) return@forEach
                        results.add(
                            NeedsTranslation(
                                element = it,
                                fqn = descriptor.simplerFqName,
                                receiver = descriptor.receiverType()?.fqNameWithoutTypeArgs
                            )
                        )
                    }
                }
            }

        // Scan for calls
        // Scan for operator calls
        // Scan for gets
        // Scan for sets
        // Scan for types
        Logger.getInstance(this::class.java).warn("Analysis result: ${results}")
        results
    } catch(e: Exception) {
        Logger.getInstance(this::class.java).warn("Analysis error: ${e.message}")
        throw e
    }

    override fun apply(file: PsiFile, annotationResult: List<NeedsTranslation>, holder: AnnotationHolder) {
        for (it in annotationResult) {
            holder.newAnnotation(HighlightSeverity.WARNING, "No translation found for ${it.fqn}")
                .range(TextRange(it.element.textRange.startOffset, it.element.textRange.endOffset))
        }
    }
}