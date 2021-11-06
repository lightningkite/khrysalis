package com.lightningkite.khrysalis.kotlin

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.lightningkite.khrysalis.analysis.*
import com.lightningkite.khrysalis.replacements.*
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.isExtensionDeclaration
import org.jetbrains.kotlin.psi.psiUtil.isPublic
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.resolve.descriptorUtil.isPublishedApi
import org.jetbrains.kotlin.resolve.descriptorUtil.secondaryConstructors
import org.jetbrains.kotlin.resolve.jvm.extensions.AnalysisHandlerExtension
import java.io.File

class KotlinCLP : CommandLineProcessor {
    companion object {
        const val PLUGIN_ID = "com.lightningkite.khrysalis.kotlin"
    }

    override val pluginId: String
        get() = PLUGIN_ID

    override val pluginOptions: Collection<AbstractCliOption>
        get() = listOf()

    init {
        println("My plugin ID is $pluginId")
    }
}

class KotlinCR : ComponentRegistrar {
    override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration) {
        AnalysisHandlerExtension.registerExtension(project, KotlinExtension())
    }
}

class KotlinExtension(
) : AnalysisHandlerExtension {
    override fun analysisCompleted(
        project: Project,
        module: ModuleDescriptor,
        bindingTrace: BindingTrace,
        files: Collection<KtFile>
    ): AnalysisResult {
        bindingContext = bindingTrace.bindingContext
        val fqnFile = (
                project.projectFile?.let { File(it.path) }
                    ?: files.asSequence()
                        .map { it.virtualFilePath }
                        .reduce { acc, file -> acc.commonPrefixWith(file) }
                        .let { File(it) }
                        .let {
                            if(it.isDirectory) it
                            else it.parentFile
                        }
                ).resolve("equivalents-template.yaml")

        val toEmit = ArrayList<ReplacementRule>()
        files.asSequence()
            .filter { !it.name.contains("deprecated", true) }
            .flatMap { it.declarations }
            .filter { it.isPublic }
            .forEach { it.writeEquivalentTemplate(toEmit) }

        val replacementMapper = ObjectMapper(
            YAMLFactory().disable(YAMLGenerator.Feature.USE_NATIVE_TYPE_ID)
                .disable(YAMLGenerator.Feature.USE_NATIVE_OBJECT_ID)
        )
            .registerModule(object : JacksonReplacementsModule() {
                override fun parseImports(node: JsonNode): List<Import> {
                    return listOf()
                }
            })
            .registerModule(KotlinModule())
            .setSerializationInclusion(JsonInclude.Include.NON_DEFAULT)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        replacementMapper.writeValue(fqnFile, RuleList(toEmit))
        return AnalysisResult.Companion.success(bindingTrace.bindingContext, module, false)
    }
}

data class RuleList(val rules: List<ReplacementRule>)

