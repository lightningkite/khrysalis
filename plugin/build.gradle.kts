import java.util.Properties

val kotlinVersion = "1.6.10"
buildscript {
    val kotlinVersion = "1.6.10"
    repositories {
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlinVersion}")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:$kotlinVersion")
    }
}
plugins {
    kotlin("jvm")
    java
    `java-gradle-plugin`
    idea
    maven
    signing
    id("org.jetbrains.dokka")
    `maven-publish`
}

group = "com.lightningkite.khrysalis"
version = "0.2.0"

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

if(signingKey != null) {
    if(!signingKey.contains('\n')){
        throw IllegalArgumentException("Expected signing key to have multiple lines")
    }
    if(signingKey.contains('"')){
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
    mavenCentral()
    google()
}

sourceSets.main {
    java.srcDirs(
        "../kotlin-compiler-plugin-swift/src/main/kotlin",
        "../kotlin-compiler-plugin-typescript/src/main/kotlin",
        "../kotlin-compiler-plugin-common/src/main/kotlin"
    )
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class).all {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    api(localGroovy())
    api(gradleApi())

    api(group = "org.jetbrains.kotlin", name = "kotlin-gradle-plugin", version = kotlinVersion)
    api(group = "org.jetbrains.kotlin", name = "kotlin-gradle-plugin-api", version = kotlinVersion)

    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable")

    // https://mvnrepository.com/artifact/com.google.protobuf/protobuf-java
    api("org.apache.commons:commons-lang3:3.10")
    api("com.fasterxml.jackson.core:jackson-databind:2.9.10")
    api("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.10")
    api("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.9.10")
    api("net.jodah:xsylum:0.1.0")

    // https://mvnrepository.com/artifact/org.apache.xmlgraphics/batik-transcoder
    implementation(group = "org.apache.xmlgraphics", name = "batik-transcoder", version = "1.13")
    implementation(group = "org.apache.xmlgraphics", name = "batik-codec", version = "1.13")

    // https://mvnrepository.com/artifact/net.mabboud.fontverter/FontVerter
    implementation(group = "net.mabboud.fontverter", name = "FontVerter", version = "1.2.22")

    testImplementation("junit:junit:4.12")

    val aetherVersion = "1.0.0.v20140518"
    val mavenVersion = "3.1.0"
    testApi("org.eclipse.aether:aether-api:$aetherVersion")
    testApi("org.eclipse.aether:aether-impl:$aetherVersion")
    testApi("org.eclipse.aether:aether-util:$aetherVersion")
    testApi("org.eclipse.aether:aether-connector-basic:$aetherVersion")
    testApi("org.eclipse.aether:aether-transport-file:$aetherVersion")
    testApi("org.eclipse.aether:aether-transport-http:$aetherVersion")
    testApi("org.apache.maven:maven-aether-provider:$mavenVersion")
}

tasks {
    val insertTypescriptJar by creating(Copy::class) {
        dependsOn(":kotlin-compiler-plugin-typescript:shadowJar")
        from("../kotlin-compiler-plugin-typescript/build/libs") {
            include("*-all.jar")
        }
        into("src/main/resources/compiler-plugins")
        rename(".*", "typescript.jar")
    }
    getByName("compileKotlin").dependsOn("insertTypescriptJar")

    val insertSwiftJar by creating(Copy::class) {
        dependsOn(":kotlin-compiler-plugin-swift:shadowJar")
        from("../kotlin-compiler-plugin-swift/build/libs") {
            include("*-all.jar")
        }
        into("src/main/resources/compiler-plugins")
        rename(".*", "swift.jar")
    }
    getByName("compileKotlin").dependsOn("insertSwiftJar")

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
        publications {
            val release by creating(MavenPublication::class) {
                from(components["java"])
                artifact(tasks["sourceJar"])
                artifact(tasks["javadocJar"])
                groupId = project.group.toString()
                artifactId = project.name
                version = project.version.toString()
            }
        }
    }
    if(useSigning){
        signing {
            useInMemoryPgpKeys(signingKey, signingPassword)
            sign(configurations.archives.get())
        }
    }
}

if(useDeployment){
    tasks.register("uploadSnapshot"){
        group="upload"
        finalizedBy("uploadArchives")
        doLast{
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
                        setProperty("name", "Khrysalis-Plugin")
                        setProperty("packaging", "jar")
                        setProperty(
                            "description",
                            "Khrysalis is a low-commitment multiplatform application development system based on converting Android apps into iOS and Web apps."
                        )
                        setProperty("url", "https://github.com/lightningkite/khrysalis")

                        "scm" {
                            setProperty("connection", "scm:git:https://github.com/lightningkite/khrysalis.git")
                            setProperty(
                                "developerConnection",
                                "scm:git:https://github.com/lightningkite/khrysalis.git"
                            )
                            setProperty("url", "https://github.com/lightningkite/khrysalis")
                        }

                        "licenses" {
                            "license"{
                                setProperty("name", "GNU General Public License v3.0")
                                setProperty("url", "https://www.gnu.org/licenses/gpl-3.0.en.html")
                                setProperty("distribution", "repo")
                            }
                            "license"{
                                setProperty("name", "Commercial License")
                                setProperty("url", "https://www.lightningkite.com")
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