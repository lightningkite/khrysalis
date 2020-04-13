package com.lightningkite.khrysalis.web.layout

import com.lightningkite.khrysalis.utils.XmlNode
import java.io.File
import javax.imageio.ImageIO

fun measureImage(imageUrl: File): Pair<String, String> {
    return when (imageUrl.extension) {
        "svg" -> {
            val svg = XmlNode.read(imageUrl, mapOf())
            Pair(
                (svg.directAttributes["width"] ?: "24") + "px",
                (svg.directAttributes["height"] ?: "24") + "px"
            )
        }
        else -> {
            val png = ImageIO.read(imageUrl)
            Pair(
                png.width.toString() + "px",
                png.height.toString() + "px"
            )
        }
    }
}
