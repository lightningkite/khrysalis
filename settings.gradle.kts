pluginManagement {
    repositories {
        maven("https://dl.bintray.com/kotlin/kotlin-eap")

        mavenCentral()

        maven("https://plugins.gradle.org/m2/")
    }
}
rootProject.name = "khrysalis"

include(":plugin")
include(":android")
include(":android-maps")
include(":android-fcm")
include(":android-bluetooth")
include(":android-qr")
include("kotlin-compiler-plugin-common")
include("kotlin-compiler-plugin-typescript")
