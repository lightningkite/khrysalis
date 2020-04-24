package com.lightningkite.khrysalis.preparse

import com.lightningkite.khrysalis.ios.swift.actuals.visibility
import org.jetbrains.kotlin.KotlinParser
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class PreparseListener {

    val interfaces = ArrayList<InterfaceData>()
    val topLevelDeclarations = HashMap<String, String>()

    fun parse(relativePath: String, ctx: KotlinParser.KotlinFileContext) {
        val packageName = ctx.packageHeader()?.identifier()?.text ?: ""
        val imports = ctx.importList().importHeader().asSequence().map { it.identifier().text }.toSet()
        ctx.topLevelObject().forEach {
            it.declaration().let {
                it.classDeclaration()?.let { ctx ->
                    if (ctx.visibility().isExposed) {
                        topLevelDeclarations[ctx.simpleIdentifier().text] = relativePath
                    }

                    if (ctx.INTERFACE() != null) {
                        interfaces.add(InterfaceData(
                            packageName = packageName,
                            name = ctx.simpleIdentifier().text,
                            implements = ctx.delegationSpecifiers()?.annotatedDelegationSpecifier()
                                ?.map { it.delegationSpecifier() }?.flatMap {
                                    val id =
                                        it.userType()?.text?.substringBefore('<') ?: return@flatMap listOf<String>()
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
                            methods = ctx.classBody()?.classMemberDeclarations()?.classMemberDeclaration()
                                ?.mapNotNull { it.declaration()?.functionDeclaration()?.simpleIdentifier()?.text }
                                ?: listOf(),
                            properties = ctx.classBody()?.classMemberDeclarations()?.classMemberDeclaration()
                                ?.mapNotNull {
                                    it.declaration()?.propertyDeclaration()?.variableDeclaration()
                                        ?.simpleIdentifier()?.text
                                } ?: listOf()
                        ))
                    }

                } ?: it.functionDeclaration()?.let {
                    if (it.visibility().isExposed) {
                        topLevelDeclarations[it.simpleIdentifier().text] = relativePath
                    }
                } ?: it.objectDeclaration()?.let {
                    if (it.visibility().isExposed) {
                        topLevelDeclarations[it.simpleIdentifier().text] = relativePath
                    }
                } ?: it.propertyDeclaration()?.let {
                    if (it.visibility().isExposed) {
                        topLevelDeclarations[it.variableDeclaration().simpleIdentifier().text] = relativePath
                    }
                } ?: it.typeAlias()?.let {
                    if (it.visibility().isExposed) {
                        topLevelDeclarations[it.simpleIdentifier().text] = relativePath
                    }
                }
            }
        }
    }
}
