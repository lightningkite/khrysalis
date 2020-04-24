package com.lightningkite.khrysalis.utils

import groovy.lang.GroovyObject
import java.lang.reflect.Field
import java.lang.reflect.Method

class UntypedThing(val wraps: Any?) {
    operator fun get(key: String): UntypedThing {
        if (wraps == null) return this
        try {
            val fields = wraps::class.java.allFields
            val field = fields.find { it.name == key } ?: fields.find { it.name == "m" + key.capitalize() }
            if (field != null) {
                field.isAccessible = true
                return UntypedThing(field.get(wraps))
            } else {
                val methods = wraps::class.java.allMethods
                val method = methods.find { it.name == "get" + key.capitalize() }
                return UntypedThing(null)
            }
        } catch (e: Exception) {
            return UntypedThing(null)
        }
    }

    private val Class<*>.allFields: Array<Field> get() = this.declaredFields + (this.superclass?.allFields ?: arrayOf())
    private val Class<*>.allMethods: Array<Method> get() = this.declaredMethods + (this.superclass?.allMethods ?: arrayOf())

    fun options() = if (wraps != null) wraps::class.java.allFields.map { it.name } else listOf()
    fun optionsString() =
        if (wraps != null) wraps::class.java.simpleName + " -- " + wraps::class.java.allMethods.map { it.name + ": " + it.returnType.simpleName }
            .joinToString() else "-none, for it is null-"

    inline fun <reified T : Any> asType(): T? = wraps as? T
    override fun toString(): String {
        return wraps.toString()
    }

    override fun equals(other: Any?): Boolean =
        if (other is UntypedThing) this.wraps == other.wraps else this.wraps == other

    override fun hashCode(): Int = wraps.hashCode()
}

val Any?.untyped get() = UntypedThing(this)

val Any?.groovyObject: GroovyObject? get() = this as? GroovyObject
fun GroovyObject.getPropertyAsObject(key: String): GroovyObject? = getProperty(key) as? GroovyObject

//_gr_svcs_, _gr_map_, __aaptOptions__, __adbExe__, __adbExecutable__, __adbOptions__, __aidlPackageWhiteList__, __applicationVariants__, __baseFeature__, __bootClasspath__, __buildOutputs__, __buildToolsRevision__, __buildToolsVersion__, __buildTypes__, __compileOptions__, __compileSdkVersion__, __dataBinding__, __defaultConfig__, __defaultPublishConfig__, __deviceProviders__, __dexOptions__, __externalNativeBuild__, __flavorDimensionList__, __generatePureSplits__, __jacoco__, __libraryRequests__, __lintOptions__, __mockableAndroidJar__, __ndkDirectory__, __packageBuildConfig__, __packagingOptions__, __productFlavors__, __resourcePrefix__, __sdkDirectory__, __signingConfigs__, __sourceSets__, __splits__, __testBuildType__, __testOptions__, __testServers__, __testVariants__, __transforms__, __transformsDependencies__, __unitTestVariants__, __variantFilter__, _gr_dyn_, _gr_mc_]
