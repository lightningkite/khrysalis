package com.lightningkite.khrysalis.intellij

import com.intellij.openapi.diagnostic.Logger
import org.jetbrains.plugins.gradle.tooling.ErrorMessageBuilder
import org.jetbrains.plugins.gradle.tooling.ModelBuilderService
import java.io.File
import java.util.ArrayList

private fun psuedoLog(something: Any) {}// = File(System.getProperty("user.home")).resolve("altlog.txt").appendText(something.toString() + "\n")

class KhrysalisModelBuilderService: ModelBuilderService {

    init {
        psuedoLog("KhrysalisModelBuilderService initialized")
    }

    override fun canBuild(modelName: String?): Boolean {
        return KhrysalisGradleDependency::class.java.name == modelName
    }

    override fun buildAll(modelName: String?, project: org.gradle.api.Project): KhrysalisGradleDependency? = try {
        psuedoLog("Building ${modelName} for ${project.name}")
        project.configurations.findByName("equivalents")?.toList()?.also{
            psuedoLog("Looking up equivalents for subproject ${project.name}, found ${it.joinToString()}")
        }?.let { KhrysalisGradleDependencyImpl(ArrayList(it)) } ?: run {
            psuedoLog("Looking up equivalents for subproject ${project.name}, found none")
            null
        }
    } catch(e: Exception) {
        psuedoLog(e.message ?: "ERROR")
        throw e
    }

    override fun getErrorMessageBuilder(project: org.gradle.api.Project, e: Exception): ErrorMessageBuilder {
        return ErrorMessageBuilder.create(
            project, e, "KhrysalisGradleDependency error"
        ).withDescription("Unable to handle.")
    }
}