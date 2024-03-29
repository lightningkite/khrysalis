package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.analysis.*
import com.lightningkite.khrysalis.generic.KotlinTranslator
import com.lightningkite.khrysalis.replacements.Replacements
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import org.jetbrains.kotlin.psi.psiUtil.containingClass
import org.jetbrains.kotlin.resolve.calls.model.VariableAsFunctionResolvedCall
import org.jetbrains.kotlin.resolve.calls.util.getImplicitReceiverValue
import org.jetbrains.kotlin.resolve.scopes.receivers.ExtensionReceiver
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class TypescriptTranslator(
    val projectName: String?,
    val commonPackage: String?,
    val outputDirectory: File,
    val collector: MessageCollector? = null,
    val replacements: Replacements
) : KotlinTranslator<TypescriptFileEmitter>() {

    val declarations: DeclarationManifest = DeclarationManifest(outputDirectory, commonPackage)

    var stubMode: Boolean = false

    val identifierMappings = HashMap<DeclarationDescriptor, String>()
    inline fun withName(ktName: DeclarationDescriptor, tsName: String, action: (String)->Unit){
        identifierMappings[ktName] = tsName
        action(tsName)
        identifierMappings.remove(ktName)
    }

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
    fun KtExpression.getTsReceiver(): Any? {
        val resolved = resolvedCall ?: return null
        val dr = when(resolved){
            is VariableAsFunctionResolvedCall -> resolved.variableCall.getImplicitReceiverValue() ?: resolved.extensionReceiver ?: resolved.dispatchReceiver
            else -> resolved.getImplicitReceiverValue() ?: resolved.extensionReceiver ?: resolved.dispatchReceiver
        } ?: return null
        val target = if (dr is ExtensionReceiver) {
            dr.declarationDescriptor
        } else {
            dr.type.constructor.declarationDescriptor
        }
        val entry = receiverStack.lastOrNull { it.declaration == target }
        return entry?.tsName ?: run {
            val targetDescriptor = dr.type.constructor.declarationDescriptor as? ClassDescriptor
                    ?: return "this"
            if(targetDescriptor.isCompanionObject
                    && targetDescriptor != this.parentOfType<KtObjectDeclaration>()?.resolvedClass) {
                return listOf(this.containingClass()?.nameIdentifier, ".Companion.INSTANCE")
            } else {
                return "this"
            }
        }
    }

    fun KtThisExpression.getTsReceiver(): Any? {
        val target = this.instanceReference.resolvedReferenceTarget
        val entry = receiverStack.lastOrNull { it.declaration == target }
        return entry?.tsName ?: run {
            val targetDescriptor = target as? ClassDescriptor ?: return "this"
            if(targetDescriptor.isCompanionObject
                && targetDescriptor != this.containingClass()?.resolvedClass) {
                return listOf(this.containingClass()?.nameIdentifier, ".Companion.INSTANCE")
            } else {
                return "this"
            }
        }
    }

    override fun emitFinalDefault(rule: Any, out: TypescriptFileEmitter) {
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

    val terminalMap = mapOf(
        "fun" to "function",
        "object" to "class",
        "vararg" to "..."
    )

    init {
        registerReflection()
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
        registerSpecialLet()
        registerJUnit()
        registerViewBinding()
        registerCast()
        registerSerializer()
        registerResources()

        handle<LeafPsiElement>(condition = { typedRule.text in terminalMap.keys }, priority = 1) {
            out.append(terminalMap[typedRule.text])
        }
    }

    fun KtExpression.needsSemi(): Boolean = this !is KtDeclaration
            && this !is KtLoopExpression
            && this !is KtBlockExpression
            && this !is KtIfExpression
            && this !is KtTryExpression
            && this !is KtWhenExpression
            && !this.isSafeLetDirect()
            && ((this as? KtBinaryExpression)?.isSafeLetChain() != true)

}

