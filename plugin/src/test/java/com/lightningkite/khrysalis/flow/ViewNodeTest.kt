package com.lightningkite.khrysalis.flow

import org.junit.Assert.*
import org.junit.Test

class ViewNodeTest {
    @Test
    fun providesBlocksDependency() {
        val nodes = listOf(
            ViewNode("nodeA").apply {
                this.provides.add(ViewVar("stack", "ObservableStack[ViewGenerator]"))
                this.operations.add(ViewStackOp.Embed("stack", "nodeB"))
                this.operations.add(ViewStackOp.Embed("stack", "nodeC"))
            },
            ViewNode("nodeB").apply {
                this.requires.add(ViewVar("stack", "ObservableStack[ViewGenerator]"))
                this.provides.add(ViewVar("thing", "Thing"))
                this.operations.add(ViewStackOp.Push("stack", "nodeC"))
            },
            ViewNode("nodeC").apply {
                this.requires.add(ViewVar("stack", "ObservableStack[ViewGenerator]"))
                this.operations.add(ViewStackOp.Push("stack", "nodeD"))
            },
            ViewNode("nodeD").apply {
                this.requires.add(ViewVar("stack", "ObservableStack[ViewGenerator]"))
                this.requires.add(ViewVar("thing", "Thing", "null"))
            }
        ).associate { it.name to it }
        println(nodes["nodeA"]!!.totalRequires(nodes))
        println(nodes["nodeB"]!!.totalRequires(nodes))
        println(nodes["nodeC"]!!.totalRequires(nodes))
        println(nodes["nodeD"]!!.totalRequires(nodes))
    }

    @Test fun mergeTests(){
        println(ViewVar("a", "String?").merge(ViewVar("a", "String")))
        println(ViewVar("a", "String").merge(ViewVar("a", "String?")))
        println(ViewVar("a", "String").merge(ViewVar("a", "String?", "null")))
        println(ViewVar("a", "String", "\"thing\"").merge(ViewVar("a", "String?")))
        println(ViewVar("a", "MutableList").merge(ViewVar("a", "List")))
        println(ViewVar("a", "List").merge(ViewVar("a", "MutableList")))
        println(ViewVar("a", "Thing").merge(ViewVar("a", "Thing")))
        println(ViewVar("a", "Thing").merge(ViewVar("a", "Thing")))
        println(ViewVar("a", "Thing").merge(ViewVar("a", "Thing")))
        println(ViewVar("b", "Bruh").merge(ViewVar("a", "Thing")))
    }

    @Test fun satisfiesTests(){
        assertFalse(ViewVar("a", "String?").satisfies(ViewVar("a", "String")))
        assertTrue(ViewVar("a", "String").satisfies(ViewVar("a", "String?")))
        assertFalse(ViewVar("b", "Bruh").satisfies(ViewVar("a", "String?")))
        assertFalse(ViewVar("a", "String").satisfies(ViewVar("b", "String")))
    }

}
