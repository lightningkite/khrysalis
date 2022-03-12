import com.lightningkite.deployhelpers.*

plugins {
    kotlin("jvm")
    signing
    id("org.jetbrains.dokka")
    `maven-publish`
}

group = "com.lightningkite.khrysalis"

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    testImplementation("junit:junit:4.12")
    testImplementation("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    api(project(":kotlin-compiler-plugin-common", "default"))
}


standardPublishing {
    name.set("Khrysalis Kotlin Helpers")
    description.set("Helper tools for working with Kotlin in Khrysalis.")
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
