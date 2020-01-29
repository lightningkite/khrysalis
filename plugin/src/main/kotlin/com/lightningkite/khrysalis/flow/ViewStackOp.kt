package com.lightningkite.khrysalis.flow

import com.lightningkite.khrysalis.layout.readXMLStyles
import com.lightningkite.khrysalis.log
import com.lightningkite.khrysalis.utils.XmlNode
import com.lightningkite.khrysalis.utils.attributeAsString
import com.lightningkite.khrysalis.utils.camelCase
import java.io.BufferedWriter
import java.io.File

sealed class ViewStackOp {
    abstract val stack: String?
    abstract val viewName: String?
    abstract val priority: Int

    data class Pop(override val stack: String?) : ViewStackOp() {
        override val viewName: String?
            get() = null
        override val priority: Int = 0
    }
    data class Dismiss(override val stack: String?) : ViewStackOp() {
        override val viewName: String?
            get() = null
        override val priority: Int = 0
    }

    data class Push(override val stack: String?, override val viewName: String) : ViewStackOp(){
        override val priority: Int = 1
    }
    data class PopTo(override val stack: String?, val viewType: String) : ViewStackOp(){
        override val priority: Int = 1
        override val viewName: String?
            get() = null
    }
    data class Reset(override val stack: String?, override val viewName: String) : ViewStackOp(){
        override val priority: Int = 1
    }
    data class Swap(override val stack: String?, override val viewName: String) : ViewStackOp(){
        override val priority: Int = 2
    }
    data class Embed(override val stack: String, override val viewName: String) : ViewStackOp(){
        override val priority: Int = 3
    }
}

