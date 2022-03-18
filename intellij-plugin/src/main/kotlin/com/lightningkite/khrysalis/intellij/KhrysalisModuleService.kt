package com.lightningkite.khrysalis.intellij

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.externalSystem.model.ProjectKeys
import com.intellij.openapi.externalSystem.model.project.ModuleData
import com.intellij.openapi.externalSystem.service.project.ProjectDataManager
import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.lightningkite.khrysalis.replacements.Replacements
import com.lightningkite.khrysalis.swift.KotlinSwiftCR
import com.lightningkite.khrysalis.swift.replacements.SwiftJacksonReplacementsModule
import com.lightningkite.khrysalis.typescript.KotlinTypescriptCLP
import com.lightningkite.khrysalis.typescript.KotlinTypescriptCR
import com.lightningkite.khrysalis.typescript.replacements.TypescriptJacksonReplacementsModule
import org.jetbrains.kotlin.idea.caches.project.externalProjectId
import org.jetbrains.plugins.gradle.util.GradleConstants
import java.io.File

@Service
class KhrysalisModuleService(val project: Project) : /*PersistentStateComponent<>,*/ com.intellij.openapi.Disposable {
    companion object {
        internal var dirtyStamp = System.currentTimeMillis()
        internal fun markDirty() { dirtyStamp = System.currentTimeMillis() }
        fun getInstance(project: Project): KhrysalisModuleService =
            project.getService(KhrysalisModuleService::class.java)
    }

    inner class ForGradleModule(val module: ModuleData) {
        var swift: Replacements = Replacements(jacksonObjectMapper().registerModule(SwiftJacksonReplacementsModule()) )
            private set
        var typescript: Replacements = Replacements(jacksonObjectMapper().registerModule(TypescriptJacksonReplacementsModule()) )
            private set
        fun load(equivalents: List<File>) {
            swift = Replacements(KotlinSwiftCR.replacementMapper)
            typescript = Replacements(KotlinTypescriptCR.replacementMapper)

            for(eq in equivalents) {
                swift.load(eq, "swift", project.name)
                typescript.load(eq, "swift", project.name)
            }
        }
    }

    private var populated: Long? = null
    private val _modules = HashMap<String, ForGradleModule>()
    val modules: Map<String, ForGradleModule> get() {
        val lastPopulated = populated
        if(lastPopulated == null || lastPopulated < dirtyStamp) {
            populated = System.currentTimeMillis()
            populate()
        }
        return _modules
    }

    private fun populate() {
        val projectsData = ProjectDataManager.getInstance().getExternalProjectsData(project, GradleConstants.SYSTEM_ID)
        for (projectInfo in projectsData) {
            val projectDataNode = projectInfo.externalProjectStructure ?: continue
            try {
                for (moduleNode in ExternalSystemApiUtil.findAll(projectDataNode, ProjectKeys.MODULE)) {
                    ExternalSystemApiUtil.find(
                        moduleNode!!, KhrysalisProjectResolveExtension.KEY
                    )?.data?.files?.let {
                        _modules.getOrPut(moduleNode.data.ideGrouping ?: "-") { ForGradleModule(moduleNode.data) }.load(it)
                    }
                }
            } catch (e: ClassCastException) {
                // catch deserialization issue caused by fast serializer
                Logger.getInstance(this::class.java).debug(e)
            }
        }
        Logger.getInstance(this::class.java).warn("Khrysalis data populated.  Available modules: ${_modules.keys.joinToString()}")
    }

    override fun dispose() {
    }

}

val PsiElement.khrysalisModule: KhrysalisModuleService.ForGradleModule? get() = module?.let { m ->
    KhrysalisModuleService.getInstance(project).modules[m.gradleName.also {
        Logger.getInstance("com.lightningkite.khrysalis").warn("Looking up module $it")
    }]
}