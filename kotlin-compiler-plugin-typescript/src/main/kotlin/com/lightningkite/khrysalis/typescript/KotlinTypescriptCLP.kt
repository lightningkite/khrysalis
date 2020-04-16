package com.lightningkite.khrysalis.typescript

import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.psi.PsiReference
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import org.jetbrains.kotlin.container.ComponentProvider
import org.jetbrains.kotlin.context.ProjectContext
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.jvm.extensions.AnalysisHandlerExtension
import org.jetbrains.kotlin.types.asSimpleType
import java.io.File

class KotlinTypescriptCLP : CommandLineProcessor {
    companion object {
        const val KEY_ENABLED_NAME = "typescriptEnabled"
        val KEY_ENABLED = CompilerConfigurationKey.create<Boolean>(KEY_ENABLED_NAME)
        const val PLUGIN_ID = "com.lightningkite.khrysalis.typescript"
    }

    override val pluginId: String get() = PLUGIN_ID
    override val pluginOptions: Collection<AbstractCliOption> = listOf(
        CliOption(KEY_ENABLED_NAME, "<true|false>", "whether or not to do Typescript transpilation")
    )

    override fun processOption(option: AbstractCliOption, value: String, configuration: CompilerConfiguration) =
        when (option.optionName) {
            "enabled" -> configuration.put(KEY_ENABLED, value.toBoolean())
            else -> {
            }
        }
}

class KotlinTypescriptCR : ComponentRegistrar {
    override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration) {
        if (configuration[KotlinTypescriptCLP.KEY_ENABLED] == false) {
            return
        }
        AnalysisHandlerExtension.registerExtension(
            project,
            KotlinTypescriptExtension()
        )
    }
}

class KotlinTypescriptExtension() : AnalysisHandlerExtension {
    override fun analysisCompleted(
        project: Project,
        module: ModuleDescriptor,
        bindingTrace: BindingTrace,
        files: Collection<KtFile>
    ): AnalysisResult? {
        println("Completed analysis.")
        val ctx = bindingTrace.bindingContext
        with(AnalysisExtensions(ctx)){
            for (file in files) {
                for (decl in file.declarations) {
                    println("Declaration: " + decl.name)
                    if (decl is KtProperty) {
                        decl.initializer?.let {
                            println("Is a property with initializer of type ")
                            println(ctx.getType(it)?.getJetTypeFqName(true))
                        }
                        decl.typeReference?.let {
                            println("Is a property with declared type ")
                            ctx[BindingContext.TYPE, it]?.let {
                                println(it)
                                println(it.getJetTypeFqName(true))
                            } ?: run {
                                println("Could not resolve.")
                            }

                        }
                    }
                }
            }
        }

        return AnalysisResult.Companion.success(ctx, module, false)
    }
}

/* IDEA TIME!

ACTUALS SUPPORT

import fully.qualified.name.Receiver
import fully.qualified.name.Something

type Something<TypeArg, TypeArg2> = TypescriptThing<${TypeArg}>
call Receiver<TypeArg>.functionName(arg: Arg, arg2: Arg...) = someTsFunction(${this}, ${arg}, ${arg})
get Receiver<TypeArg>.propertyName = ${this}.otherNameInTs
set Receiver<TypeArg>.propertyName = ${this}.otherNameInTs = ${value}
call functionName(arg: Arg, arg2: Arg...) = someTsFunction(${this}, ${arg}, ${arg})
get propertyName = ${this}.otherNameInTs
set propertyName = ${this}.otherNameInTs = ${value}

call ... = something
    that
    spans
    multiple
    lines

add .Companion for static
#import("asdfasdf.asdfasd.asdf") allows for macro stuff
${temp} to create a temporary identifier

Comment and whitespace preservation: Try to translate at the lowest level possible to preserve

call kotlin.let -> asdfasdfasdfafd
call kotlin.let where ARG1 = x ->
 */
