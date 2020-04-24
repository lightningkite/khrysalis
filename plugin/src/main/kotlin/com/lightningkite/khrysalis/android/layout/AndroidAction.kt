package com.lightningkite.khrysalis.android.layout

data class AndroidAction(
    val name: String,
    val action: String,
    val optional: Boolean = false
) {
    val invocation: String get() = name + (if (optional) "?." else ".") + action
}
