// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    val kotlin_version = "1.3.41"
    repositories {
        google()
        jcenter()
        mavenLocal()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.2.1")
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
        jcenter()
    }
}

tasks.create("clean", Delete::class.java) {
    delete(rootProject.buildDir)
}

idea {
    module {
        excludeDirs.add(file("ios"))
        excludeDirs.add(file("ios-bluetooth"))
        excludeDirs.add(file("ios-fcm"))
        excludeDirs.add(file("ios-maps"))
    }
}
