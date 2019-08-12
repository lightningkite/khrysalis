package com.lightningkite.kwift.altswift

import com.lightningkite.kwift.swift.TabWriter
import com.lightningkite.kwift.utils.forEachBetween
import com.lightningkite.kwift.utils.snakeCase
import org.jetbrains.kotlin.KotlinParser

fun SwiftAltListener.registerClass() {

    fun TabWriter.handleEnumClass(item: KotlinParser.ClassDeclarationContext) {
        line {
            append(item.modifierList().visibilityString())
            append(" enum ${item.simpleIdentifier().text}: String, CaseIterable, Codable {")
        }
        tab {
            item.enumClassBody().enumEntries().enumEntry().forEach {
                line("case ${it.simpleIdentifier().text} = \"${it.simpleIdentifier().text}\"")
            }
            line("public init(from decoder: Decoder) throws {")
            tab {
                line("self = try ${item.simpleIdentifier().text}(rawValue: decoder.singleValueContainer().decode(RawValue.self)) ?? .${item.enumClassBody().enumEntries().enumEntry(0).simpleIdentifier().text}")
            }
            line("}")

            item.enumClassBody().classMemberDeclaration().forEach {
                startLine()
                it.classDeclaration()?.let { write(it) }
                it.functionDeclaration()?.let { write(it) }
                it.objectDeclaration()?.let { write(it) }
                it.companionObject()?.let { write(it) }
                it.propertyDeclaration()?.let { write(it) }
                it.secondaryConstructor()?.let { throw UnsupportedOperationException("No secondary constructors yet.") }
            }
        }
        line("}")
    }

    fun TabWriter.handleNormalClass(item: KotlinParser.ClassDeclarationContext) {
        line {
            append(item.modifierList().visibilityString())
            append(" class ${item.simpleIdentifier().text}")
            item.typeParameters()?.let {
                append(it.text)
            }
            item.delegationSpecifiers()?.let { dg ->
                append(": ")
                dg.delegationSpecifier().forEachBetween(
                    forItem = {
                        it.constructorInvocation()?.let {
                            append(it.userType().toSwift())
                        }
                        it.userType()?.let { append(it.toSwift()) }
                        it.explicitDelegation()?.let {
                            throw IllegalArgumentException("Explicit delegation ('by') not supported")
                        }
                    },
                    between = {
                        append(", ")
                    }
                )
            }
            append(" {")
        }
        tab {
            line()

            item.primaryConstructor()?.classParameters()?.classParameter()
                ?.filter { it.VAL() != null || it.VAR() != null }
                ?.forEach {
                    line("public var ${it.simpleIdentifier().text}: ${it.type().toSwift()}")
                }

            line()

            line {
                append("init(")
                item.primaryConstructor()?.classParameters()?.classParameter()?.forEachBetween(
                    forItem = {
                        append(it.simpleIdentifier().text)
                        append(": ")
                        append(it.type().toSwift())
                        it.expression()?.let {
                            append(" = ")
                            write(it)
                        }
                    },
                    between = {
                        append(", ")
                    }
                )
                append(") {")
            }
            tab {

                item.primaryConstructor()?.classParameters()?.classParameter()
                    ?.filter { it.VAL() != null || it.VAR() != null }
                    ?.forEach {
                        line("self.${it.simpleIdentifier().text} = ${it.simpleIdentifier().text}")
                    }

                item.classBody()?.let{
                    it.classMemberDeclaration().asSequence()
                        .mapNotNull { it.anonymousInitializer() }
                        .flatMap { it.block().statement().asSequence() }
                        .forEach {
                            startLine()
                            write(it)
                        }
                }
            }
            line("}")

            line()

            item.classBody()?.let { write(it) }
        }
        line("}")
    }

    fun TabWriter.handleInterfaceClass(item: KotlinParser.ClassDeclarationContext) {
        line {
            append(item.modifierList().visibilityString())
            append(" protocol ${item.simpleIdentifier().text}")
            item.typeParameters()?.let {
                append(it.text)
            }
            item.delegationSpecifiers()?.let { dg ->
                append(": ")
                dg.delegationSpecifier().forEachBetween(
                    forItem = {
                        it.constructorInvocation()?.let {
                            append(it.userType().toSwift())
                        }
                        it.userType()?.let { append(it.toSwift()) }
                        it.explicitDelegation()?.let {
                            throw IllegalArgumentException("Explicit delegation ('by') not supported")
                        }
                    },
                    between = {
                        append(", ")
                    }
                )
            }
            append(" {")
        }
        tab {
            item.classBody()?.let { write(it) }
        }
        line("}")
    }

    fun KotlinParser.ClassDeclarationContext.constructorVars() =
        primaryConstructor()?.classParameters()?.classParameter()
            ?.asSequence()
            ?.filter { it.VAL() != null || it.VAR() != null } ?: sequenceOf()

    fun TabWriter.handleDataClass(item: KotlinParser.ClassDeclarationContext) {
        line {
            append(item.modifierList().visibilityString())
            append(" class ${item.simpleIdentifier().text}")
            item.typeParameters()?.let {
                append(it.text)
            }
            item.delegationSpecifiers()?.let { dg ->
                append(": Equatable, Hashable")
                dg.delegationSpecifier().forEach {
                    append(", ")
                    it.constructorInvocation()?.let {
                        append(it.userType().toSwift())
                    }
                    it.userType()?.let { append(it.toSwift()) }
                    it.explicitDelegation()?.let {
                        throw IllegalArgumentException("Explicit delegation ('by') not supported")
                    }
                }
            }
            append(" {")
        }
        tab {
            line()

            item.constructorVars().forEach {
                line("public var ${it.simpleIdentifier().text}: ${it.type().toSwift()}")
            }

            line()

            line {
                append("init(")
                item.primaryConstructor()?.classParameters()?.classParameter()?.forEachBetween(
                    forItem = {
                        append(it.simpleIdentifier().text)
                        append(": ")
                        append(it.type().toSwift())
                        it.expression()?.let {
                            append(" = ")
                            write(it)
                        }
                    },
                    between = {
                        append(", ")
                    }
                )
                append(") {")
            }
            tab {

                item.constructorVars().forEach {
                    line("self.${it.simpleIdentifier().text} = ${it.simpleIdentifier().text}")
                }

                item.classBody()?.let{
                    it.classMemberDeclaration().asSequence()
                        .mapNotNull { it.anonymousInitializer() }
                        .flatMap { it.block().statement().asSequence() }
                        .forEach {
                            startLine()
                            write(it)
                        }
                }
            }
            line("}")

            line()

            line("public static func == (lhs: ${item.simpleIdentifier().text}, rhs: ${item.simpleIdentifier().text}) -> Bool {")
            tab {
                line("return ")
                tab {
                    item.constructorVars().forEach {
                        line("lhs.${it.simpleIdentifier().text} == rhs.${it.simpleIdentifier().text}")
                    }
                }
            }
            line("}")

            line("public var hashValue: Int {")
            tab {
                line("return " + buildString {
                    item.constructorVars().forEachBetween(
                        forItem = {
                            append(it.simpleIdentifier().text + ".hashValue")
                        },
                        between = {
                            append(" ^ ")
                        }
                    )
                })
            }
            line("}")

            line("public func copy(")
            tab {
                val lastIndex = item.constructorVars().count() - 1
                item.constructorVars().forEachIndexed { index, it ->
                    line{
                        append(it.type().toSwift())
                        append(": (")
                        append(it.type().toSwift())
                        append(")?")
                        append(" = nil")
                        if(index != lastIndex){
                            append(",")
                        }
                    }
                }
            }
            line(") -> ${item.simpleIdentifier().text} {")
            tab {
                line("return ${item.simpleIdentifier().text}(")
                tab {
                    val lastIndex = item.constructorVars().count() - 1
                    item.constructorVars().forEachIndexed { index, it ->
                        line{
                            append(it.simpleIdentifier().text)
                            append(": ")
                            append(it.simpleIdentifier().text)
                            append(" ?? self.")
                            append(it.simpleIdentifier().text)
                            if(index != lastIndex){
                                append(",")
                            }
                        }
                    }
                }
                line(")")
            }
            line("}")

            item.classBody()?.let { write(it) }
        }
        line("}")
    }

    fun TabWriter.handleCodableBody(item: KotlinParser.ClassDeclarationContext) {
        if(item.delegationSpecifiers().delegationSpecifier().any { it.userType().text == "Codable" }) {
            line("public init(from decoder: Decoder) throws {")
            tab {
                line("let values = try decoder.container(keyedBy: CodingKeys.self)")
                item.constructorVars().forEach {
                    line("${it.simpleIdentifier().text} = try values.decodeIfPresent(${it.type().toSwift()}.self, forKey: .${it.simpleIdentifier().text}) ?? \"\"")
                }
            }
            line("}")
            line()
            line("enum CodingKeys: String, CodingKey {")
            tab {
                item.constructorVars().forEach {
                    line("case ${it.simpleIdentifier().text} = \"${it.simpleIdentifier().text.snakeCase()}}\"")
                }
            }
            line("}")
        }
    }

    handle<KotlinParser.ClassDeclarationContext> { item ->
        when {
            item.INTERFACE() != null -> this.handleInterfaceClass(item)
            item.modifierList()?.modifier()?.any { it.classModifier()?.ENUM() != null } == true -> this.handleEnumClass(item)
            item.modifierList()?.modifier()?.any { it.classModifier()?.DATA() != null } == true -> this.handleDataClass(item)
            else -> this.handleNormalClass(item)
        }
    }

    handle<KotlinParser.ClassBodyContext> { item ->
        item.classMemberDeclaration().forEach {
            startLine()
            it.classDeclaration()?.let { write(it) }
            it.functionDeclaration()?.let { write(it) }
            it.objectDeclaration()?.let { write(it) }
            it.companionObject()?.let { write(it) }
            it.propertyDeclaration()?.let { write(it) }
            it.secondaryConstructor()?.let { throw UnsupportedOperationException("No secondary constructors yet.") }
        }
    }
}
