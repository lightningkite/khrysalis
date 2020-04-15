package com.lightningkite.khrysalis.android.layout

import com.fasterxml.jackson.annotation.JsonIgnore

data class AndroidSubLayout(
    val name: String,
    val resourceId: String,
    val layoutXmlClass: String,
    val optional: Boolean = false
) {
    @get:JsonIgnore()
    val initiation: String
        get() = if (optional)
            "$name = view.findViewById<View>(R.id.$resourceId)?.let { $layoutXmlClass().apply{ setup(it) } }"
        else
            "$name = $layoutXmlClass().apply{ setup(view.findViewById<View>(R.id.$resourceId)) }"

    @get:JsonIgnore()
    val declaration: String
        get() = if (optional)
            "var $name: $layoutXmlClass? = null"
        else
            "lateinit var $name: $layoutXmlClass"
}
