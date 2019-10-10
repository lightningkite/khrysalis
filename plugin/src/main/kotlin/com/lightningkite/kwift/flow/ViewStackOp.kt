package com.lightningkite.kwift.flow

import com.lightningkite.kwift.layout.readXMLStyles
import com.lightningkite.kwift.log
import com.lightningkite.kwift.utils.XmlNode
import com.lightningkite.kwift.utils.attributeAsString
import com.lightningkite.kwift.utils.camelCase
import java.io.BufferedWriter
import java.io.File

sealed class ViewStackOp {
    abstract val stack: String?
    abstract val viewName: String?

    data class Pop(override val stack: String?) : ViewStackOp() {
        override val viewName: String?
            get() = null
    }

    data class Push(override val stack: String?, override val viewName: String) : ViewStackOp()
    data class Reset(override val stack: String?, override val viewName: String) : ViewStackOp()
    data class Swap(override val stack: String?, override val viewName: String) : ViewStackOp()
    data class Embed(override val stack: String, override val viewName: String) : ViewStackOp()
}

