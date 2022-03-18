package com.lightningkite.khrysalis.intellij

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.lightningkite.rx.subscribeByNullable
import io.reactivex.rxjava3.kotlin.addTo
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextArea

class KhrysalisToolWindowFactory : ToolWindowFactory {
    companion object {
        internal val LOG = Logger.getInstance(
            KhrysalisToolWindowFactory::class.java
        )
    }

    init {
        LOG.warn("KhrysalisToolWindowFactory initialized")
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
//        val service = KhrysalisModuleService.getInstance(project)
        toolWindow.contentManager.addContent(
            toolWindow.contentManager.factory.createContent(
                JPanel().apply {
                    layout = BoxLayout(this, BoxLayout.Y_AXIS)
                    add(JLabel("Khrysalis"))
                    add(JTextArea("The info text will go here").apply {
                        this.isEditable = false
                        LOG.warn("KhrysalisToolWindowFactory Subscribing")
//                        project.psiElementAtCaret.subscribeByNullable {
//                            LOG.warn("KhrysalisToolWindowFactory hit!")
////                        it?.containingFile?.module?.let { m ->
////                            project.khrysalisEquivalents(m)
////                        }?.joinToString("\n")?.let {
////                            this.text = it
////                            this.repaint()
////                        }
//                            generateSequence(it) { it.parent }
//                                .toList()
//                                .reversed()
//                                .joinToString("\n") { it::class.simpleName ?: "Unknown" }
//                                .plus("\n${it?.module?.name}")
//                                .plus(
//                                    "\n${
//                                        it?.containingFile?.module?.let { m ->
//                                            project.khrysalisEquivalents(m)
//                                        }?.joinToString("\n")
//                                    }"
//                                )
//                                .let {
//                                    this.text = it
//                                    this.repaint()
//                                }
//                        }.addTo(service.removed)
                    })
                },
                "Khrysalis",
                true
            )
        )
    }
}