package com.lightningkite.khrysalis.gradle

import com.android.build.gradle.api.AndroidSourceSet
import org.gradle.api.Named
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.tasks.SourceSet
import org.gradle.language.base.plugins.LanguageBasePlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*

val SourceSet.equivalents: SourceDirectorySet get() = this.extensions.getByName("equivalents") as SourceDirectorySet
internal val equivalentMap = WeakHashMap<Any, SourceDirectorySet>()
var AndroidSourceSet.equivalents: SourceDirectorySet
    get() = equivalentMap[this] as SourceDirectorySet
    set(value) {
        equivalentMap[this] = value
    }

//fun Project.duplicate(task: KotlinCompile, newName: String): KotlinCompile = tasks.create(newName, task::class.java) { newTask ->
//    task::class.java.fields.forEach {
//        if(it.trySetAccessible()) {
//            it.set(newTask, it.get(task))
//        }
//    }
//}