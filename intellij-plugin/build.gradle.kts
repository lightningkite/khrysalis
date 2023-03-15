import com.lightningkite.deployhelpers.*

plugins {
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
    implementation(project(":kotlin-compiler-plugin-swift"))
    implementation(project(":kotlin-compiler-plugin-typescript"))
    implementation("io.gitlab.arturbosch.detekt:detekt-api:1.22.0")
    testImplementation("io.gitlab.arturbosch.detekt:detekt-test:1.22.0")
}


intellij {
    version.set("2021.3.2")
    pluginName.set("khrysalis")
    plugins.add("java")
    plugins.add("org.jetbrains.plugins.gradle")
    plugins.add("org.jetbrains.kotlin")
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
