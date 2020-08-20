package com.lightningkite.khrysalis.web.layout

class WebResources() {
    data class Color(
        val cssName: String,
        val rawValue: String
    )
    val colors = HashMap<String, Color>()
    data class Drawable(
        val cssName: String,
        val imagePath: String? = null
    )
    val drawables = HashMap<String, Drawable>()
}