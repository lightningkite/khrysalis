package com.lightningkite.khrysalis.intellij

import com.intellij.openapi.diagnostic.Logger
import org.jetbrains.plugins.gradle.tooling.ErrorMessageBuilder
import org.jetbrains.plugins.gradle.tooling.ModelBuilderService
import java.util.ArrayList

class KhrysalisModelBuilderService: ModelBuilderService {

    init {
        Logger.getInstance(this::class.java).warn("KhrysalisModelBuilderService initialized")
    }

    override fun canBuild(modelName: String?): Boolean {
        return KhrysalisGradleDependency::class.java.name == modelName
    }

    override fun buildAll(modelName: String?, project: org.gradle.api.Project): KhrysalisGradleDependency? {
        return project.configurations.findByName("equivalents")?.toList()?.also{
            Logger.getInstance(this::class.java).warn("Looking up equivalents for subproject ${project.name}, found ${it.joinToString()}")
        }?.let { KhrysalisGradleDependencyImpl(ArrayList(it)) } ?: run {
            Logger.getInstance(this::class.java).warn("Looking up equivalents for subproject ${project.name}, found none")
            null
        }
    }

    override fun getErrorMessageBuilder(project: org.gradle.api.Project, e: Exception): ErrorMessageBuilder {
        return ErrorMessageBuilder.create(
            project, e, "KhrysalisGradleDependency error"
        ).withDescription("Unable to handle.")
    }
}