package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.generic.PartialTranslatorByType
import com.lightningkite.khrysalis.generic.TranslatorInterface
import com.lightningkite.khrysalis.util.AnalysisExtensions
import org.jetbrains.kotlin.com.intellij.lang.PsiParser
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafElement
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.psi.psiUtil.getElementTextWithContext
import org.jetbrains.kotlin.psi.psiUtil.getTextWithLocation
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.isFakePsiElement
import kotlin.text.Appendable

class TypescriptTranslator(override val bindingContext: BindingContext) :
    PartialTranslatorByType<Appendable, Unit, Any>(), TranslatorInterface<Appendable, Unit>, AnalysisExtensions {
    override fun emitDefault(identifier: Class<*>, rule: Any, out: Appendable) {
        when (rule) {
            is Char -> out.append(rule)
            is String -> out.append(rule)
            is PsiElement -> {
                if (rule.firstChild == null) {
                    out.append(rule.text)
                    return
                } else {
                    var current = rule.firstChild!!
                    while (true) {
                        translate(current, out)
                        current = current.nextSibling ?: break
                    }
                }
            }
        }
    }

    val terminalMap = mapOf(
        "fun" to "function",
        "object" to "class"
    )

    init {

        registerAnnotation()
        registerFile()
        registerFunction()
        registerIdentifiers()
        registerType()
        registerClass()
        registerVariable()
        registerExpression()
        registerLiterals()
        registerLambda()
        registerControl()
//        registerStatement()

        handle<LeafPsiElement>(condition = { typedRule.text in terminalMap.keys }, priority = 1) {
            out.append(terminalMap[typedRule.text])
        }
    }
}