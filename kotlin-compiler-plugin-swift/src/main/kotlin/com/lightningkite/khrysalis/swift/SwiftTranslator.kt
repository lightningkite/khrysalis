package com.lightningkite.khrysalis.swift

import com.lightningkite.khrysalis.generic.PartialTranslatorByType
import com.lightningkite.khrysalis.generic.TranslatorInterface
import com.lightningkite.khrysalis.analysis.*
import com.lightningkite.khrysalis.replacements.Replacements
import com.lightningkite.khrysalis.util.fqNameWithTypeArgs
import com.lightningkite.khrysalis.util.fqNameWithoutTypeArgs
import com.lightningkite.khrysalis.util.walkTopDown
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.ValueDescriptor
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.scopes.receivers.ExtensionReceiver
import org.jetbrains.kotlin.types.KotlinType
import java.util.*
import kotlin.collections.ArrayList

class SwiftTranslator(
    val projectName: String?,
    val commonPath: String,
    val collector: MessageCollector? = null,
    val replacements: Replacements
) : PartialTranslatorByType<SwiftFileEmitter, Unit, Any>(), TranslatorInterface<SwiftFileEmitter, Unit>{

    val fqToImport = HashMap<String, String>()

    var stubMode: Boolean = false

    data class ReceiverAssignment(val declaration: DeclarationDescriptor, val tsName: String)

    val _identifierScopes = HashMap<String, ArrayList<String>>()
    val identifierScopes: Map<String, List<String>> get() = _identifierScopes
    inline fun withName(ktName: String, tsName: String, action: (String) -> Unit) {
        val list = _identifierScopes.getOrPut(ktName) { ArrayList() }
        list.add(tsName)
        action(tsName)
        list.removeAt(list.lastIndex)
        if (list.isEmpty()) {
            _identifierScopes.remove(ktName)
        }
    }

    private val ignoreSmartcast = ArrayList<HashSet<ValueDescriptor>>()
    fun beginSmartcastBlock() = ignoreSmartcast.add(HashSet())
    fun endSmartcastBlock() = ignoreSmartcast.removeAt(ignoreSmartcast.lastIndex)
    fun ignoreSmartcast(v: ValueDescriptor) = ignoreSmartcast.lastOrNull()?.add(v)
    fun isSmartcastIgnored(v: ValueDescriptor?) = v != null && ignoreSmartcast.any { it.contains(v) }

    val _receiverStack = ArrayList<ReceiverAssignment>()
    val receiverStack: List<ReceiverAssignment> get() = _receiverStack
    inline fun withReceiverScope(
        descriptor: DeclarationDescriptor,
        suggestedName: String = "this",
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
        val target = if (dr is ExtensionReceiver) {
            dr.declarationDescriptor
        } else {
            dr.type.constructor.declarationDescriptor
        }
        val entry = receiverStack.lastOrNull { it.declaration == target }
        return entry?.tsName ?: "self"
    }

    fun KtThisExpression.getTsReceiver(): String? {
        val target = this.instanceReference.resolvedReferenceTarget
        val entry = receiverStack.lastOrNull { it.declaration == target }
        return entry?.tsName ?: "self"
    }

    override fun emitFinalDefault(identifier: Class<*>, rule: Any, out: SwiftFileEmitter) {
        when (rule) {
            is Array<*> -> rule.forEach { if (it != null) translate(it, out) }
            is Iterable<*> -> rule.forEach { if (it != null) translate(it, out) }
            is Sequence<*> -> rule.forEach { if (it != null) translate(it, out) }
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

    override fun translate(identifier: Class<*>, rule: Any, out: SwiftFileEmitter, afterPriority: Int) {
//        if(rule is KtExpression){
//            out.append("/*${rule.resolvedUsedAsExpression}*/")
//        }
        super.translate(identifier, rule, out, afterPriority)
    }
    override fun emitDefault(identifier: Class<*>, rule: Any, out: SwiftFileEmitter): Unit {
        return identifier.superclass?.let {
            translate(it, rule, out)
        } ?: emitFinalDefault(identifier, rule, out)
    }

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
        registerSpecialLet()
        registerQualified()
        registerException()
    }

    inline fun <reified T> PsiElement.parentIfType(): T? = parent as? T

    fun KotlinType.requiresMutable(): Boolean {
        return replacements.requiresMutable(this)
    }

    fun PartialTranslatorByType<SwiftFileEmitter, Unit, Any>.ContextByType<*>.runWithTypeHeader(
        on: KtExpression
    ) {
        val type = on.resolvedExpressionTypeInfo?.type
        if (type?.fqNameWithoutTypeArgs == "kotlin.Nothing") {
            //find type via returns
            on.walkTopDown()
                .mapNotNull { it as? KtReturnExpression }
                .mapNotNull { it.getTargetLabel()?.resolvedLabelTarget }
                .mapNotNull { it as? KtFunctionLiteral }
                .firstOrNull()
                ?.resolvedFunction
                ?.returnType
                ?.let { type ->
                    -"run {"
                    -" () -> "
                    -type
                    -" in \n"
                } ?: run {
                -"run { \n"
            }
        } else if (type != null) {
            -"run {"
            -" () -> "
            -type
            -" in \n"
        } else {
            -"run { /*!*/ \n"
        }
    }
}
