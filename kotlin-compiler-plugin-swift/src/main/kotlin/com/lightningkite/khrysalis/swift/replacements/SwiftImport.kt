package com.lightningkite.khrysalis.swift.replacements

import com.lightningkite.khrysalis.replacements.Import

data class SwiftImport(val module: String): Import {
    override fun toString(): String {
        return "<import $module>"
    }
}