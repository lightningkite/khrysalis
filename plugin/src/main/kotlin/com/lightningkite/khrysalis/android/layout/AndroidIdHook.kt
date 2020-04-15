package com.lightningkite.khrysalis.android.layout

import com.fasterxml.jackson.annotation.JsonIgnore

data class AndroidIdHook(
    val name: String,
    val type: String,
    val resourceId: String,
    val optional: Boolean = false
) {
    @get:JsonIgnore()
    val initiation: String
        get() = if (optional)
            "$name = view.findViewById<$type>(R.id.$resourceId)"
        else
            "$name = view.findViewById<$type>(R.id.$resourceId)"

    @get:JsonIgnore()
    val declaration: String
        get() = if (optional)
            "var $name: $type? = null"
        else
            "lateinit var $name: $type"
}
