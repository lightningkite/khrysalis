package com.lightningkite.khrysalis.gradle

import com.lightningkite.khrysalis.utils.groovyObject
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.SourceSetContainer
import org.jetbrains.kotlin.gradle.plugin.FilesSubpluginOption
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption
import org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompile
import org.jetbrains.kotlin.gradle.tasks.CompilerPluginOptions
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilerPluginData
import java.io.File
import java.util.zip.ZipFile

internal fun DependencyHandler.kcp(dependencyNotation: Any): Dependency? {
    return this.add("kcp", dependencyNotation)
}

internal fun DependencyHandler.equivalents(dependencyNotation: Any): Dependency? {
    return this.add("equivalents", dependencyNotation)
}

private val propForcePluginOptions by lazy {
    AbstractKotlinCompile::class.java.declaredFields
        .find { it.name.contains("kotlinPluginData") && it.type == Provider::class.java }!!
        .also { it.trySetAccessible() }
}
internal val AbstractKotlinCompile<*>.forcePluginOptions: CompilerPluginOptions
    get(){
        return (propForcePluginOptions.get(this) as? Provider<KotlinCompilerPluginData>)?.get()?.options ?: CompilerPluginOptions()
    }

internal fun AbstractKotlinCompile<*>.plugin(pluginName: String, build: SubpluginOptionsBuilder.()->Unit) {
    val allRootDeps = this.project.configurations.maybeCreate("kcp")
        .resolvedConfiguration
        .firstLevelModuleDependencies
    val rootDep = allRootDeps
        .find { it.moduleName.contains(pluginName, true) } ?: throw IllegalStateException("No plugin found with name '$pluginName' in it.  Available: ${allRootDeps.joinToString { it.name }}")
    val pluginRawJar = rootDep.moduleArtifacts.find { it.classifier.isNullOrEmpty() && it.extension == "jar" } ?: throw IllegalStateException("Could not find JAR for '${rootDep.moduleName}'")
    val pluginId = ZipFile(pluginRawJar.file).use { zipFile ->
        zipFile.getEntry("META-INF/services/com.lightningkite.kotlin.kcpgradle.PluginName")?.let {
            zipFile.getInputStream(it).readAllBytes().toString(Charsets.UTF_8)
        } ?: zipFile.getEntry("META-INF/services/org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor")?.let {
            zipFile.getInputStream(it).readAllBytes().toString(Charsets.UTF_8).substringBeforeLast('.')
        }
    } ?: throw IllegalStateException("Could not find plugin ID in JAR for ${rootDep.moduleName}")
    rootDep.allModuleArtifacts
        .filter { it.classifier.isNullOrEmpty() && it.extension == "jar" }
        .forEach { pluginClasspath.from(it.file) }
    SubpluginOptionsBuilder(forcePluginOptions, pluginId).apply(build)
}

internal class SubpluginOptionsBuilder(val options: CompilerPluginOptions, val pluginId: String) {
    infix fun String.set(value: String) = options.addPluginArgument(pluginId, SubpluginOption(this, value))
    infix fun String.set(files: List<File>) = options.addPluginArgument(pluginId, FilesSubpluginOption(this, files))
}

val Project.sourceSetsMaybeAndroid: NamedDomainObjectContainer<*>
    get() =
        (project.extensions.findByName("android")?.groovyObject?.getProperty("sourceSets") as? NamedDomainObjectContainer<*>) ?:
        ((this as org.gradle.api.plugins.ExtensionAware).extensions.getByName("sourceSets") as NamedDomainObjectContainer<*>)
