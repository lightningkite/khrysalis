package com.lightningkite.khrysalis.ios.drawables

import com.lightningkite.khrysalis.utils.XmlNode
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class ConvertVectorDrawableKtTest {
    @Test fun compareHard(){
        val base = File("src/test/data")
        val out = File("build/test/data")
        out.mkdirs()
        val files = listOf(
            base.resolve("ic_bullet.xml"),
            base.resolve("logo_text.xml")
        )
        for(file in files){
            val result = StringBuilder()
            val debug = StringBuilder()
            convertVectorDrawable(file.nameWithoutExtension, XmlNode.read(file, mapOf()), result, debug)
            out.resolve(file.nameWithoutExtension + ".svg").writeText(debug.toString())
        }
    }
}