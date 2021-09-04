package com.lightningkite.khrysalis.flow

import java.io.BufferedWriter
import java.io.File

//npx -p @mermaid-js/mermaid-cli mmdc -h

private var installed = false
private val mmdcDirectory = File(System.getProperty("user.home")).resolve(".khrysalis/js-run").also { it.mkdirs() }
private fun install(){
    if(!installed) {
        val result = ProcessBuilder()
            .directory(mmdcDirectory)
            .command("npm", "install", "@mermaid-js/mermaid-cli")
            .inheritIO()
            .start()
            .waitFor()
        if(result == 0) {
            installed = true
        }
    }
}

fun File.mermaidToSvg(
    out: File = this.parentFile.resolve(this.nameWithoutExtension + ".svg")
): File? {
    println("Converting $name to SVG:")
    install()
    val result = ProcessBuilder()
        .directory(mmdcDirectory)
        .command("node_modules/.bin/mmdc", "-i", this.absolutePath, "-o", out.absolutePath, "--width", "1920", "--height", "1080")
        .inheritIO()
        .start()
        .waitFor()
    return if(result == 0) out else null
}


fun File.mermaidToPng(
    out: File = this.parentFile.resolve(this.nameWithoutExtension + ".png")
): File? {
    println("Converting $name to PNG:")
    install()
    val result = ProcessBuilder()
        .directory(mmdcDirectory)
        .command("node_modules/.bin/mmdc", "-i", this.absolutePath, "-o", out.absolutePath, "--width", "1920", "--height", "1080")
        .inheritIO()
        .start()
        .waitFor()
    return if(result == 0) out else null
}
