// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    val kotlin_version = "1.6.10"
    repositories {
        google()
        mavenLocal()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.0.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

plugins {
    idea
}

allprojects {
    repositories {
        google()
    }
}

tasks.create("clean", Delete::class.java) {
    delete(rootProject.buildDir)
}

