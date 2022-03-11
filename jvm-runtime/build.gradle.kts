import com.lightningkite.deployhelpers.*

plugins {
    id("kotlin")
    id("signing")
    id("org.jetbrains.dokka")
    `maven-publish`
}

group = "com.lightningkite.khrysalis"

dependencies {
    testImplementation("junit:junit:4.13.2")
}

standardPublishing {
    name.set("Khrysalis Runtime")
    description.set("A set of Annotations, extension functions, type aliases, and interfaces required for transpiling using Khrysalis.")
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

sourceSets.forEach {
    val dirSet = objects.sourceDirectorySet("equivalents", "Khrysalis Equivalents")
    dirSet.srcDirs(project.projectDir.resolve("src/${it.name}/equivalents"))
    it.extensions.add("equivalents", dirSet)
    project.tasks.create("equivalentsJar${it.name.capitalize()}", org.gradle.jvm.tasks.Jar::class.java) {
        this.group = "khrysalis"
        this.archiveClassifier.set("equivalents")
        this.from(dirSet)
    }
}

tasks.getByName("equivalentsJarMain").published = true