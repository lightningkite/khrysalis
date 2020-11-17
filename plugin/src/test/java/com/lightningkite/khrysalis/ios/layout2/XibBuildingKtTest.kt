package com.lightningkite.khrysalis.ios.layout2

import com.lightningkite.khrysalis.swift.replacements.xib.AttKind
import com.lightningkite.khrysalis.swift.replacements.xib.AttPath
import com.lightningkite.khrysalis.swift.replacements.xib.PureXmlOut
import org.junit.Assert.*
import org.junit.Test

class XibBuildingKtTest {

    val resolver = object: CanResolveValue {
        override fun resolveFont(string: String): IosFont? = null
        override fun resolveDimension(string: String): String = string
        override fun resolveColor(string: String): Any = string
        override fun resolveString(string: String): String = string
        override fun resolveImage(string: String): String = string
        override fun resolveDrawable(string: String): String? = string
    }

    @Test
    fun complexPathing(){
        val out = PureXmlOut()
        val path = AttPath("property/fontDescription/attribute/attName")
        path.resolve(out).put(AttKind.Raw, "test", resolver)
        println(out.toString())
        assertEquals("test", out.children.find { it.name == "fontDescription" }?.attributes?.get("attName"))
    }
    @Test
    fun complexPathing2(){
        val out = PureXmlOut()
        val path = AttPath("property/fontDescription/attribute/pointSize")
        path.resolve(out).put(AttKind.Dimension, "10", resolver)
        println(out.toString())
    }
}