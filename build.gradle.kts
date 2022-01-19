// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    val kotlinVersion = "1.6.10"
    repositories {
        google()
        maven(url="https://s01.oss.sonatype.org/content/repositories/snapshots/")
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.2.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:$kotlinVersion")
        classpath("com.lightningkite:deploy-helpers:master-SNAPSHOT")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

plugins {
    idea
}

allprojects {
    repositories {
        mavenCentral()
        google()
    }
}

tasks.create("clean", Delete::class.java) {
    delete(rootProject.buildDir)
}

