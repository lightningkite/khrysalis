package com.lightningkite.kwift.prototype

data class ViewVar(val name: String, val type: String) {
    override fun toString(): String = "$name: $type"
}
