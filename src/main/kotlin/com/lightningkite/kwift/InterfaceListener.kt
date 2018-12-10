package com.lightningkite.kwift

import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.misc.Interval
import org.antlr.v4.runtime.tree.TerminalNode
import org.jetbrains.kotlin.KotlinParser
import org.jetbrains.kotlin.KotlinParserBaseListener

open class InterfaceListener(val parser: KotlinParser) : KotlinParserBaseListener() {

    data class InterfaceData(
        val packageName: String,
        val name: String,
        val methods: List<String>,
        val properties: List<String>,
        val implements: List<String>
    )

    val interfaces = ArrayList<InterfaceData>()
    var currentPackage = ""
    val imports = ArrayList<String>()

    override fun enterKotlinFile(ctx: KotlinParser.KotlinFileContext?) {
        currentPackage = ""
        imports.clear()
    }

    override fun enterImportHeader(ctx: KotlinParser.ImportHeaderContext) {
        imports.add(ctx.identifier().text)
    }

    override fun enterPackageHeader(ctx: KotlinParser.PackageHeaderContext) {
        currentPackage = ctx.text.substringAfter("package ").removeSuffix(";")
    }

    override fun enterClassDeclaration(ctx: KotlinParser.ClassDeclarationContext) {
        if (ctx.INTERFACE() == null) return
        interfaces.add(InterfaceData(
            packageName = currentPackage,
            name = ctx.simpleIdentifier().text,
            implements = ctx.delegationSpecifiers()?.delegationSpecifier()?.flatMap {
                val id = it.userType().text.substringBefore('<')
                if (id.firstOrNull()?.isLowerCase() == true) {
                    //qualified
                    listOf(id)
                } else {
                    imports.find {
                        it.endsWith(id)
                    }?.let { listOf(it) } ?: imports.filter {
                        it.endsWith('*')
                    }.map {
                        it.removeSuffix("*").plus(id)
                    }.plus(id)
                }
            } ?: listOf(),
            methods = ctx.classBody().classMemberDeclaration()
                .mapNotNull { it.functionDeclaration()?.identifier()?.text },
            properties = ctx.classBody().classMemberDeclaration()
                .mapNotNull { it.propertyDeclaration()?.variableDeclaration()?.simpleIdentifier()?.text }
        ))
    }
}
