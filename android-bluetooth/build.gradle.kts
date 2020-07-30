import com.lightningkite.khrysalis.KhrysalisSettings
import com.lightningkite.khrysalis.ios.convertResourcesToIos
import com.lightningkite.khrysalis.ios.layout.convertLayoutsToSwift

buildscript {
    val kotlin_version = "1.3.72"
    repositories {
        google()
        jcenter()
        mavenLocal()
    }
    dependencies {
        //        classpath("com.lightningkite:khrysalis:0.1.0")
        classpath("com.lightningkite.khrysalis:plugin:0.1.0")
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

group = "com.lightningkite.khrysalis"
version = "0.1.1"

repositories {
    maven("https://jitpack.io")
    google()
    mavenLocal()
    maven("https://maven.google.com")
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

    packagingOptions {
        pickFirst("META-INF/android_release.kotlin_module")
        pickFirst("META-INF/android_debug.kotlin_module")
        pickFirst("META-INF/android-maps_release.kotlin_module")
        pickFirst("META-INF/android-maps_debug.kotlin_module")
    }
}

val kotlin_version = "1.3.72"
dependencies {
    api(project(":android"))
//    implementation("com.lightningkite.khrysalis:android:0.1.0")
    testImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test:runner:1.2.0")
    androidTestImplementation("com.android.support.test.espresso:espresso-core:3.0.2")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlin_version")
    api("com.polidea.rxandroidble2:rxandroidble:1.11.0")

}

tasks.create("sourceJar", Jar::class) {
    classifier = "sources"
    from(android.sourceSets["main"].java.srcDirs)
    from(project.projectDir.resolve("src/include"))
}

publishing {
    publications {
        val mavenAar by creating(MavenPublication::class) {
            from(components["android"])
            artifact(tasks.getByName("sourceJar"))
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
        }
    }
}

KhrysalisSettings.verbose = true

val androidBase = project.projectDir
val iosBase = project.projectDir.resolve("../ios-bluetooth/KhrysalisBluetooth")
