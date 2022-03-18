package com.lightningkite.khrysalis.intellij

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.lang.Language
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiManager
import com.lightningkite.khrysalis.analysis.bindingContext
import com.lightningkite.khrysalis.analysis.resolvedCall
import com.lightningkite.khrysalis.analysis.resolvedReferenceTarget
import com.lightningkite.khrysalis.replacements.Replacements
import com.lightningkite.khrysalis.replacements.TemplatePart
import com.lightningkite.khrysalis.shouldBeTranslated
import com.lightningkite.khrysalis.util.fqNameWithoutTypeArgs
import com.lightningkite.khrysalis.util.simpleFqName
import com.lightningkite.khrysalis.util.simplerFqName
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.idea.caches.resolve.analyzeWithAllCompilerChecks
import org.jetbrains.kotlin.idea.intentions.receiverType
import org.jetbrains.kotlin.idea.kdoc.insert
import org.jetbrains.kotlin.idea.util.projectStructure.getModuleDir
import org.jetbrains.kotlin.idea.util.sourceRoots
import org.jetbrains.kotlin.load.java.descriptors.JavaClassDescriptor
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import org.jetbrains.kotlin.resolve.source.PsiSourceFile
import java.io.File
import java.util.ArrayList

class KhrysalisAnnotator : ExternalAnnotator<KtFile, List<KhrysalisAnnotator.NeedsTranslation>>() {
    init {
        Logger.getInstance(this::class.java).warn("KhrysalisAnnotator initialized")
    }

    data class NeedsTranslation(
        val lang: String,
        val element: KtElement,
        val fqn: String,
        val type: Type,
        val additionalInfo: String = "",
        val receiver: String? = null
    ) {
        enum class Type {
            Call, Class, Property
        }
    }

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

