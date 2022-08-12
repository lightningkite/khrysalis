// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    val kotlinVersion:String by extra
    repositories {
        google()
        mavenCentral()
        maven(url="https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.4")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:$kotlinVersion")
        classpath("com.lightningkite:deploy-helpers:0.0.5")

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
        mavenCentral()
        maven(url="https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
}

tasks.create("clean", Delete::class.java) {
    delete(rootProject.buildDir)
}

