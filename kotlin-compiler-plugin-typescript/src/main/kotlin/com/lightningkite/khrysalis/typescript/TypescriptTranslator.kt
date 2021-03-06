package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.generic.PartialTranslatorByType
import com.lightningkite.khrysalis.generic.TranslatorInterface
import com.lightningkite.khrysalis.typescript.replacements.Replacements
import com.lightningkite.khrysalis.util.AnalysisExtensions
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.ValueDescriptor
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import org.jetbrains.kotlin.psi.psiUtil.containingClass
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.scopes.receivers.ExtensionReceiver
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
    @Deprecated("NO.  Go to your room.", ReplaceWith("this.declarations.node")) val kotlinFqNameToFile get() = declarations.node
    @Deprecated("NO.  Go to your room.", ReplaceWith("this.declarations.local")) val kotlinFqNameToRelativeFile get() = declarations.local

    data class ReceiverAssignment(val declaration: DeclarationDescriptor, val tsName: String)

    val identifierMappings = HashMap<DeclarationDescriptor, String>()
    inline fun withName(ktName: DeclarationDescriptor, tsName: String, action: (String)->Unit){
        identifierMappings[ktName] = tsName
        action(tsName)
        identifierMappings.remove(ktName)
    }

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
        val dr = this.resolvedCall?.dispatchReceiver ?: this.resolvedCall?.extensionReceiver ?: run {
            return null
        }
        val target = if(dr is ExtensionReceiver) {
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
    override fun emitDefault(identifier: Class<*>, rule: Any, out: TypescriptFileEmitter): Unit {
        return identifier.superclass?.let {
            translate(it, rule, out)
        } ?: emitFinalDefault(identifier, rule, out)
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
        registerSpecialLet()
        registerJUnit()

        handle<LeafPsiElement>(condition = { typedRule.text in terminalMap.keys }, priority = 1) {
            out.append(terminalMap[typedRule.text])
        }
    }


    fun KtExpression.isSafeLetDirect(): Boolean {
        if (this !is KtSafeQualifiedExpression) return false
        val callExpression = this.selectorExpression as? KtCallExpression ?: return false
        if (callExpression.lambdaArguments.isEmpty()) return false
        if ((callExpression.calleeExpression as? KtReferenceExpression)?.resolvedReferenceTarget?.fqNameSafe?.asString() != "kotlin.let") return false
        return true
    }

    fun KtExpression.isRunDirect(): Boolean {
        if (this !is KtCallExpression) return false
        if (this.lambdaArguments.isEmpty()) return false
        if ((this.calleeExpression as? KtReferenceExpression)?.resolvedReferenceTarget?.fqNameSafe?.asString() != "kotlin.run") return false
        return true
    }

    fun KtBinaryExpression.isSafeLetChain(): Boolean {
        if (this.operationToken != KtTokens.ELVIS) return false
        if (this.left?.isSafeLetDirect() == true) return true
        return (this.left as? KtBinaryExpression)?.isSafeLetChain() == true
    }

    fun KtBinaryExpression.safeLetChainRoot(): KtBinaryExpression {
        (this.parent as? KtBinaryExpression)?.let { p ->
            if (p.left == this) {
                if (p.operationToken == KtTokens.ELVIS) {
                    return p.safeLetChainRoot()
                }
            }
        }
        return this
    }

    fun KtExpression.needsSemi(): Boolean = this !is KtDeclaration
            && this !is KtLoopExpression
            && this !is KtBlockExpression
            && this !is KtIfExpression
            && this !is KtTryExpression
            && this !is KtWhenExpression
            && !this.isSafeLetDirect()
            && ((this as? KtBinaryExpression)?.isSafeLetChain() != true)

    inline fun <reified T> PsiElement.parentIfType(): T? = parent as? T
    override fun determineMaybeExpressionLambda(it: KtFunctionLiteral): Boolean {
        if(!super.determineMaybeExpressionLambda(it)) {
            return false
        }
        if(it
                .parentIfType<KtLambdaExpression>()
                ?.let {
                    it.parentIfType<KtLambdaArgument>() ?: it.parentIfType<KtAnnotatedExpression>()?.parentIfType<KtLambdaArgument>()
                }
                ?.parentIfType<KtCallExpression>()
                ?.let {
                    if((it.parent as? KtExpression)?.let {
                            it.isSafeLetDirect() && !determineMaybeExpression(it)
                        } == true){
                        return false
                    }
                    it.parentIfType<KtBinaryExpression>()
                        ?: it.parentIfType<KtQualifiedExpression>()
                            ?.parentIfType<KtBinaryExpression>()
                }
                ?.let { it.isSafeLetChain() && !determineMaybeExpression(it.safeLetChainRoot()) } == true
        ){
            return false
        }
        return true
    }
}

