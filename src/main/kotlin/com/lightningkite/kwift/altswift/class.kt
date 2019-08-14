package com.lightningkite.kwift.altswift

import com.lightningkite.kwift.swift.TabWriter
import com.lightningkite.kwift.utils.forEachBetween
import com.lightningkite.kwift.utils.snakeCase
import org.jetbrains.kotlin.KotlinParser

fun SwiftAltListener.registerClass() {

    fun KotlinParser.ClassDeclarationContext.constructorVars() =
        primaryConstructor()?.classParameters()?.classParameter()
            ?.asSequence()
            ?.filter { it.VAL() != null || it.VAR() != null } ?: sequenceOf()

    fun TabWriter.handleEnumClass(item: KotlinParser.ClassDeclarationContext) {
        line {
            append(item.modifiers().visibilityString())
            append(" enum ${item.simpleIdentifier().text}: String, CaseIterable, Codable {")
        }
        tab {
            item.enumClassBody().enumEntries().enumEntry().forEach {
                line("case ${it.simpleIdentifier().text} = \"${it.simpleIdentifier().text}\"")
            }
            line("public init(from decoder: Decoder) throws {")
            tab {
                line(
                    "self = try ${item.simpleIdentifier().text}(rawValue: decoder.singleValueContainer().decode(RawValue.self)) ?? .${item.enumClassBody().enumEntries().enumEntry(
                        0
                    ).simpleIdentifier().text}"
                )
            }
            line("}")

            item.enumClassBody().classMemberDeclarations().classMemberDeclaration().forEach {
                startLine()
                write(it)
            }
        }
        line("}")
    }

    fun TabWriter.handleNormalClass(item: KotlinParser.ClassDeclarationContext) {
        line {
            append(item.modifiers().visibilityString())
            append(" class ${item.simpleIdentifier().text}")
            item.typeParameters()?.let {
                write(it)
            }
            item.delegationSpecifiers()?.let { dg ->
                append(": ")
                dg.annotatedDelegationSpecifier()
                    .asSequence()
                    .mapNotNull { it.delegationSpecifier() }
                    .forEachBetween(
                        forItem = {
                            it.constructorInvocation()?.let {
                                write(it.userType())
                            }
                            it.userType()?.let { write(it) }
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

            item.constructorVars().forEach {
                line {
                    append("public var ")
                    append(it.simpleIdentifier().text)
                    append(": ")
                    write(it.type())
                }
            }

            line()

            line {
                append("init(")
                item.primaryConstructor()?.classParameters()?.classParameter()?.forEachBetween(
                    forItem = {
                        append(it.simpleIdentifier().text)
                        append(": ")
                        write(it.type())
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

                item.classBody()?.let {
                    it.classMemberDeclarations().classMemberDeclaration().asSequence()
                        .mapNotNull { it.anonymousInitializer() }
                        .flatMap { it.block()?.statements()?.statement()?.asSequence() ?: sequenceOf() }
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
            append(item.modifiers().visibilityString())
            append(" protocol ${item.simpleIdentifier().text}")
            item.typeParameters()?.let {
                write(it)
            }
            item.delegationSpecifiers()?.let { dg ->
                append(": ")
                dg.annotatedDelegationSpecifier()
                    .asSequence()
                    .mapNotNull { it.delegationSpecifier() }
                    .forEachBetween(
                        forItem = {
                            it.constructorInvocation()?.let {
                                write(it.userType())
                            }
                            it.userType()?.let { write(it) }
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

    fun TabWriter.handleDataClass(item: KotlinParser.ClassDeclarationContext) {
        line {
            append(item.modifiers().visibilityString())
            append(" class ${item.simpleIdentifier().text}")
            item.typeParameters()?.let {
                write(it)
            }
            item.delegationSpecifiers()?.let { dg ->
                append(": Equatable, Hashable")
                dg.annotatedDelegationSpecifier()
                    .asSequence()
                    .mapNotNull { it.delegationSpecifier() }
                    .forEach {
                        append(", ")
                        it.constructorInvocation()?.let {
                            write(it.userType())
                        }
                        it.userType()?.let { write(it) }
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
                line {
                    append("public var ")
                    append(it.simpleIdentifier().text)
                    append(": ")
                    write(it.type())
                }
            }

            line()

            line {
                append("init(")
                item.primaryConstructor()?.classParameters()?.classParameter()?.forEachBetween(
                    forItem = {
                        append(it.simpleIdentifier().text)
                        append(": ")
                        write(it.type())
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

                item.classBody()?.let {
                    it.classMemberDeclarations().classMemberDeclaration().asSequence()
                        .mapNotNull { it.anonymousInitializer() }
                        .flatMap { it.block().statements()?.statement()?.asSequence() ?: sequenceOf() }
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
                    line {
                        write(it.type())
                        append(": (")
                        write(it.type())
                        append(")?")
                        append(" = nil")
                        if (index != lastIndex) {
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
                        line {
                            append(it.simpleIdentifier().text)
                            append(": ")
                            append(it.simpleIdentifier().text)
                            append(" ?? self.")
                            append(it.simpleIdentifier().text)
                            if (index != lastIndex) {
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
        if (item.delegationSpecifiers().annotatedDelegationSpecifier().any { it.delegationSpecifier().userType().text == "Codable" }) {
            line("public init(from decoder: Decoder) throws {")
            tab {
                line("let values = try decoder.container(keyedBy: CodingKeys.self)")
                item.constructorVars().forEach {
                    line {
                        if (it.type().text == "Double") {
                            it.expression()?.let { default ->
                                append(it.simpleIdentifier().text)
                                append(" = try values.decodeIfDoublePresent(forKey: .")
                                append(it.simpleIdentifier().text)
                                append(") ?? ")
                                write(default)
                            } ?: run {
                                append(it.simpleIdentifier().text)
                                append(" = try values.decodeIfDouble(forKey: .")
                                append(it.simpleIdentifier().text)
                                append(")")
                            }
                        } else {
                            it.expression()?.let { default ->
                                append(it.simpleIdentifier().text)
                                append(" = try values.decodeIfPresent(")
                                write(it.type())
                                append(".self, forKey: .")
                                append(it.simpleIdentifier().text)
                                append(") ?? ")
                                write(default)
                            } ?: run {
                                append(it.simpleIdentifier().text)
                                append(" = try values.decode(")
                                write(it.type())
                                append(".self, forKey: .")
                                append(it.simpleIdentifier().text)
                                append(")")
                            }
                        }
                    }
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
            item.modifiers()?.modifier()?.any { it.classModifier()?.ENUM() != null } == true -> this.handleEnumClass(
                item
            )
            item.modifiers()?.modifier()?.any { it.classModifier()?.DATA() != null } == true -> this.handleDataClass(
                item
            )
            else -> this.handleNormalClass(item)
        }
    }

    handle<KotlinParser.ClassBodyContext> { item ->
        item.classMemberDeclarations().classMemberDeclaration().forEach {
            startLine()
            write(it)
        }
    }
}
