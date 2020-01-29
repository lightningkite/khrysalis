package com.lightningkite.khrysalis.swift

import com.lightningkite.khrysalis.utils.forEachBetween
import org.jetbrains.kotlin.KotlinParser
import java.util.*
import kotlin.collections.ArrayList

private val KotlinParserClassDeclarationContextAdditionalInits =
    HashMap<KotlinParser.ClassDeclarationContext, MutableList<SwiftAltListener.(TabWriter) -> Unit>>()
val KotlinParser.ClassDeclarationContext.additionalInits: MutableList<SwiftAltListener.(TabWriter) -> Unit>
    get() = KotlinParserClassDeclarationContextAdditionalInits.getOrPut(this) { ArrayList() }

private val KotlinParserClassDeclarationContextPostClass =
    HashMap<KotlinParser.ClassDeclarationContext, MutableList<SwiftAltListener.(TabWriter) -> Unit>>()
val KotlinParser.ClassDeclarationContext.postClass: MutableList<SwiftAltListener.(TabWriter) -> Unit>
    get() = KotlinParserClassDeclarationContextPostClass.getOrPut(this) { ArrayList() }

fun KotlinParser.ClassDeclarationContext.clearAdditionalInits() =
    KotlinParserClassDeclarationContextAdditionalInits.remove(this)

private val KotlinParserClassDeclarationContextAdditionalDeclarations =
    HashMap<KotlinParser.ClassDeclarationContext, MutableList<SwiftAltListener.(TabWriter) -> Unit>>()
val KotlinParser.ClassDeclarationContext.additionalDeclarations: MutableList<SwiftAltListener.(TabWriter) -> Unit>
    get() = KotlinParserClassDeclarationContextAdditionalDeclarations.getOrPut(this) { ArrayList() }

fun KotlinParser.ClassDeclarationContext.clearAdditionalDeclarations() =
    KotlinParserClassDeclarationContextAdditionalDeclarations.remove(this)

fun KotlinParser.ClassDeclarationContext.constructorVars() =
    primaryConstructor()?.classParameters()?.classParameter()
        ?.asSequence()
        ?.filter { it.VAL() != null || it.VAR() != null } ?: sequenceOf()

fun KotlinParser.ClassDeclarationContext.constructorParameterNames() =
    primaryConstructor()?.classParameters()?.classParameter()
        ?.asSequence()
        ?.map { it.simpleIdentifier().text } ?: sequenceOf()


