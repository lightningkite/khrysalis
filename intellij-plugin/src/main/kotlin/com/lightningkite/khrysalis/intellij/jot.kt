package com.lightningkite.khrysalis.intellij

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.cli.jvm.compiler.TopDownAnalyzerFacadeForJVM
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import java.awt.FlowLayout
import javax.swing.*

class KhrysalisToolWindowFactory: ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        toolWindow.contentManager.addContent(toolWindow.contentManager.factory.createContent(
            JPanel().apply {
                layout = BoxLayout(this, BoxLayout.Y_AXIS)
                add(JLabel("Khrysalis"))
                add(JTextArea("The info text will go here").apply {
                    this.isEditable = false
                })
            },
            "Khrysalis",
            true
        ))
    }
}

data class IssueMessage(
    val severity: HighlightSeverity,
    val message: String,
    val range: TextRange
)

class KhrysalisAnnotator: ExternalAnnotator<PsiFile, List<IssueMessage>>() {
    override fun doAnnotate(collectedInfo: PsiFile): List<IssueMessage> {

        return listOf()
    }
    override fun apply(file: PsiFile, annotationResult: List<IssueMessage>, holder: AnnotationHolder) {
        for(it in annotationResult) {
            holder.newAnnotation(it.severity, it.message)
                .range(it.range)
        }
    }
}

//import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
//import com.lightningkite.khrysalis.analysis.resolvedCall
//import com.lightningkite.khrysalis.replacements.Replacements
//import com.lightningkite.khrysalis.shouldBeTranslated
//import com.lightningkite.khrysalis.swift.replacements.SwiftJacksonReplacementsModule
//import com.lightningkite.khrysalis.typescript.replacements.TypescriptJacksonReplacementsModule
//import io.gitlab.arturbosch.detekt.api.*
//import org.jetbrains.kotlin.psi.KtExpression
//import org.jetbrains.kotlin.psi.KtFile
//import org.jetbrains.kotlin.psi.KtTypeReference
//import java.io.File
//import java.io.InputStream
//import java.util.zip.ZipEntry
//import java.util.zip.ZipFile
//
//class SmartRule(config: Config) : Rule(config) {
//    override val issue: Issue =
//        Issue(javaClass.simpleName, Severity.Warning, "This has no defined equivalents", Debt.FIVE_MINS)
//
//    val swiftEquivalents: Replacements = config.valueOrDefault<List<String>>("swiftEquivalents", listOf())
//        .asSequence()
//        .flatMap { File(it).walkZip() }
//        .filter { it.name.endsWith(".swift.yaml") || it.name.endsWith(".swift.yml") }
//        .let {
//            val parser = YAMLMapper().registerModule(SwiftJacksonReplacementsModule())
//            val repl = Replacements(parser)
//            for (file in it) {
//                file.file?.let {
//                    repl += it
//                } ?: run {
//                    repl += file.inputStream()
//                }
//            }
//            repl
//        }
//    val typescriptEquivalents: Replacements = config.valueOrDefault<List<String>>("typescriptEquivalents", listOf())
//        .asSequence()
//        .flatMap { File(it).walkZip() }
//        .filter { it.name.endsWith(".ts.yaml") || it.name.endsWith(".ts.yml") }
//        .let {
//            val parser = YAMLMapper().registerModule(TypescriptJacksonReplacementsModule())
//            val repl = Replacements(parser)
//            for (file in it) {
//                file.file?.let {
//                    repl += it
//                } ?: run {
//                    repl += file.inputStream()
//                }
//            }
//            repl
//        }
//
//    var shouldBeUsed = false
//    override fun preVisit(root: KtFile) {
//        super.preVisit(root)
//        shouldBeUsed = root.shouldBeTranslated()
//    }
//
//    override fun visitExpression(expression: KtExpression) {
//        super.visitExpression(expression)
//        expression.typ
//    }
//
//    override fun visitTypeReference(typeReference: KtTypeReference) {
//        typeReference.analyze()
//    }
//}
//
//data class FileOrZipEntry(val file: File? = null, val zipEntry: ZipEntry? = null, val zip: ZipFile? = null) {
//    val name: String get() = file?.name ?: zipEntry?.name ?: ""
//    fun inputStream(): InputStream = file?.inputStream() ?: zip!!.getInputStream(zipEntry!!)
//}
//
//fun File.walkZip(): Sequence<FileOrZipEntry> {
//    return this.walkTopDown().flatMap {
//        if (it.extension == "jar" || it.extension == "zip") {
//            val file = ZipFile(it)
//            file.entries().asIterator().asSequence().map { FileOrZipEntry(zipEntry = it, zip = file) }
//        } else if (it.isDirectory) {
//            sequenceOf()
//        } else {
//            sequenceOf(FileOrZipEntry(file = it))
//        }
//    }
//}