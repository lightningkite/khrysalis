package com.lightningkite.khrysalis.formatting

import org.junit.Assert.*
import org.junit.Test

class String_retabKtTest {
    @Test fun test(){
        val content = """
           |Asdf((
           |hi
           |))
           |
           |ASDF((
           |hey
           |)
           |)
           |
           |Partial(((
           |ooh
           |))
           |)
           |
           |Partial2(((
           |ooh
           |)
           |))
           |
           |Partial2(((
           |ooh
           |)(
           |uhh
           |)
           |))
           |
           |Calls(
           |x
           |.y
           |.z
           |.t
           |)
           |
        """.trimMargin()
        println(content)
        println("---retabbed---")
        println(content.retab())
    }
    @Test fun testReal(){
        val content = """
        self.currentPin.subscribeBy(onNext: { (it) -> Void in if it == existingPin {
        self.dialog.dismiss()
        ApplicationAccess.INSTANCE.softInputActive.value = false
    } else { if it.count >= 4 {
        post(action: { () -> Void in self.currentPin.value = "" })
    } } }).until(condition: xml.pin.removed)

"""
        val correct = """
self.currentPin.subscribeBy(onNext: { (it) -> Void in if it == existingPin {
        self.dialog.dismiss()
        ApplicationAccess.INSTANCE.softInputActive.value = false
    } else { if it.count >= 4 {
        post(action: { () -> Void in self.currentPin.value = "" })
} } }).until(condition: xml.pin.removed)

"""
        println(content)
        println("---retabbed---")
        val retabbed = content.retab()
        println(retabbed)
        assertEquals(correct, retabbed)
    }
}