fun KtDeclaration.writeEquivalentTemplate(toEmit: MutableList<ReplacementRule>) {
    val it = this
    when (it) {
        is KtClassOrObject -> it.resolvedClass?.writeEquivalentTemplate(toEmit)
        is KtProperty -> it.resolvedProperty?.writeEquivalentTemplate(toEmit)
        is KtTypeAlias -> it.resolvedTypeAlias?.writeEquivalentTemplate(toEmit)
        is KtFunction -> it.resolvedFunction?.writeEquivalentTemplate(toEmit)
    }
}
fun DeclarationDescriptorNonRoot.writeEquivalentTemplate(toEmit: MutableList<ReplacementRule>) {
    val it = this
    when (it) {
        is ClassDescriptor -> {
            val name = it.fqNameOrNull()?.asString() ?: return
            toEmit.add(
                TypeReplacement(
                    id = name,
                    template = Template.fromString(it.name.asString() + if(it.declaredTypeParameters.isNotEmpty()) it.declaredTypeParameters.joinToString(", ", "<", ">"){ "~T${it.index}~" } else "")
                )
            )
            it.unsubstitutedPrimaryConstructor?.let { c ->
                if(it.modality == Modality.ABSTRACT) return@let
                toEmit.add(
                    FunctionReplacement(
                        name,
                        template = Template.fromString("${it.name.asString()}(${c.valueParameters.filter { !it.name.isSpecial }.joinToString { "~${it.name}~" }})")
                    )
                )
            }
            it.secondaryConstructors.forEach { c ->
                if(it.modality == Modality.ABSTRACT) return@forEach
                toEmit.add(
                    FunctionReplacement(
                        name,
                        arguments = c.valueParameters.filter { !it.name.isSpecial }.map {
                            it.type.constructor.declarationDescriptor?.fqNameOrNull()?.asString() ?: "???"
                        },
                        template = Template.fromString("${it.name.asString()}(${c.valueParameters.filter { !it.name.isSpecial }.joinToString { "~${it.name}~" }})")
                    )
                )
            }
            it.unsubstitutedMemberScope.getContributedDescriptors()
                .mapNotNull { it as? MemberDescriptor }
                .filter { it.visibility.isPublicAPI }
                .filter {
                    when(it) {
                        is PropertyDescriptor -> it.overriddenDescriptors.isEmpty()
                        is FunctionDescriptor -> it.overriddenDescriptors.isEmpty()
                        else -> true
                    }
                }
                .forEach { m ->
                    m.writeEquivalentTemplate(toEmit)
                }
        }
        is PropertyDescriptor -> {
            val receiverType = it.extensionReceiverParameter?.type
            toEmit.add(
                GetReplacement(
                    it.fqNameOrNull()?.asString() ?: "???",
                    receiver = receiverType?.constructor?.declarationDescriptor?.fqNameOrNull()?.asString(),
                    template = if (it.extensionReceiverParameter != null || it.dispatchReceiverParameter != null)
                        Template.fromString("~this~.${it.name.asString()}")
                    else
                        Template.fromString(it.name.asString())
                )
            )
            if (it.isVar) {
                toEmit.add(
                    SetReplacement(
                        it.fqNameOrNull()?.asString() ?: "???",
                        receiver = receiverType?.constructor?.declarationDescriptor?.fqNameOrNull()
                            ?.asString(),
                        template = if (it.extensionReceiverParameter != null || it.dispatchReceiverParameter != null)
                            Template.fromString("~this~.${it.name.asString()} = ~value~")
                        else
                            Template.fromString("${it.name.asString()} = ~value~")
                    )
                )
            }
        }
        is FunctionDescriptor -> {
            toEmit.add(
                FunctionReplacement(
                    it.fqNameOrNull()?.asString() ?: return,
                    receiver = it.extensionReceiverParameter?.type?.constructor?.declarationDescriptor?.fqNameOrNull()
                        ?.asString(),
                    arguments = it.valueParameters.filter { !it.name.isSpecial }.map {
                        it.type.constructor.declarationDescriptor?.fqNameOrNull()?.asString() ?: "???"
                    },
                    template = if (it.extensionReceiverParameter != null || it.dispatchReceiverParameter != null)
                        Template.fromString("~this~.${it.name.asString()}(${it.valueParameters.filter { !it.name.isSpecial }.joinToString { "~${it.name}~" }})")
                    else
                        Template.fromString("${it.name.asString()}(${it.valueParameters.filter { !it.name.isSpecial }.joinToString { "~${it.name}~" }})")
                )
            )
        }
        is TypeAliasDescriptor -> {
            toEmit.add(
                TypeReplacement(
                    id = it.fqNameOrNull()?.asString() ?: return,
                    template = Template.fromString(it.name.asString() + if(it.declaredTypeParameters.isNotEmpty()) it.declaredTypeParameters.joinToString(", ", "<", ">"){ "~T${it.index}~" } else "")
                )
            )
        }
    }
}