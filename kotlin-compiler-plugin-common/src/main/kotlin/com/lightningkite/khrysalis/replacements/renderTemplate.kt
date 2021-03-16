package com.lightningkite.khrysalis.replacements

import com.lightningkite.khrysalis.generic.*
import com.lightningkite.khrysalis.util.forEachBetween
import org.jetbrains.kotlin.lexer.KtToken
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.types.KotlinType

interface TemplateRendering {
    val receiver: TemplateInfoPart<KtExpression>
    val dispatchReceiver: TemplateInfoPart<KtExpression>
    val extensionReceiver: TemplateInfoPart<KtExpression>
    val value: TemplateInfoPart<KtExpression>
    fun allParameters(out: FileEmitter) {}
    val operatorToken: TemplateInfoPart<KtToken>
    fun parameters(name: String): TemplateInfoPart<KtExpression>
    fun parameters(index: Int): TemplateInfoPart<KtExpression>
    fun typeParameters(name: String): TemplateInfoPart<KtTypeProjection>
    fun typeParameters(index: Int): TemplateInfoPart<KtTypeProjection>

    fun block(out: FileEmitter, body: DeclarationSet.()->Unit)
    fun declare(out: FileEmitter, name: String, context: TemplateInfoPart<KtExpression>)
    fun writeBody(out: FileEmitter, expression: KtBlockExpression)
    interface DeclarationSet {
        fun declareName(context: TemplateInfoPart<KtExpression>): String
    }
}

interface TemplateInfoPart<KtBase> {
    val source: KtBase? get() = null
    val type: KotlinType? get() = null
    fun write(out: FileEmitter)
    object Empty: TemplateInfoPart<Any> {
        override fun write(out: FileEmitter) {}
    }
    companion object {
        @Suppress("UNCHECKED_CAST")
        fun <T> empty() = Empty as TemplateInfoPart<T>
    }
}

private class TemplateInfoPartDeduplicated(val name: String, val original: TemplateInfoPart<KtExpression>): TemplateInfoPart<KtExpression> by original {
    override fun write(out: FileEmitter) {
        out.append(name)
    }
}

private fun TemplateRendering.deduplicate(out: FileEmitter, template: Template, action: TemplateRendering.()->Unit) {
    val duplicatedExpressions = template.allParts
        .filter { it is TemplatePart.Expression }
        .distinct()
        .associateWith { other -> template.parts.count { it == other } }
        .filterValues { it > 1 }
    block(out){
        var nameReceiver: TemplateInfoPart<KtExpression>? = null
        var nameDispatchReceiver: TemplateInfoPart<KtExpression>? = null
        var nameExtensionReceiver: TemplateInfoPart<KtExpression>? = null
        var nameValue: TemplateInfoPart<KtExpression>? = null
        val nameParametersByName = HashMap<String, TemplateInfoPart<KtExpression>>()
        val nameParametersByIndex = HashMap<Int, TemplateInfoPart<KtExpression>>()
        fun make(info: TemplateInfoPart<KtExpression>): TemplateInfoPartDeduplicated {
            val name = declareName(info)
            return TemplateInfoPartDeduplicated(name, info)
        }
        for(part in duplicatedExpressions.keys){
            when(part){
                TemplatePart.Receiver -> { nameReceiver = make(receiver) }
                TemplatePart.DispatchReceiver -> { nameDispatchReceiver = make(dispatchReceiver) }
                TemplatePart.ExtensionReceiver -> { nameExtensionReceiver = make(extensionReceiver) }
                TemplatePart.Value -> { nameValue = make(value) }
                is TemplatePart.Parameter -> { nameParametersByName[part.name] = make(parameters(part.name)) }
                is TemplatePart.ParameterByIndex -> { nameParametersByIndex[part.index] = make(parameters(part.index)) }
            }
        }
        val obj = object: TemplateRendering by this@deduplicate {
            override val receiver: TemplateInfoPart<KtExpression> get() = nameReceiver ?: this@deduplicate.receiver
            override val dispatchReceiver: TemplateInfoPart<KtExpression> get() = nameDispatchReceiver ?: this@deduplicate.dispatchReceiver
            override val extensionReceiver: TemplateInfoPart<KtExpression> get() = nameExtensionReceiver ?: this@deduplicate.extensionReceiver
            override val value: TemplateInfoPart<KtExpression> get() = nameValue ?: this@deduplicate.value
            override fun parameters(name: String): TemplateInfoPart<KtExpression> = nameParametersByName[name] ?: this@deduplicate.parameters(name)
            override fun parameters(index: Int): TemplateInfoPart<KtExpression> = nameParametersByIndex[index] ?: this@deduplicate.parameters(index)
        }
        action(obj)
    }
}

fun TemplateRendering.renderDeduplicate(out: FileEmitter, template: Template) {
    deduplicate(out, template) {
        render(out, template)
    }
}

fun TemplateRendering.render(out: FileEmitter, template: Template){
    template.imports.forEach { out.addImport(it) }
    for (part in template.parts) {
        when (part) {
            is TemplatePart.Text -> out.append(part.string)
            TemplatePart.Receiver -> receiver.write(out)
            TemplatePart.DispatchReceiver -> dispatchReceiver.write(out)
            TemplatePart.ExtensionReceiver -> extensionReceiver.write(out)
            TemplatePart.Value -> value.write(out)
            TemplatePart.AllParameters -> allParameters(out)
            TemplatePart.OperatorToken -> operatorToken.write(out)
            is TemplatePart.Parameter -> parameters(part.name).write(out)
            is TemplatePart.ParameterByIndex -> parameters(part.index).write(out)
            is TemplatePart.TypeParameter -> typeParameters(part.name).write(out)
            is TemplatePart.TypeParameterByIndex -> typeParameters(part.index).write(out)
            is TemplatePart.LambdaParameterContents -> {
                out.append("\n")
                val item = when (val p = part.pointer) {
                    is TemplatePart.ParameterByIndex -> parameters(p.index)
                    is TemplatePart.Parameter -> parameters(p.name)
                    else -> continue
                }
                (item.source as? KtLambdaExpression)?.let { item ->
                    part.paramMap.zip(item.valueParameters.map { it.name }.takeUnless { it.isEmpty() }
                        ?: listOf("it")).forEach {
                        declare(out, it.second!!, object : TemplateInfoPart<KtExpression> {
                            override fun write(out: FileEmitter) {
                                render(out, it.first)
                            }
                        })
                        out.append("\n")
                    }
                    writeBody(out, item.bodyExpression!!)
                } ?: run {
                    item.write(out)
                    out.append("(")
                    part.paramMap.forEachBetween(forItem = {
                        render(out, it)
                    }, between = {
                        out.append(", ")
                    })
                    out.append(")")
                }
                out.append("\n")
            }
            is TemplatePart.Expression -> TODO()
        }
    }
}
