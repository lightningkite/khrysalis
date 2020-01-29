package com.lightningkite.khrysalis.flow

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


fun File.mermaidToPng(
    out: File = this.parentFile.resolve(this.nameWithoutExtension + ".png")
): File? {
    println("Converting $name to PNG:")
    val result = ProcessBuilder()
        .command("mmdc", "-i", this.absolutePath, "-o", out.absolutePath, "--width", "1920", "--height", "1080")
        .inheritIO()
        .start()
        .waitFor()
    return if(result == 0) out else null
}
