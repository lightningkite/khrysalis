package com.lightningkite.khrysalis.android.layout

import com.fasterxml.jackson.annotation.JsonIgnore

data class AndroidDelegateHook(
    val name: String,
    val type: String,
    val resourceId: String,
    val optional: Boolean = false
) {
    @get:JsonIgnore()
    val initiation: String
        get() = if (optional)
            "${name}Delegate = view.findViewById<CustomView>(R.id.$resourceId).delegate as? $type"
        else
            "${name}Delegate = view.findViewById<CustomView>(R.id.$resourceId).delegate as $type"

    @get:JsonIgnore()
    val declaration: String
        get() = if (optional)
            "var ${name}Delegate: $type? = null"
        else
            "lateinit var ${name}Delegate: $type"
}
