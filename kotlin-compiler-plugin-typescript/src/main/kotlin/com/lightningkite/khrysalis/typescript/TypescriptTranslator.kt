package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.generic.PartialTranslatorByType
import com.lightningkite.khrysalis.generic.TranslatorInterface
import com.lightningkite.khrysalis.typescript.replacements.Replacements
import com.lightningkite.khrysalis.util.AnalysisExtensions
import com.lightningkite.khrysalis.util.simpleFqName
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtThisExpression
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.scopes.receivers.ExtensionReceiver
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class TypescriptTranslator(
    val projectName: String?,
    override val bindingContext: BindingContext,
    val commonPath: String,
    val collector: MessageCollector? = null,
    val replacements: Replacements = Replacements()
) : PartialTranslatorByType<TypescriptFileEmitter, Unit, Any>(), TranslatorInterface<TypescriptFileEmitter, Unit>, AnalysisExtensions {

    val declarations: DeclarationManifest = DeclarationManifest()

    var stubMode: Boolean = false
    @Deprecated("NO.  Go to your room.") val kotlinFqNameToFile get() = declarations.node
    @Deprecated("NO.  Go to your room.") val kotlinFqNameToRelativeFile get() = declarations.local

    data class ReceiverAssignment(val declaration: DeclarationDescriptor, val tsName: String)

    val _receiverStack = ArrayList<ReceiverAssignment>()
    val receiverStack: List<ReceiverAssignment> get() = _receiverStack
    inline fun withReceiverScope(
        descriptor: DeclarationDescriptor,
        suggestedName: String = "this_",
        action: (newIdentifier: String) -> Unit
    ) {
        var currentNumber = 0
        var tsName = suggestedName
        while (_receiverStack.any { it.tsName == tsName }) {
            currentNumber++
            tsName = suggestedName + currentNumber
        }

        val newItem = ReceiverAssignment(descriptor, tsName)
        _receiverStack.add(newItem)
        action(tsName)
        _receiverStack.remove(newItem)
    }
    fun KtExpression.getTsReceiver(): String? {
        val dr = this.resolvedCall?.dispatchReceiver ?: this.resolvedCall?.extensionReceiver ?: run {
            return null
        }
        val target = if(dr is ExtensionReceiver) {
            dr.declarationDescriptor
        } else {
            dr.type.constructor.declarationDescriptor
        }
        val entry = receiverStack.lastOrNull { it.declaration == target }
        return entry?.tsName ?: "this"
    }

    fun KtThisExpression.getTsReceiver(): String? {
        val target = this.instanceReference.resolvedReferenceTarget
        val entry = receiverStack.lastOrNull { it.declaration == target }
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
            is Function0<*> -> rule.invoke()
        }
    }

    override fun translate(identifier: Class<*>, rule: Any, out: TypescriptFileEmitter, afterPriority: Int) {
//        if(rule is KtExpression){
//            out.append("/*${rule.resolvedUsedAsExpression}*/")
//        }
        super.translate(identifier, rule, out, afterPriority)
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

        handle<LeafPsiElement>(condition = { typedRule.text in terminalMap.keys }, priority = 1) {
            out.append(terminalMap[typedRule.text])
        }
    }
}

