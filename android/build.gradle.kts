

import com.lightningkite.kwift.KwiftSettings
import com.lightningkite.kwift.convertResourcesToIos
import com.lightningkite.kwift.layout.convertLayoutsToSwift
import com.lightningkite.kwift.layout.createAndroidLayoutClasses
import com.lightningkite.kwift.swift.convertKotlinToSwift

buildscript {
    val kotlin_version = "1.3.41"
    repositories {
        google()
        jcenter()
        mavenLocal()
    }
    dependencies {
//        classpath("com.lightningkite:kwift:0.1.0")
        classpath("com.lightningkite.kwift:plugin:0.1.0")
        classpath("com.android.tools.build:gradle:3.5.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("digital.wup.android-maven-publish") version "3.6.2"
}

group = "com.lightningkite.kwift"
version = "0.1.0"

repositories {
    maven("https://jitpack.io")
    google()
}

android {
    //    buildToolsVersion = "28.0.3"
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(19)
        targetSdkVersion(29)
        versionCode = 5
        versionName = "1.0.5"
    }
    buildTypes {
//        release {
//            minifyEnabled = false
//            proguardFiles = getDefaultProguardFile("proguard-android.txt"), 'proguard-rules.pro'
//        }
    }
}

val kotlin_version = "1.3.41"
dependencies {
    testImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test:runner:1.2.0")
    androidTestImplementation("com.android.support.test.espresso:espresso-core:3.0.2")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlin_version")
    api("androidx.appcompat:appcompat:1.1.0")
    api("com.google.android.material:material:1.0.0")
    api("com.android.support.constraint:constraint-layout:1.1.3")
    api("androidx.recyclerview:recyclerview:1.0.0")
    api("com.fasterxml.jackson.core:jackson-core:2.9.9")
    api("com.fasterxml.jackson.core:jackson-annotations:2.9.7")
    api("com.fasterxml.jackson.core:jackson-databind:2.9.9")
    api("com.squareup.okhttp3:okhttp:3.12.0")
    api("de.hdodenhof:circleimageview:2.2.0")
    api("br.com.simplepass:loading-button-android:1.14.0")
    api("com.squareup.picasso:picasso:2.71828")
    api("com.romandanylyk:pageindicatorview:1.0.3")
    api("com.theartofdev.edmodo:android-image-cropper:2.7.0")
    api("com.github.marcoscgdev:Android-Week-View:1.2.7")
}

tasks.create("sourceJar", Jar::class) {
    classifier = "sources"
    from(android.sourceSets["main"].java.srcDirs)
}

publishing {
    publications {
        val mavenAar by creating(MavenPublication::class){
            from(components["android"])
            artifact(tasks.getByName("sourceJar"))
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
        }
    }
}

KwiftSettings.verbose = true

val androidBase = project.projectDir
val iosBase = project.projectDir.resolve("../ios/Kwift")

tasks.create("kwiftConvertKotlinToSwift") {
    this.group = "build"
    doLast {
        println("Started on $androidBase")
        convertKotlinToSwift(
                androidFolder = androidBase,
                iosFolder = iosBase,
                clean = true
        )
        println("Finished")
    }
}