fun SwiftAltListener.registerClass() {

    fun KotlinParser.ClassParameterContext.needsOverride(parent: KotlinParser.ClassDeclarationContext): Boolean {
        val myName = this.simpleIdentifier().text
        val originalUsesOverride =
            this.modifiers()?.modifier()?.any { it.memberModifier()?.OVERRIDE() != null } ?: false
        return originalUsesOverride && !parent.implements()
            .any { myName in it.properties }
    }

    fun TabWriter.writeConvenienceInit(item: KotlinParser.ClassDeclarationContext) {
        if (item.primaryConstructor()?.classParameters()?.classParameter()?.size ?: 0 == 0) return
        line {
            var isFirst = true
            append("convenience public init(")
            item.primaryConstructor()?.classParameters()?.classParameter()?.forEachBetween(
                forItem = {
                    append("_ ")
                    append(it.simpleIdentifier().text)
                    append(": ")
                    write(it.type())
                    if (!isFirst) {
                        it.expression()?.let {
                            append(" = ")
                            write(it)
                        }
                    }
                    isFirst = false
                },
                between = {
                    append(", ")
                }
            )
            append(") {")
        }
        tab {
            line {
                append("self.init(")
                item.primaryConstructor()?.classParameters()?.classParameter()?.forEachBetween(
                    forItem = {
                        append(it.simpleIdentifier().text)
                        append(": ")
                        append(it.simpleIdentifier().text)
                    },
                    between = {
                        append(", ")
                    }
                )
                append(")")
            }
        }
        line("}")
    }

    fun TabWriter.handleCodableBody(item: KotlinParser.ClassDeclarationContext) {
        if (item.delegationSpecifiers()?.annotatedDelegationSpecifier()?.any { it.delegationSpecifier()?.userType()?.text == "Codable" } == true) {
            line("required public init(from decoder: Decoder) throws {")
            tab {
                line("let values = try decoder.container(keyedBy: CodingKeys.self)")
                item.constructorVars().forEach {
                    line {
                        val typeText = it.type().text.trim()
                        if (typeText == "Double") {
                            it.expression()?.let { default ->
                                append(it.simpleIdentifier().text)
                                append(" = try values.decodeDoubleIfPresent(forKey: .")
                                append(it.simpleIdentifier().text)
                                append(") ?? ")
                                write(default)
                            } ?: run {
                                append(it.simpleIdentifier().text)
                                append(" = try values.decodeDouble(forKey: .")
                                append(it.simpleIdentifier().text)
                                append(")")
                            }
                        } else if (typeText == "Double?") {
                            it.expression()?.let { default ->
                                append(it.simpleIdentifier().text)
                                append(" = try values.decodeDoubleIfPresent(forKey: .")
                                append(it.simpleIdentifier().text)
                                append(") ?? ")
                                write(default)
                            } ?: run {
                                append(it.simpleIdentifier().text)
                                append(" = try values.decodeDoubleIfPresent(forKey: .")
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
                    val jsonName = it.modifiers()?.annotation()?.asSequence()?.mapNotNull {
                        it.singleAnnotation()
                            ?.unescapedAnnotation()
                            ?.constructorInvocation()
                            ?.takeIf { it.userType().text == "JsonProperty" }
                            ?.valueArguments()
                            ?.valueArgument()
                            ?.firstOrNull()
                            ?.expression()
                            ?.text
                            ?.removePrefix("\"")
                            ?.removeSuffix("\"")
                    }?.firstOrNull()
                    line("case ${it.simpleIdentifier().text} = \"${jsonName ?: it.simpleIdentifier().text}\"")
                }
            }
            line("}")
            line()
            line("public func encode(to encoder: Encoder) throws {")
            tab {
                line("var container = encoder.container(keyedBy: CodingKeys.self)")
                item.constructorVars().forEach {
                    line {
                        val typeText = it.type().text.trim()
                        if (typeText.endsWith("?")) {
                            append("try container.encodeIfPresent(self.")
                            append(it.simpleIdentifier().text)
                            append(", forKey: .")
                            append(it.simpleIdentifier().text)
                            append(")")
                        } else {
                            append("try container.encode(self.")
                            append(it.simpleIdentifier().text)
                            append(", forKey: .")
                            append(it.simpleIdentifier().text)
                            append(")")
                        }
                    }
                }
            }
            line("}")
            line()
        }
    }

    fun TabWriter.handleEnumClass(item: KotlinParser.ClassDeclarationContext) {
        line {
            append(item.modifiers().visibilityString())
            append(" enum ${item.simpleIdentifier().text}: String, StringEnum, CaseIterable, Codable {")
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

            item.enumClassBody()?.classMemberDeclarations()?.classMemberDeclaration()?.forEach {
                startLine()
                write(it)
            }
        }
        line("}")
    }

    fun TabWriter.handleNormalClass(item: KotlinParser.ClassDeclarationContext) {
        val superclassInitCall = item.delegationSpecifiers()?.annotatedDelegationSpecifier()?.asSequence()?.mapNotNull {
            it.delegationSpecifier()?.constructorInvocation()
        }?.firstOrNull()

        line {
            append(item.modifiers().visibilityString())
            append(" class ${item.simpleIdentifier().text}")
            item.typeParameters()?.let {
                write(it)
            }
            val writeMainDgs = item
                .delegationSpecifiers()
                ?.annotatedDelegationSpecifier()
                ?.asSequence()
                ?.mapNotNull { it.delegationSpecifier() }
                ?.map {
                    fun() {
                        it.constructorInvocation()?.let {
                            write(it.userType())
                        }
                        it.userType()?.let { write(it) }
                        it.explicitDelegation()?.let {
                            throw IllegalArgumentException("Explicit delegation ('by') not supported")
                        }
                    }
                }
                ?: emptySequence()

            val writeDataDgs = if (item.modifiers()?.modifier()?.any { it.classModifier()?.DATA() != null } == true)
                sequenceOf(
                    { direct.append("Equatable") },
                    { direct.append("Hashable") }
                )
            else
                sequenceOf()

            (writeMainDgs + writeDataDgs).toList()
                .takeUnless { it.isEmpty() }
                ?.also { append(": ") }
                ?.forEachBetween(
                    forItem = {
                        it()
                    },
                    between = {
                        append(", ")
                    }
                )
            append(" {")
        }
        tab {
            line()

            item.constructorVars().forEach {
                if (it.needsOverride(item)) {
                    val type = it.type()
                    val name = it.simpleIdentifier().text
                    line {
                        append("public var _")
                        append(name)
                        append(": ")
                        filterEscapingAnnotation = true
                        write(type)
                        filterEscapingAnnotation = false
                    }
                    line {
                        append("override public var ")
                        append(name)
                        append(": ")
                        filterEscapingAnnotation = true
                        write(type)
                        filterEscapingAnnotation = false
                        append(" { get { return _$name } set(value) { _$name = value } }")
                    }
                } else {
                    line {
                        append("public var ")
                        append(it.simpleIdentifier().text)
                        append(": ")
                        val type = it.type()
                        filterEscapingAnnotation = true
                        write(type)
                        filterEscapingAnnotation = false
                    }
                }
            }

            line()

            if (item.modifiers()?.modifier()?.any { it.classModifier()?.DATA() != null } == true) {
                line("public static func == (lhs: ${item.simpleIdentifier().text}, rhs: ${item.simpleIdentifier().text}) -> Bool {")
                tab {
                    line {
                        append("return ")
                        tab {
                            item.constructorVars().forEachBetween(
                                forItem = {
                                    direct.append("lhs.${it.simpleIdentifier().text} == rhs.${it.simpleIdentifier().text}")
                                },
                                between = {
                                    direct.append(" &&")
                                    startLine()
                                }
                            )
                        }
                    }
                }
                line("}")

                line("public func hash(into hasher: inout Hasher) {")
                tab {
                    item.constructorVars().forEach {
                        line("hasher.combine(${it.simpleIdentifier().text})")
                    }
                }
                line("}")

                line("public func copy(")
                tab {
                    val lastIndex = item.constructorVars().count() - 1
                    item.constructorVars().forEachIndexed { index, it ->
                        line {
                            write(it.simpleIdentifier())
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

                line()
            }

            item.classBody()?.let { write(it) }

            line()

            line {
                if (superclassInitCall != null) {
                    val superclassArgs =
                        superclassInitCall.valueArguments()?.valueArgument()?.map { it.simpleIdentifier().text }
                            ?: listOf()
                    val myArgs = item.primaryConstructor()?.classParameters()?.classParameter()?.map {
                        it.simpleIdentifier().text
                    } ?: listOf()
                    if (superclassArgs == myArgs) {
                        append("override ")
                    }
                }
                append(item.primaryConstructor()?.modifiers().visibilityString().let {
                    if (it == "open") "public" else it
                })
                append(" init(")
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
                    if (it.needsOverride(item)) {
                        line("self._${it.simpleIdentifier().text} = ${it.simpleIdentifier().text}")
                    } else {
                        line("self.${it.simpleIdentifier().text} = ${it.simpleIdentifier().text}")
                    }
                }

                item.additionalInits.forEach { it.invoke(this@registerClass, this) }
                item.clearAdditionalInits()

                if (superclassInitCall != null) {
                    line {
                        append("super.init")
                        write(superclassInitCall.valueArguments())
                    }
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

            writeConvenienceInit(item)
            handleCodableBody(item)

            item.additionalDeclarations.forEach { it.invoke(this@registerClass, this@handleNormalClass) }
            item.clearAdditionalDeclarations()
        }
        line("}")
    }

    fun TabWriter.handleObject(item: KotlinParser.ObjectDeclarationContext) {
        line {
            append(item.modifiers().visibilityString())
            append(" enum ${item.simpleIdentifier().text}")
            append(" {")
        }
        tab {
            item.classBody()?.let {
                it.classMemberDeclarations().classMemberDeclaration().forEach {
                    startLine()
                    write(it)
                }
            }
        }
        line("}")
    }

    fun TabWriter.handleCompanionObject(item: KotlinParser.ClassBodyContext) {
        line()
        line("//Start Companion")
        item.classMemberDeclarations().classMemberDeclaration().forEach {
            startLine()
            write(it)
        }
        line("//End Companion")
        line()
    }

    fun TabWriter.handleInterfaceClass(item: KotlinParser.ClassDeclarationContext) {
        var defaultsContent = ArrayList<TabWriter.() -> Unit>()
        line {
            append(item.modifiers().visibilityString())
            append(" protocol ${item.simpleIdentifier().text}")
            item.typeParameters()?.let {
                throw IllegalArgumentException("Cannot use interfaces with type parameters; not available in Swift protocols.")
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
            item.classBody()?.let { item ->
                item.classMemberDeclarations().classMemberDeclaration().forEach {
                    it.companionObject()?.classBody()?.let {
                        handleCompanionObject(it)
                    }
                    it.declaration()?.let {
                        it.classDeclaration()
                            ?.let { throw UnsupportedOperationException("Classes within protocols not allowed in Swift") }
                        it.objectDeclaration()
                            ?.let { throw UnsupportedOperationException("Classes within protocols not allowed in Swift") }
                        it.typeAlias()?.let {
                            startLine()
                            write(it)
                        }
                        it.functionDeclaration()?.let {
                            startLine()
                            handleNormalFunction(this, it, excludeBody = true)
                            if (it.functionBody() != null) {
                                defaultsContent.add {
                                    handleNormalFunction(this, it)
                                }
                            }
                        }
                        it.propertyDeclaration()?.let {
                            startLine()
                            if (it.VAL() != null) {
                                line {
                                    append("var ")
                                    append(it.variableDeclaration().simpleIdentifier().text)
                                    append(": ")
                                    write(it.variableDeclaration().type())
                                    append(" { get }")
                                }
                            } else {
                                line {
                                    append("var ")
                                    append(it.variableDeclaration().simpleIdentifier().text)
                                    append(": ")
                                    write(it.variableDeclaration().type())
                                    append(" { get set }")
                                }
                            }
                            if (it.getter() != null) {
                                defaultsContent.add {
                                    write(it)
                                }
                            }
                        }
                    }
                    it.anonymousInitializer()?.let { throw UnsupportedOperationException() }
                    it.secondaryConstructor()?.let { throw UnsupportedOperationException() }
                }
            }
        }
        line("}")
        if (defaultsContent.isNotEmpty()) {
            line()
            line {
                append(item.modifiers().visibilityString())
                append(" extension ")
                append(item.simpleIdentifier().text)
                append(" {")
            }
            tab {
                for (w in defaultsContent) {
                    startLine()
                    w()
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
            else -> this.handleNormalClass(item)
        }
        item.postClass.forEach { it.invoke(this@registerClass, this@handle) }
    }

    handle<KotlinParser.ObjectDeclarationContext> { item ->
        this.handleObject(item)
    }

    handle<KotlinParser.ClassBodyContext> { item ->
        item.classMemberDeclarations().classMemberDeclaration().forEach {
            it.anonymousInitializer()?.run { return@forEach }
            it.companionObject()?.classBody()?.let {
                handleCompanionObject(it)
            } ?: run {
                startLine()
                write(it)
            }
        }
    }
}
