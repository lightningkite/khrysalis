package com.lightningkite.khrysalis.intellij

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.parentOfType
import com.lightningkite.khrysalis.analysis.bindingContext
import com.lightningkite.khrysalis.analysis.resolvedCall
import com.lightningkite.khrysalis.analysis.resolvedReferenceTarget
import com.lightningkite.khrysalis.analysis.resolvedType
import com.lightningkite.khrysalis.replacements.Replacements
import com.lightningkite.khrysalis.replacements.TemplatePart
import com.lightningkite.khrysalis.shouldBeTranslated
import com.lightningkite.khrysalis.util.fqNameWithoutTypeArgs
import com.lightningkite.khrysalis.util.simpleFqName
import com.lightningkite.khrysalis.util.simplerFqName
import com.lightningkite.khrysalis.util.walkTopDown
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.idea.caches.resolve.analyzeFully
import org.jetbrains.kotlin.idea.caches.resolve.analyzeWithAllCompilerChecks
import org.jetbrains.kotlin.idea.configuration.externalProjectId
import org.jetbrains.kotlin.idea.configuration.externalProjectPath
import org.jetbrains.kotlin.idea.intentions.receiverType
import org.jetbrains.kotlin.load.java.descriptors.JavaClassDescriptor
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.isAnnotationConstructor
import org.jetbrains.kotlin.resolve.source.PsiSourceFile
import java.util.ArrayList

class KhrysalisAnnotator : ExternalAnnotator<KtFile, List<KhrysalisAnnotator.NeedsTranslation>>() {
    init {
        Logger.getInstance(this::class.java).warn("KhrysalisAnnotator initialized")
    }

    data class NeedsTranslation(
        val type: String,
        val element: KtElement,
        val fqn: String,
        val additionalInfo: String = "",
        val receiver: String? = null
    )

    private inline fun <reified T> Any?.maybeCast(): T? = this as? T
    private fun DeclarationDescriptorWithSource.isTranslated(): Boolean {
        return source.containingFile.maybeCast<PsiSourceFile>()
            ?.psiFile?.maybeCast<KtFile>()
            ?.shouldBeTranslated() == true
    }

    private fun DeclarationDescriptor.hasDirect(replacements: Replacements): Boolean {
        if (replacements.direct.containsKey(this.simplerFqName)) return true
        if (replacements.direct.containsKey(this.simpleFqName)) return true
        if (this is CallableDescriptor) {
            return this.overriddenDescriptors.any { it.hasDirect(replacements) }
        }
        return false
    }

    override fun collectInformation(file: PsiFile): KtFile? {
        val asKt = file as? KtFile ?: return null
        if (!asKt.shouldBeTranslated()) return null
        return asKt
    }

