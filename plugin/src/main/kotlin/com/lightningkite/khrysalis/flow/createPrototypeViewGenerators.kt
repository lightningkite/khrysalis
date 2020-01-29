package com.lightningkite.khrysalis.flow

import com.lightningkite.khrysalis.layout.readXMLStyles
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
    files.forEach { item ->
        log(item.toString())
        val fileName = item.nameWithoutExtension.camelCase().capitalize()
        val node = nodes[fileName] ?: return@forEach
        createPrototypeVG(
            styles = styles,
            viewName = fileName,
            xml = item,
            target = outputFolder.resolve(fileName + "VG.shared.kt"),
            viewNodeMap = nodes,
            viewNode = node,
            packageName = packageName,
            applicationPackage = applicationPackage
        )
    }

    //Clean up old stuff
    (outputFolder.listFiles() ?: arrayOf())
        .filter { it.name.endsWith("VG.kt") }
        .filter { it.name.removeSuffix("VG.kt") !in nodes.keys }
        .filter { it.useLines { it.any { it.contains(oldWarning, true) } } }
        .forEach {
            println("Cleaning out $it...")
            it.delete()
        }
}
