package com.lightningkite.khrysalis.android.layout

import com.fasterxml.jackson.annotation.JsonIgnore

data class AndroidAction(
    val name: String,
    val action: String,
    val optional: Boolean = false
) {
    @get:JsonIgnore val invocation: String get() = name + (if (optional) "?." else ".") + action
}
