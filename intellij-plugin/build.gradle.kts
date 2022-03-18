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
version = "0.0.1"

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlin:kotlin-compiler-embeddable")
//    implementation(project(":kotlin-compiler-plugin-swift"))
//    implementation(project(":kotlin-compiler-plugin-typescript"))
    implementation("com.lightningkite.rx:rxplus:1.0.0-rc1")
}

intellij {
    version.set("2021.3.2")
    pluginName.set("khrysalis")
    plugins.set(listOf(
//        "gradle"
        "com.intellij.java",
        "org.jetbrains.plugins.gradle",
        "com.intellij.gradle",
        "com.intellij.externalSystem.dependencyUpdater"
    ))
    downloadSources.set(true)
    type.set("IC")
    updateSinceUntilBuild.set(false)
}

standardPublishing {
    name.set("Khrysalis IntelliJ Plugin")
    description.set("Helps you write Khrysalis-translatable code.")
    github("lightningkite", "khrysalis")
    licenses {
        mit()
    }

    developers {
        developer(
            id = "LightningKiteJoseph",
            name = "Joseph Ivie",
            email = "joseph@lightningkite.com",
        )
        developer(
            id = "bjsvedin",
            name = "Brady Svedin",
            email = "brady@lightningkite.com",
        )
    }
}