    override fun doAnnotate(collectedInfo: KtFile): List<NeedsTranslation> = try {
        val khrysalisModule = (collectedInfo as PsiFile).khrysalisModule ?: run {
            Logger.getInstance(this::class.java).warn("Khrysalis module not found!")
            return listOf()
        }
        val results = ArrayList<NeedsTranslation>()
        val file = collectedInfo

        ApplicationManager.getApplication().runReadAction {
            bindingContext = file.analyzeWithAllCompilerChecks().bindingContext
            fun KtElement.analyze() {
                when (this) {
                    is KtPackageDirective -> {}
                    is KtImportDirective -> {}
                    is KtAnnotationEntry -> {}
//                    is KtQualifiedExpression -> {
//                        val call = this.resolvedCall ?: return
//                        val descriptor = call.resultingDescriptor
//                        when {
//                            descriptor.isTranslated() ||
//                                    descriptor.dispatchReceiverParameter != null ||
//                                    descriptor.containingDeclaration is JavaClassDescriptor ||
//                                    descriptor.hasDirect(khrysalisModule.swift) -> {}
//                            else -> khrysalisModule.swift.getCall(call, descriptor)?.let { replacement ->
//                                val used = replacement.template.allParts.toSet()
//                                if(used.contains(TemplatePart.AllParameters)) {
//                                    this.allChildren.filterIsInstance<KtElement>().forEach { it.analyze() }
//                                } else {
//                                    used.filterIsInstance<TemplatePart.ParameterByIndex>().forEach {
//                                        call.valueArgumentsByIndex?.get(it.index)?.arguments?.forEach {
//                                            it.getArgumentExpression()?.analyze()
//                                        }
//                                    }
//                                    used.filterIsInstance<TemplatePart.Parameter>().forEach {
//                                        call.valueArguments.entries.find { e -> e.key.name.asString() == it.name }?.value?.arguments?.forEach {
//                                            it.getArgumentExpression()?.analyze()
//                                        }
//                                    }
//                                    this.typeArguments.forEach { it.analyze() }
//                                }
//                                return
//                            } ?: results.add(
//                                NeedsTranslation(
//                                    type = "swift",
//                                    element = this,
//                                    fqn = descriptor.simplerFqName,
//                                    receiver = descriptor.receiverType()?.fqNameWithoutTypeArgs
//                                )
//                            )
//                        }
//                        this.allChildren.filterIsInstance<KtElement>().forEach { it.analyze() }
//                    }
                    is KtCallExpression -> {
                        val call = this.resolvedCall ?: return
                        val descriptor = call.resultingDescriptor
                        when {
                            descriptor.isTranslated() ||
                            descriptor.dispatchReceiverParameter != null ||
                            descriptor.containingDeclaration is JavaClassDescriptor ||
                            descriptor.hasDirect(khrysalisModule.swift) -> {}
                            else -> khrysalisModule.swift.getCall(call, descriptor)?.let { replacement ->
                                val used = replacement.template.allParts.toSet()
                                if(used.contains(TemplatePart.AllParameters)) {
                                    this.allChildren.filterIsInstance<KtElement>().forEach { it.analyze() }
                                } else {
                                    used.filterIsInstance<TemplatePart.ParameterByIndex>().forEach {
                                        call.valueArgumentsByIndex?.get(it.index)?.arguments?.forEach {
                                            it.getArgumentExpression()?.analyze()
                                        }
                                    }
                                    used.filterIsInstance<TemplatePart.Parameter>().forEach {
                                        call.valueArguments.entries.find { e -> e.key.name.asString() == it.name }?.value?.arguments?.forEach {
                                            it.getArgumentExpression()?.analyze()
                                        }
                                    }
                                    this.typeArguments.forEach { it.analyze() }
                                }
                                return
                            } ?: results.add(
                                NeedsTranslation(
                                    type = "swift",
                                    element = this,
                                    fqn = descriptor.simplerFqName,
                                    receiver = descriptor.receiverType()?.fqNameWithoutTypeArgs
                                )
                            )
                        }
                        this.allChildren.filterIsInstance<KtElement>().forEach { it.analyze() }
                    }
                    is KtSimpleNameExpression -> {
                        val descriptor = this.resolvedReferenceTarget ?: return
                        when {
                            descriptor is FunctionDescriptor -> return
                            descriptor is SyntheticPropertyDescriptor -> return
                            descriptor is ParameterDescriptor -> return
                            descriptor.name.asString().endsWith("Binding") -> return
                            descriptor.containingDeclaration?.name?.asString()?.endsWith("Binding") == true -> return
                            descriptor is PackageFragmentDescriptor -> return
                            (descriptor as? DeclarationDescriptorWithSource)?.isTranslated() == true ||
                                    descriptor.hasDirect(khrysalisModule.swift) -> {}
                            (descriptor as? PropertyDescriptor)?.dispatchReceiverParameter != null -> return
                            else -> (descriptor as? PropertyDescriptor)?.let { khrysalisModule.swift.getGet(it) }
                                ?: khrysalisModule.swift.getGet(descriptor)
                                ?: khrysalisModule.swift.getType(descriptor)
                                ?: results.add(
                                NeedsTranslation(
                                    type = "swift",
                                    element = this,
                                    fqn = descriptor.simplerFqName,
                                    additionalInfo = "${descriptor::class.qualifiedName} - ${descriptor} inside ${descriptor.containingDeclaration}",
                                    receiver = (descriptor as? PropertyDescriptor)?.receiverType()?.fqNameWithoutTypeArgs
                                )
                            )
                        }
                        this.allChildren.filterIsInstance<KtElement>().forEach { it.analyze() }
                    }
                    else -> this.allChildren.filterIsInstance<KtElement>().forEach { it.analyze() }
                }
            }
            file.analyze()
        }

        // Scan for calls
        // Scan for operator calls
        // Scan for gets
        // Scan for sets
        // Scan for types
        Logger.getInstance(
            this::
            class.java
        ).warn("Analysis result: ${results}")
        results
    } catch (e: Exception) {
        Logger.getInstance(this::class.java).warn("Analysis error: ${e.message}")
        throw e
    }

    override fun apply(file: PsiFile, annotationResult: List<NeedsTranslation>, holder: AnnotationHolder) {
        for (it in annotationResult) {
            holder.newAnnotation(HighlightSeverity.WARNING, "No ${it.type} translation found for ${it.fqn}. ${it.additionalInfo}")
                .range(it.element as PsiElement)
                .create()
        }
    }
}