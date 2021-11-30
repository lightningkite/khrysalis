pluginManagement {
    repositories {
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    }
}
rootProject.name = "khrysalis"

include(":plugin")
include(":jvm-runtime")
include("kotlin-compiler-plugin-common")
include("kotlin-compiler-plugin-kotlin")
include("kotlin-compiler-plugin-swift")
include("kotlin-compiler-plugin-typescript")
