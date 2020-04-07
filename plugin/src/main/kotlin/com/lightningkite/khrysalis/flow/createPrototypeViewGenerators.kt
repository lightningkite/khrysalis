package com.lightningkite.khrysalis.flow

import com.lightningkite.khrysalis.ios.layout.readXMLStyles
import com.lightningkite.khrysalis.log
import com.lightningkite.khrysalis.utils.XmlNode
import com.lightningkite.khrysalis.utils.camelCase
import java.io.File


fun createPrototypeViewGenerators(androidFolder: File, applicationPackage: String) =
    createPrototypeViewGenerators(
        resourcesFolder = androidFolder.resolve("src/main/res"),
        applicationPackage = applicationPackage,
        outputFolder = androidFolder.resolve("src/main/java/${applicationPackage.replace('.', '/')}/vg")
    )

internal fun createPrototypeViewGenerators(
    resourcesFolder: File,
    applicationPackage: String,
    outputFolder: File
) {
    outputFolder.mkdirs()

    val styles = File(resourcesFolder, "values/styles.xml").readXMLStyles()
    val packageName =
        outputFolder.absolutePath.replace('\\', '/').substringAfter("src/main/").substringAfter('/').replace('/', '.')
    val nodes = HashMap<String, ViewNode>()
    val files = File(resourcesFolder, "layout").walkTopDown()
        .filter { it.extension == "xml" }
        .filter { !it.name.contains("component") }

    //Gather graph information
    files.forEach { item ->
        log(item.toString())
        val fileName = item.nameWithoutExtension.camelCase().capitalize()
        val node = ViewNode(fileName)
        node.gather(XmlNode.read(item, styles), item, styles)
        nodes[fileName] = node
    }

    //Emit inaccessible nodes warning
    val inaccessibleNodes = nodes.values.filter { node ->
        nodes.values.asSequence().flatMap { it.operations.asSequence() }.none { it.viewName == node.name }
    }
    inaccessibleNodes.forEach { println("WARNING! Node ${it.name} is not accessible") }

    //Throw on leaks
    ViewNode.assertNoLeaks(nodes)

    //Emit views
    val scannedDirectoryInfo = outputFolder.walkTopDown().filter { !it.isDirectory }.associate { it.name to it }
    files.forEach { item ->
        log(item.toString())
        val fileName = item.nameWithoutExtension.camelCase().capitalize()
        val node = nodes[fileName] ?: return@forEach
        val targetFileName = fileName + "VG.shared.kt"
        val targetFile = scannedDirectoryInfo[targetFileName] ?: outputFolder.resolve(targetFileName)
        createPrototypeVG(
            styles = styles,
            viewName = fileName,
            xml = item,
            target = targetFile,
            viewNodeMap = nodes,
            viewNode = node,
            packageName = packageName + (targetFile.relativeTo(outputFolder)
                .path
                .split(File.separator)
                .filter { it.firstOrNull()?.isLowerCase() == true }
                .takeUnless { it.isEmpty() }
                ?.joinToString(".", ".")
                ?: ""),
            applicationPackage = applicationPackage
        )
    }
}
