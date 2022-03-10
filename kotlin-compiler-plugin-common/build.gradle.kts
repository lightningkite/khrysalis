import com.lightningkite.deployhelpers.*

plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka")
    `maven-publish`
}

group = "com.lightningkite.khrysalis"

dependencies {
    testImplementation("junit:junit:4.12")
    api("com.fasterxml.jackson.core:jackson-databind:2.9.10")
    api("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.10")
    api("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.9.10")
}

standardPublishing {
    name.set("Khrysalis Common")
    description.set("Common translational tools between Typescript and Swift.")
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
