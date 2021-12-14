import java.util.Properties

val kotlinVersion = "1.6.0"
buildscript {
    val kotlinVersion = "1.6.0"
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
version = "0.7.1"

gradlePlugin {
    plugins {
        val khrysalisPlugin by creating() {
            id = "com.lightningkite.khrysalis"
            implementationClass = "com.lightningkite.khrysalis.gradle.KhrysalisPlugin"
        }
    }
}

repositories {
    mavenLocal()
    jcenter()
    mavenCentral()
    google()
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class).all {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    api(localGroovy())
    api(gradleApi())

//    implementation(project(":kotlin-compiler-plugin-typescript"))
//    implementation(project(":kotlin-compiler-plugin-kotlin"))
//    implementation(project(":kotlin-compiler-plugin-swift"))

    api(group = "org.jetbrains.kotlin", name = "kotlin-gradle-plugin", version = kotlinVersion)
    api(group = "org.jetbrains.kotlin", name = "kotlin-gradle-plugin-api", version = kotlinVersion)

    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable")

    // https://mvnrepository.com/artifact/com.google.protobuf/protobuf-java
    api("org.apache.commons:commons-lang3:3.12.0")
    api("com.fasterxml.jackson.core:jackson-databind:2.13.0")
    api("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.0")
    api("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.0")
    api("net.jodah:xsylum:0.1.0")

    // https://mvnrepository.com/artifact/org.apache.xmlgraphics/batik-transcoder
    implementation(group = "org.apache.xmlgraphics", name = "batik-transcoder", version = "1.13")
    implementation(group = "org.apache.xmlgraphics", name = "batik-codec", version = "1.13")

    // https://mvnrepository.com/artifact/net.mabboud.fontverter/FontVerter
    implementation(group = "net.mabboud.fontverter", name = "FontVerter", version = "1.2.22")

    testImplementation("junit:junit:4.13.2")

    val aetherVersion = "1.1.0"
    val mavenVersion = "3.3.9"
    testApi("org.eclipse.aether:aether-api:$aetherVersion")
    testApi("org.eclipse.aether:aether-impl:$aetherVersion")
    testApi("org.eclipse.aether:aether-util:$aetherVersion")
    testApi("org.eclipse.aether:aether-connector-basic:$aetherVersion")
    testApi("org.eclipse.aether:aether-transport-file:$aetherVersion")
    testApi("org.eclipse.aether:aether-transport-http:$aetherVersion")
    testApi("org.apache.maven:maven-aether-provider:$mavenVersion")
}


// Signing and publishing
val props = project.rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use { stream ->
    Properties().apply { load(stream) }
}
val signingKey: String? = (System.getenv("SIGNING_KEY")?.takeUnless { it.isEmpty() }
    ?: props?.getProperty("signingKey")?.toString())
    ?.lineSequence()
    ?.filter { it.trim().firstOrNull()?.let { it.isLetterOrDigit() || it == '=' || it == '/' || it == '+' } == true }
    ?.joinToString("\n")
val signingPassword: String? = System.getenv("SIGNING_PASSWORD")?.takeUnless { it.isEmpty() }
    ?: props?.getProperty("signingPassword")?.toString()
val useSigning = signingKey != null && signingPassword != null

if (signingKey != null) {
    if (!signingKey.contains('\n')) {
        throw IllegalArgumentException("Expected signing key to have multiple lines")
    }
    if (signingKey.contains('"')) {
        throw IllegalArgumentException("Signing key has quote outta nowhere")
    }
}

val deploymentUser = (System.getenv("OSSRH_USERNAME")?.takeUnless { it.isEmpty() }
    ?: props?.getProperty("ossrhUsername")?.toString())
    ?.trim()
val deploymentPassword = (System.getenv("OSSRH_PASSWORD")?.takeUnless { it.isEmpty() }
    ?: props?.getProperty("ossrhPassword")?.toString())
    ?.trim()
val useDeployment = deploymentUser != null || deploymentPassword != null

tasks {
    val sourceJar by creating(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets["main"].java.srcDirs)
        from(project.projectDir.resolve("src/include"))
    }
    val javadocJar by creating(Jar::class) {
        dependsOn("dokkaJavadoc")
        archiveClassifier.set("javadoc")
        from(project.file("build/dokka/javadoc"))
    }
    artifacts {
        archives(sourceJar)
        archives(javadocJar)
    }
}

afterEvaluate {
    publishing {
        this.publications.forEach {
            (it as MavenPublication).setPom()
        }
        publications {
            val release by creating(MavenPublication::class) {
                from(components["java"])
                artifact(tasks["sourceJar"])
                if (useSigning) {
                    artifact(tasks["javadocJar"])
                }
                groupId = project.group.toString()
                artifactId = project.name
                version = project.version.toString()
                setPom()
            }
        }
        if (useDeployment) {
            repositories {
                maven {
                    name = "MavenCentral"
                    val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
                    val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
                    url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
                    credentials {
                        this.username = deploymentUser
                        this.password = deploymentPassword
                    }
                }
            }
        }
    }
    if (useSigning) {
        signing {
            useInMemoryPgpKeys(signingKey, signingPassword)
            sign(publishing.publications)
        }
    }
}

fun MavenPublication.setPom() {
    pom {
        name.set("Khrysalis-Plugin")
        description.set("Khrysalis is a low-commitment multiplatform application development system based on converting Android apps into iOS and Web apps.")
        url.set("https://github.com/lightningkite/khrysalis")

        scm {
            connection.set("scm:git:https://github.com/lightningkite/khrysalis.git")
            developerConnection.set("scm:git:https://github.com/lightningkite/khrysalis.git")
            url.set("https://github.com/lightningkite/khrysalis")
        }

        licenses {

            license {
                name.set("GNU General Public License v3.0")
                url.set("https://www.gnu.org/licenses/gpl-3.0.en.html")
                distribution.set("repo")
            }
            license {
                name.set("Commercial License")
                url.set("https://www.lightningkite.com")
                distribution.set("repo")
            }
        }

        developers {
            developer {
                id.set("LightningKiteJoseph")
                name.set("Joseph Ivie")
                email.set("joseph@lightningkite.com")
            }
        }
    }
}
