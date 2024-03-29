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
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.4.9")
    api(project(":kotlin-compiler-plugin-common", "default"))
}


standardPublishing {
    name.set("Khrysalis Typescript")
    description.set("Transpiles Kotlin to Typescript")
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
