package com.lightningkite.khrysalis.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.tasks.SourceSet
import org.gradle.language.base.plugins.LanguageBasePlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val SourceSet.equivalents: SourceDirectorySet get() = this.extensions.getByName("equivalents") as SourceDirectorySet

fun Project.duplicate(task: KotlinCompile, newName: String): KotlinCompile = tasks.create(newName, task::class.java) { newTask ->
    task::class.java.fields.forEach {
        if(it.trySetAccessible()) {
            it.set(newTask, it.get(task))
        }
    }
}