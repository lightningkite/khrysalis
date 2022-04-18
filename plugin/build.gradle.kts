import com.lightningkite.deployhelpers.*

buildscript {
    val kotlinVersion:String by project
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlinVersion}")
    }
}
plugins {
    kotlin("jvm")
    java
    `java-gradle-plugin`
    idea
    signing
    id("org.jetbrains.dokka")
    `maven-publish`
}

group = "com.lightningkite.khrysalis"

gradlePlugin {
    plugins {
        val khrysalisPlugin by creating() {
            id = "com.lightningkite.khrysalis"
            implementationClass = "com.lightningkite.khrysalis.gradle.KhrysalisPlugin"
        }
    }
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class).all {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

val jacksonVersion = "2.13.2"
val kotlinVersion:String by project
dependencies {
    api(localGroovy())
    api(gradleApi())

    api("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    api("org.jetbrains.kotlin:kotlin-gradle-plugin-api:$kotlinVersion")

    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    compileOnly("com.android.tools.build:gradle:7.0.4")

    // https://mvnrepository.com/artifact/com.google.protobuf/protobuf-java
    api("org.apache.commons:commons-lang3:3.12.0")
    api("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    api("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    api("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")
    api("net.jodah:xsylum:0.1.0")

    // https://mvnrepository.com/artifact/org.apache.xmlgraphics/batik-transcoder
    implementation("org.apache.xmlgraphics:batik-transcoder:1.14")
    implementation("org.apache.xmlgraphics:batik-codec:1.14")

    // https://mvnrepository.com/artifact/net.mabboud.fontverter/FontVerter
    implementation("net.mabboud.fontverter:FontVerter:1.2.22")

    testImplementation("junit:junit:4.13.2")
}

standardPublishing {
    name.set("Khrysalis-Plugin")
    description.set("Khrysalis is a low-commitment multiplatform application development system based on converting Android apps into iOS and Web apps.")
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
    }
}
