package com.lightningkite.khrysalis.utils

import com.lightningkite.khrysalis.gradle.KhrysalisPlugin
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.JarURLConnection
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarFile

fun copyOutFromRes(folder: String, target: File, organization: String, organizationId: String, projectName: String) {
    fun String.applyReplacements(): String = this
        .replace("KhrysalisProductName", projectName)
        .replace("Khrysalis-Product-Name", projectName)
        .replace("Khrysalis_Product_Name", projectName)
        .replace("Khrysalis Product Name", projectName)
        .replace("KhrysalisOrganizationName", organization)
        .replace("Khrysalis-Organization-Name", organization)
        .replace("Khrysalis_Organization_Name", organization)
        .replace("Khrysalis Organization Name", organization)
        .replace("khrysalisorganizationidentifier", organizationId)

    val jarConnection = KhrysalisPlugin::class.java.getResource("/$folder").openConnection() as JarURLConnection

    try {
        val jarFile: JarFile = jarConnection.jarFile

        /**
         * Iterate all entries in the jar file.
         */
        val e: Enumeration<JarEntry> = jarFile.entries()
        while (e.hasMoreElements()) {
            val jarEntry: JarEntry = e.nextElement()
            val jarEntryName: String = jarEntry.name
            val jarConnectionEntryName: String = jarConnection.entryName
            /**
             * Extract files only if they match the path.
             */
            if (jarEntryName.startsWith(jarConnectionEntryName)) {
                val filename =
                    if (jarEntryName.startsWith(jarConnectionEntryName)) jarEntryName.substring(jarConnectionEntryName.length) else jarEntryName
                val currentFile = target.resolve(filename.applyReplacements().removePrefix("/"))
                if (jarEntry.isDirectory) {
                    currentFile.mkdirs()
                } else {
                    if (!currentFile.exists()) {
                        currentFile.bufferedWriter().use { out ->
                            jarFile.getInputStream(jarEntry).bufferedReader().useLines { input ->
                                input.forEach { out.appendLine(it.applyReplacements()) }
                            }
                        }
                    }
                }
            }
        }
    } catch (e: IOException) {
        // TODO add logger
        e.printStackTrace()
    }
}

fun copyFolderOutFromRes(folder: String, target: File) {
    val jarConnection = KhrysalisPlugin::class.java.getResource("/$folder").openConnection() as JarURLConnection

    try {
        val jarFile: JarFile = jarConnection.jarFile

        /**
         * Iterate all entries in the jar file.
         */
        val e: Enumeration<JarEntry> = jarFile.entries()
        while (e.hasMoreElements()) {
            val jarEntry: JarEntry = e.nextElement()
            val jarEntryName: String = jarEntry.name
            val jarConnectionEntryName: String = jarConnection.entryName
            /**
             * Extract files only if they match the path.
             */
            if (jarEntryName.startsWith(jarConnectionEntryName)) {
                val filename =
                    if (jarEntryName.startsWith(jarConnectionEntryName)) jarEntryName.substring(jarConnectionEntryName.length) else jarEntryName
                val currentFile = target.resolve(filename.removePrefix("/"))
                if (jarEntry.isDirectory) {
                    currentFile.mkdirs()
                } else {
                    if (currentFile.exists()) {
                        if (currentFile.checksum() == jarFile.getInputStream(jarEntry).checksum()) {
                            println("Skipping file copy; already the same")
                        }
                    }
                    currentFile.outputStream().use { out ->
                        jarFile.getInputStream(jarEntry).use { input ->
                            input.copyTo(out)
                        }
                    }
                }
            }
        }
    } catch (e: IOException) {
        // TODO add logger
        e.printStackTrace()
    }
}