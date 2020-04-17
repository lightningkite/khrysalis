package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.generic.PartialTranslatorByType
import com.lightningkite.khrysalis.generic.TranslatorInterface
import com.lightningkite.khrysalis.util.AnalysisExtensions
import org.jetbrains.kotlin.com.intellij.lang.PsiParser
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafElement
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.psiUtil.getElementTextWithContext
import org.jetbrains.kotlin.psi.psiUtil.getTextWithLocation
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.isFakePsiElement
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.scopes.receivers.ExtensionReceiver
import java.util.*
import kotlin.collections.ArrayList
import kotlin.text.Appendable

class TypescriptTranslator(override val bindingContext: BindingContext) :
    PartialTranslatorByType<Appendable, Unit, Any>(), TranslatorInterface<Appendable, Unit>, AnalysisExtensions {

    data class ReceiverAssignment(val fqName: String, val tsName: String)

    val _receiverStack = ArrayList<ReceiverAssignment>()
    val receiverStack: List<ReceiverAssignment> get() = _receiverStack
    inline fun withReceiverScope(
        fqName: String,
        suggestedName: String = "this_${fqName.substringAfterLast('.').capitalize()}",
        action: (newIdentifier: String) -> Unit
    ) {
        var currentNumber = 0
        var tsName = suggestedName
        while (_receiverStack.any { it.tsName == tsName }) {
            currentNumber++
            tsName = suggestedName + currentNumber
        }

        val newItem = ReceiverAssignment(fqName, tsName)
        println("Start scope with $newItem")
        _receiverStack.add(newItem)
        action(tsName)
        println("End scope with $newItem")
        _receiverStack.remove(newItem)
    }
    fun KtExpression.getTsReceiver(): String? {
        val dr = this.resolvedCall?.dispatchReceiver ?: this.resolvedCall?.extensionReceiver ?: run {
            println("Found no receiver for ${this.text}, despite it being requested")
            return null
        }
        val fq = if(dr is ExtensionReceiver) {
            dr.declarationDescriptor.fqNameSafe.asString()
        } else {
            "real"
        }
        val entry = receiverStack.lastOrNull { it.fqName == fq }
        return entry?.tsName ?: "this"
    }

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
        registerReceiver()
//        registerStatement()

        handle<LeafPsiElement>(condition = { typedRule.text in terminalMap.keys }, priority = 1) {
            out.append(terminalMap[typedRule.text])
        }
    }
}