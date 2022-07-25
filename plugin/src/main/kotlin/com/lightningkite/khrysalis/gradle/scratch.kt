package com.lightningkite.khrysalis.gradle

import com.android.build.gradle.api.AndroidSourceSet
import org.gradle.api.Named
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.tasks.SourceSet
import org.gradle.language.base.plugins.LanguageBasePlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*

//fun <T> Project.duplicate(newName: String, kotlinCompile: KotlinCompile): KotlinCompile {
//    return KotlinCompile(
//        KotlinJvmOptions
//    )
//}