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
                this.operations.add(ViewStackOp.Embed("stack", "nodeB'"))
            },
            ViewNode("nodeB").apply {
                this.requires.add(ViewVar("stack", "ObservableStack[ViewGenerator]"))
                this.provides.add(ViewVar("thing", "Thing"))
                this.operations.add(ViewStackOp.Push("stack", "nodeC"))
            },
            ViewNode("nodeB'").apply {
                this.requires.add(ViewVar("stack", "ObservableStack[ViewGenerator]"))
                this.operations.add(ViewStackOp.Push("stack", "nodeC"))
            },
            ViewNode("nodeC").apply {
                this.requires.add(ViewVar("stack", "ObservableStack[ViewGenerator]"))
                this.requires.add(ViewVar("thing", "Thing"))
            }
        ).associate { it.name to it }
        println(nodes["nodeA"]!!.totalRequires(nodes))
        println(nodes["nodeB"]!!.totalRequires(nodes))
        println(nodes["nodeB'"]!!.totalRequires(nodes))
        println(nodes["nodeC"]!!.totalRequires(nodes))
    }
}
