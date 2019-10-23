package com.lightningkite.kwift.flow

import java.io.BufferedWriter
import java.io.File

fun File.mermaidToSvg(
    out: File = this.parentFile.resolve(this.nameWithoutExtension + ".svg")
): File? {
    println("Converting $name to SVG:")
    val result = ProcessBuilder()
        .command("mmdc", "-i", this.absolutePath, "-o", out.absolutePath)
        .inheritIO()
        .start()
        .waitFor()
    return if(result == 0) out else null
}
