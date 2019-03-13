package com.lightningkite.kwift.interfaces

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.jetbrains.kotlin.KotlinParser
import org.jetbrains.kotlin.KotlinParserBaseListener

open class InterfaceListener(val parser: KotlinParser) : KotlinParserBaseListener() {

    data class InterfaceData(
        @JsonProperty("packageName") val packageName: String = "",
        val name: String = "",
        val methods: List<String> = listOf(),
        val properties: List<String> = listOf(),
        val implements: List<String> = listOf()
    ){
        @get:JsonIgnore val qualifiedName get() = packageName + "." + name
    }

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
        currentPackage = ctx.identifier()?.text ?: ""
    }

    override fun enterClassDeclaration(ctx: KotlinParser.ClassDeclarationContext) {
        if (ctx.INTERFACE() == null) return
        interfaces.add(InterfaceData(
            packageName = currentPackage,
            name = ctx.simpleIdentifier().text,
            implements = ctx.delegationSpecifiers()?.delegationSpecifier()?.flatMap {
                val id = it.userType()?.text?.substringBefore('<') ?: return@flatMap listOf<String>()
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
            methods = ctx.classBody()?.classMemberDeclaration()
                ?.mapNotNull { it.functionDeclaration()?.identifier()?.text } ?: listOf(),
            properties = ctx.classBody()?.classMemberDeclaration()
                ?.mapNotNull { it.propertyDeclaration()?.variableDeclaration()?.simpleIdentifier()?.text } ?: listOf()
        ))
    }
}
