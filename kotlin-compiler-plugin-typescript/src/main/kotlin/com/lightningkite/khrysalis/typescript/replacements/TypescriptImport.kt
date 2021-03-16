package com.lightningkite.khrysalis.typescript.replacements

import com.lightningkite.khrysalis.replacements.Import

data class TypescriptImport(val path: String, val identifier: String, val asName: String? = null): Import {
    companion object {
        const val WHOLE = "WHOLE"
    }

    override fun toString(): String {
        return "<import $path $identifier $asName>"
    }
}