import com.lightningkite.deployhelpers.*

plugins {
    java
    id("kotlin")
    id("signing")
    id("org.jetbrains.dokka")
    id("org.jetbrains.intellij") version "1.4.0"
    `maven-publish`
}

group = "com.lightningkite.khrysalis"
version = "0.0.2"

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-compiler")
    testImplementation("junit:junit:4.13.2")
    implementation(project(":kotlin-compiler-plugin-swift"))
    implementation(project(":kotlin-compiler-plugin-typescript"))
    implementation("com.lightningkite.rx:rxplus:1.0.0-rc1")
}

intellij {
    version.set("2021.3.3")
    pluginName.set("khrysalis")
    plugins.set(listOf(
//        "gradle"
        "org.jetbrains.kotlin",
        "org.jetbrains.plugins.gradle",
        "com.intellij.gradle",
        "com.intellij.externalSystem.dependencyUpdater"
    ))
    downloadSources.set(true)
    type.set("IC")
    updateSinceUntilBuild.set(false)
}
