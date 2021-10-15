import java.util.Properties

plugins {
    id("kotlin")
    id("maven")
    id("signing")
    id("org.jetbrains.dokka") version "1.5.0"
    `maven-publish`
}

group = "com.lightningkite.khrysalis"
version = "0.0.1"


val props = project.rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use { stream ->
    Properties().apply { load(stream) }
}
val signingKey: String? = (System.getenv("SIGNING_KEY")?.takeUnless { it.isEmpty() }
    ?: project.properties["signingKey"]?.toString())
    ?.lineSequence()
    ?.filter { it.trim().firstOrNull()?.let { it.isLetterOrDigit() || it == '=' || it == '/' || it == '+' } == true }
    ?.joinToString("\n")
val signingPassword: String? = System.getenv("SIGNING_PASSWORD")?.takeUnless { it.isEmpty() }
    ?: project.properties["signingPassword"]?.toString()
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
    ?: project.properties["ossrhUsername"]?.toString())
    ?.trim()
val deploymentPassword = (System.getenv("OSSRH_PASSWORD")?.takeUnless { it.isEmpty() }
    ?: project.properties["ossrhPassword"]?.toString())
    ?.trim()
val useDeployment = deploymentUser != null || deploymentPassword != null

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("junit:junit:4.13.2")
}

tasks {
    val sourceJar by creating(Jar::class) {
        archiveClassifier.set("sources")
//        from(android.sourceSets["main"].java.srcDirs)
//        from(project.projectDir.resolve("src"))
        from(kotlin.sourceSets["main"].kotlin.srcDirs)
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
        publications {
            val java by creating(MavenPublication::class) {
                from(components["java"])
                artifact(tasks.getByName("sourceJar"))
                //artifact(tasks.getByName("javadocJar"))
                groupId = project.group.toString()
                artifactId = project.name
                version = project.version.toString()
            }
        }
    }
    if (useSigning) {
        signing {
            useInMemoryPgpKeys(signingKey, signingPassword)
            sign(configurations.archives.get())
        }
    }
}

if (useDeployment) {
    tasks.register("uploadSnapshot") {
        group = "upload"
        finalizedBy("uploadArchives")
        doLast {
            project.version = project.version.toString() + "-SNAPSHOT"
        }
    }

    tasks.named<Upload>("uploadArchives") {
        repositories.withConvention(MavenRepositoryHandlerConvention::class) {
            mavenDeployer {
                beforeDeployment {
                    signing.signPom(this)
                }
            }
        }

        repositories.withGroovyBuilder {
            "mavenDeployer"{
                "repository"("url" to "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/") {
                    "authentication"(
                        "userName" to deploymentUser,
                        "password" to deploymentPassword
                    )
                }
                "snapshotRepository"("url" to "https://s01.oss.sonatype.org/content/repositories/snapshots/") {
                    "authentication"(
                        "userName" to deploymentUser,
                        "password" to deploymentPassword
                    )
                }
                "pom" {
                    "project" {
                        setProperty("name", "RxKotlin-Property")
                        setProperty("packaging", "aar")
                        setProperty(
                            "description",
                            "An observable library for kotlin based on rxkotlin."
                        )
                        setProperty("url", "https://github.com/lightningkite/rxkotlin-property")

                        "scm" {
                            setProperty("connection", "scm:git:https://github.com/lightningkite/rxkotlin-property.git")
                            setProperty(
                                "developerConnection",
                                "scm:git:https://github.com/lightningkite/rxkotlin-property.git"
                            )
                            setProperty("url", "https://github.com/lightningkite/rxkotlin-property")
                        }

                        "licenses" {
                            "license"{
                                setProperty("name", "The MIT License (MIT)")
                                setProperty("url", "https://www.mit.edu/~amini/LICENSE.md")
                                setProperty("distribution", "repo")
                            }

                        }
                        "developers"{
                            "developer"{
                                setProperty("id", "bjsvedin")
                                setProperty("name", "Brady Svedin")
                                setProperty("email", "brady@lightningkite.com")
                            }
                        }
                    }
                }
            }
        }
    }
}
