package com.lightningkite.khrysalis.kotlin

import java.io.File
import java.lang.reflect.Method
import java.net.URLClassLoader

object JVM {
    class JarFileLoader() : URLClassLoader(arrayOf()) {
        fun addFile(file: File) {
            addURL(file.toURI().toURL())
        }
    }

    fun load(jars: List<File>): JarFileLoader {
        val jarLoader = JarFileLoader()
        for (jar in jars) {
            jarLoader.addFile(jar)
        }
        return jarLoader
    }

    fun runMain(jars: List<File>, mainClass: String, arguments: Array<*>): Int {
        val jarLoader = load(jars)
        val mainMethod = jarLoader.loadClass(mainClass).let {
            it.getDeclaredMethodOrNull("main", Array<Any?>::class.java)
                ?: it.getDeclaredMethodOrNull("main", Array<String>::class.java)
                ?: it.getDeclaredMethodOrNull("main")
                ?: run {
                    throw IllegalArgumentException("Could not find main function.  Available: ${
                        it.methods.joinToString("\n") { it.name + "(" + it.parameters.joinToString { it.name + ": " + it.type.simpleName } + ")" }
                    }")
                }
        }
        return mainMethod.invoke(jarLoader, arguments).let { it as? Int } ?: 0
    }

}

fun Class<*>.getDeclaredMethodOrNull(name: String, vararg types: Class<*>): Method? = try {
    this.getMethod(name, *types)
} catch (e: Exception) {
    null
}