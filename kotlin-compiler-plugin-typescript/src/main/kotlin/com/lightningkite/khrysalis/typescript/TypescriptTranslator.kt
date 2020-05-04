package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.generic.PartialTranslatorByType
import com.lightningkite.khrysalis.generic.TranslatorInterface
import com.lightningkite.khrysalis.typescript.replacements.Replacements
import com.lightningkite.khrysalis.util.AnalysisExtensions
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.scopes.receivers.ExtensionReceiver

class TypescriptTranslator(
    override val bindingContext: BindingContext,
    val collector: MessageCollector? = null,
    val replacements: Replacements = Replacements()
) : PartialTranslatorByType<TypescriptFileEmitter, Unit, Any>(), TranslatorInterface<TypescriptFileEmitter, Unit>, AnalysisExtensions {

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
        _receiverStack.add(newItem)
        action(tsName)
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

    override fun emitFinalDefault(identifier: Class<*>, rule: Any, out: TypescriptFileEmitter) {
        when (rule) {
            is Array<*> -> rule.forEach { if(it != null) translate(it, out) }
            is Iterable<*> -> rule.forEach { if(it != null) translate(it, out) }
            is Sequence<*> -> rule.forEach { if(it != null) translate(it, out) }
            is Char -> out.append(rule)
            is String -> out.append(rule)
            is PsiWhiteSpace -> {
                out.append(rule.text)
                return
            }
            is LeafPsiElement -> {
                out.append(rule.text)
                return
            }
            is PsiElement -> rule.allChildren.forEach { translate(it, out) }
        }
    }

    val terminalMap = mapOf(
        "fun" to "function",
        "object" to "class",
        "vararg" to "..."
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
        registerOperators()
        registerReceiver()
//        registerStatement()

        handle<LeafPsiElement>(condition = { typedRule.text in terminalMap.keys }, priority = 1) {
            out.append(terminalMap[typedRule.text])
        }
    }
}

