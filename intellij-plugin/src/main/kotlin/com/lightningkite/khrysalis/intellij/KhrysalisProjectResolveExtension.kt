package com.lightningkite.khrysalis.intellij

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.externalSystem.model.DataNode
import com.intellij.openapi.externalSystem.model.Key
import com.intellij.openapi.externalSystem.model.project.ModuleData
import org.gradle.tooling.model.idea.IdeaModule
import org.jetbrains.plugins.gradle.service.project.AbstractProjectResolverExtension

class KhrysalisProjectResolveExtension: AbstractProjectResolverExtension() {
    companion object {
        val KEY = Key.create(
            KhrysalisGradleDependency::class.java, 252
        )
    }
    init {
        Logger.getInstance(this::class.java).warn("KhrysalisProjectResolveExtension initialized")
    }
    override fun getToolingExtensionsClasses(): Set<Class<*>> {
        return setOf(KhrysalisModelBuilderService::class.java, KhrysalisGradleDependency::class.java)
    }
    override fun getExtraProjectModelClasses(): Set<Class<*>> {
        return setOf(KhrysalisGradleDependency::class.java)
    }

    override fun populateModuleExtraModels(gradleModule: IdeaModule, ideModule: DataNode<ModuleData?>) {
        val model = resolverCtx.getExtraProject(gradleModule, KhrysalisGradleDependency::class.java)
        if (model != null) {
            ideModule.createChild(KhrysalisProjectResolveExtension.KEY, model)
            Logger.getInstance(this::class.java).warn("Writing ${model.files.joinToString()} to ${ideModule.data.ideGrouping}")
        }
        super.populateModuleExtraModels(gradleModule, ideModule)
    }
}