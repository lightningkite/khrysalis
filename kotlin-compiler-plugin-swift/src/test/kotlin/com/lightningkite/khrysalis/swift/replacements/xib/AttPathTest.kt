package com.lightningkite.khrysalis.swift.replacements.xib

import org.junit.Assert.*
import org.junit.Test

class AttPathTest {
    @Test fun parseComplex(){
        val value = AttPath("property/fontDescription/attribute/pointSize")
        println(value)
        assertEquals(AttPathType.Property, value.pathType)
        assertEquals("fontDescription", value.name)
        assertEquals("fontDescription", value.type)
        assertEquals(AttPathType.Attribute, value.then?.pathType)
        assertEquals("pointSize", value.then?.name)
    }
}