    fun checkLang(file: KtFile, language: String, equivalents: Replacements, results: MutableList<NeedsTranslation>) {

        ApplicationManager.getApplication().runReadAction {
            bindingContext = file.analyzeWithAllCompilerChecks().bindingContext
            fun KtElement.analyze() {
                if(this is KtAnnotated) {
                    if(this.annotationEntries.any { it.typeReference?.text?.endsWith("Suppress") == true && it.valueArguments.any { it.getArgumentExpression()?.text?.trim('"') == "MISSING_EQUIVALENTS" } }) {
                        return
                    }
                }
                when (this) {
                    is KtPackageDirective -> {}
                    is KtImportDirective -> {}
                    is KtAnnotationEntry -> {}
                    is KtCallExpression -> {
                        val call = this.resolvedCall ?: return
                        val descriptor = call.resultingDescriptor
                        when {
                            descriptor.isTranslated() ||
                                    descriptor.dispatchReceiverParameter != null ||
                                    descriptor.containingDeclaration is JavaClassDescriptor ||
                                    descriptor.hasDirect(equivalents) -> {}
                            else -> equivalents.getCall(call, descriptor)?.let { replacement ->
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
                                    lang = language,
                                    element = this,
                                    fqn = descriptor.simplerFqName,
                                    type = NeedsTranslation.Type.Call,
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
                                    descriptor.hasDirect(equivalents) -> {}
                            (descriptor as? PropertyDescriptor)?.dispatchReceiverParameter != null -> return
                            else -> (descriptor as? PropertyDescriptor)?.let { equivalents.getGet(it) }
                                ?: equivalents.getGet(descriptor)
                                ?: equivalents.getType(descriptor)
                                ?: results.add(
                                    NeedsTranslation(
                                        lang = language,
                                        element = this,
                                        fqn = descriptor.simplerFqName,
                                        type = when(descriptor) {
                                            is PropertyDescriptor -> NeedsTranslation.Type.Property
                                            is ClassDescriptor -> NeedsTranslation.Type.Class
                                            else -> return
                                        },
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
    }

    override fun doAnnotate(collectedInfo: KtFile): List<NeedsTranslation> = try {
        val khrysalisModule = (collectedInfo as PsiFile).khrysalisModule ?: run {
            Logger.getInstance(this::class.java).warn("Khrysalis module not found!")
            return listOf()
        }
        val results = ArrayList<NeedsTranslation>()
        val file = collectedInfo

        checkLang(file, "swift", khrysalisModule.swift, results)
        checkLang(file, "ts", khrysalisModule.typescript, results)

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
            holder.newAnnotation(HighlightSeverity.WARNING, "No ${it.lang} translation found for ${it.fqn}. ${it.additionalInfo}")
                .range(it.element as PsiElement)
                .newFix(CreateEquivalent(it)).registerFix()
                .newFix(DeclareEquivalent(it)).registerFix()
                .newFix(IgnoreEquivalent(it)).registerFix()
                .create()
        }
    }

    class CreateEquivalent(val issue: NeedsTranslation): IntentionAction {
        override fun startInWriteAction(): Boolean = false

        override fun getText(): String = "Create ${issue.lang} Equivalent"
        override fun getFamilyName(): String = "Khrysalis"

        override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean = true

        override fun invoke(project: Project, editor: Editor, file: PsiFile) {
            val srcRoot = file.module?.sourceRoots?.find { it.path.contains("/src/") } ?: return
            val equivalentFileRoot = srcRoot.toNioPath().toFile().resolve("../equivalents")
            val equivalentFile = equivalentFileRoot.resolve("manual.${issue.lang}.yaml")
            equivalentFile.parentFile.mkdirs()
            val casualName = issue.fqn.substringAfterLast('.')
            val template = when(issue.type) {
                NeedsTranslation.Type.Call -> if(issue.receiver != null) """
                    - id: ${issue.fqn}
                      type: call
                      receiver: ${issue.receiver}
                      template: '~this~.$casualName() /*TODO*/'
                """.trimIndent() else """
                    - id: ${issue.fqn}
                      type: call
                      template: '~this~.$casualName() /*TODO*/'
                """.trimIndent()
                NeedsTranslation.Type.Class -> """
                    - id: ${issue.fqn}
                      type: type
                      receiver: ${issue.receiver}
                      template: '$casualName'
                """.trimIndent()
                NeedsTranslation.Type.Property -> if(issue.receiver != null) """
                    - id: ${issue.fqn}
                      type: get
                      receiver: ${issue.receiver}
                      template: '~this~.$casualName /*TODO*/'
                    - id: ${issue.fqn}
                      type: set
                      receiver: ${issue.receiver}
                      template: '~this~.$casualName = ~value~ /*TODO*/'
                """.trimIndent() else """
                    - id: ${issue.fqn}
                      type: get
                      template: '~this~.$casualName /*TODO*/'
                    - id: ${issue.fqn}
                      type: set
                      template: '~this~.$casualName = ~value~ /*TODO*/'
                """.trimIndent()
            }
            if(equivalentFile.exists()) {
                equivalentFile.appendText(template + "\n")
            } else {
                equivalentFile.writeText(template + "\n")
            }
            val vfs = VfsUtil.findFileByIoFile(equivalentFile, true) ?: return
//            val doc = FileDocumentManager.getInstance().getDocument(vfs) ?: return
            FileEditorManager.getInstance(project).openTextEditor(OpenFileDescriptor(project, vfs), true)
        }
    }
    class DeclareEquivalent(val issue: NeedsTranslation): IntentionAction {
        override fun startInWriteAction(): Boolean = false

        override fun getText(): String = "Declare ${issue.lang} Equivalent"
        override fun getFamilyName(): String = "Khrysalis"

        override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean = true

        override fun invoke(project: Project, editor: Editor, file: PsiFile) {
            val srcRoot = file.module?.sourceRoots?.find { it.path.contains("/src/") } ?: return
            val equivalentFileRoot = srcRoot.toNioPath().toFile().resolve("../equivalents")
            val equivalentFile = equivalentFileRoot.resolve("manual.${issue.lang}.fqnames")
            equivalentFile.parentFile.mkdirs()
            if(equivalentFile.exists()) {
                equivalentFile.appendText(issue.fqn + "\n")
            } else {
                equivalentFile.writeText("myPackageName\n\n" + issue.fqn + "\n")
            }
            val vfs = VfsUtil.findFileByIoFile(equivalentFile, true) ?: return
//            val doc = FileDocumentManager.getInstance().getDocument(vfs) ?: return
            FileEditorManager.getInstance(project).openTextEditor(OpenFileDescriptor(project, vfs), true)
        }
    }
    class IgnoreEquivalent(val issue: NeedsTranslation): IntentionAction {
        override fun startInWriteAction(): Boolean = false

        override fun getText(): String = "Declare ${issue.lang} Equivalent"
        override fun getFamilyName(): String = "Khrysalis"

        override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean = true

        override fun invoke(project: Project, editor: Editor, file: PsiFile) {
            val srcRoot = file.module?.sourceRoots?.find { it.path.contains("/src/") } ?: return
            val equivalentFileRoot = srcRoot.toNioPath().toFile().resolve("../equivalents")
            val equivalentFile = equivalentFileRoot.resolve("manual.${issue.lang}.fqnames")
            equivalentFile.parentFile.mkdirs()
            if(equivalentFile.exists()) {
                equivalentFile.appendText(issue.fqn + "\n")
            } else {
                equivalentFile.writeText("*ignore\n\n" + issue.fqn + "\n")
            }
            val vfs = VfsUtil.findFileByIoFile(equivalentFile, true) ?: return
//            val doc = FileDocumentManager.getInstance().getDocument(vfs) ?: return
            FileEditorManager.getInstance(project).openTextEditor(OpenFileDescriptor(project, vfs), true)
        }
    }
}