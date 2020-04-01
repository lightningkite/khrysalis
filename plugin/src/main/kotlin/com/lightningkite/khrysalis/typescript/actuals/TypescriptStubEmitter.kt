package com.lightningkite.khrysalis.typescript.actuals

import com.lightningkite.khrysalis.flow.CodeSection
import com.lightningkite.khrysalis.flow.section
import com.lightningkite.khrysalis.flow.sectionPermanent
import com.lightningkite.khrysalis.swift.SwiftAltListener
import com.lightningkite.khrysalis.swift.TabWriter
import com.lightningkite.khrysalis.typescript.TypescriptAltListener
import com.lightningkite.khrysalis.utils.forEachBetween
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.atn.ATNConfigSet
import org.antlr.v4.runtime.dfa.DFA
import org.antlr.v4.runtime.tree.ParseTreeWalker
import org.jetbrains.kotlin.KotlinLexer
import org.jetbrains.kotlin.KotlinParser
import org.jetbrains.kotlin.KotlinParserBaseListener
import org.jetbrains.kotlin.KotlinParserListener
import java.io.File
import java.io.FileInputStream
import java.util.*
import kotlin.collections.ArrayList

class TypescriptStubEmitter(val swiftAltListener: TypescriptAltListener, val out: TabWriter) {
    init {
        out.line("//Stub file made with Khrysalis 2 (by Lightning Kite)")
        out.line("")
    }

    val owningStack = ArrayList<Pair<String, ParserRuleContext>>()

    fun section(text: String) {
        out.line("")
        out.section(owningStack.asSequence().map { it.first }.plus(text).joinToString("."))
    }

    fun handleKotlinFile(ctx: KotlinParser.KotlinFileContext){
        ctx.topLevelObject().forEach {
            it.declaration()?.let { handleDeclaration(it) }
        }
    }

    fun handleTypeAlias(ctx: KotlinParser.TypeAliasContext) {
        if (!ctx.visibility().isExposed) return
        section(ctx.simpleIdentifier().text)
        out.line("public typealias ${ctx.simpleIdentifier().text} = Void")
    }

    fun handleDeclaration(ctx: KotlinParser.DeclarationContext){
        ctx.classDeclaration()?.let { handleClassDeclaration(it) }
        ctx.objectDeclaration()?.let { handleObjectDeclaration(it) }
        ctx.functionDeclaration()?.let { handleFunctionDeclaration(it) }
        ctx.propertyDeclaration()?.let { handlePropertyDeclaration(it) }
        ctx.typeAlias()?.let { handleTypeAlias(it) }
    }

    fun handleClassBody(ctx: KotlinParser.ClassBodyContext) {
        ctx.classMemberDeclarations()?.classMemberDeclaration()?.forEach {
            it.companionObject()?.let { handleCompanionObject(it) }
//            it.secondaryConstructor()?.let { handleSecondaryConstructor(it) }
            it.declaration()?.let { handleDeclaration(it) }
        }
    }

    fun handleCompanionObject(ctx: KotlinParser.CompanionObjectContext) {
        if (!ctx.visibility().isExposed) return
        owningStack.add("Companion" to ctx)
        section("{")

        ctx.classBody()?.let { handleClassBody(it) }

        section("}")
        owningStack.removeAt(owningStack.lastIndex)
    }

    fun handleObjectDeclaration(ctx: KotlinParser.ObjectDeclarationContext) {
        if (!ctx.visibility().isExposed) return
        owningStack.add(ctx.simpleIdentifier().text to ctx)
        section("{")
        out.line("public enum ${ctx.simpleIdentifier().text} {")
        out.tabIn()

        ctx.classBody()?.let { handleClassBody(it) }

        section("}")
        out.tabOut()
        out.line("}")
        owningStack.removeAt(owningStack.lastIndex)
    }

    fun handleClassDeclaration(ctx: KotlinParser.ClassDeclarationContext) {
        if (!ctx.visibility().isExposed) return
        if(ctx.INTERFACE() != null) {
            section(ctx.simpleIdentifier().text)
            out.line("public protocol ${ctx.simpleIdentifier().text} {}")
            return
        }
        owningStack.add(ctx.simpleIdentifier().text to ctx)
        section("{")
        out.line("public class ${ctx.simpleIdentifier().text} {")
        out.tabIn()

        ctx.primaryConstructor()?.let {
            section("Primary Constructor")
            out.line {
                append("public init(")
                it.classParameters()?.classParameter()?.forEachBetween(
                    forItem = {
                        append(it.simpleIdentifier().text)
                        append(": ")
                        with(swiftAltListener) {
                            out.write(it.type())
                        }
                    },
                    between = { append(", ") }
                )
                append(") {")
            }
            out.tab{
                out.line("TODO()")
            }
            out.line("}")
        }

        ctx.classBody()?.let { handleClassBody(it) }

        section("}")
        out.tabOut()
        out.line("}")
        owningStack.removeAt(owningStack.lastIndex)
    }

    fun handlePropertyDeclaration(ctx: KotlinParser.PropertyDeclarationContext) {
        if (!ctx.visibility().isExposed) return
        ctx.receiverType()?.let {
            section(it.text + "." + ctx.variableDeclaration().simpleIdentifier().text)
        } ?: run {
            section(ctx.variableDeclaration().simpleIdentifier().text)
        }
        ctx.receiverType()?.let {
            out.line {
                append("public extension ")
                with(swiftAltListener) {
                    out.write(it.typeReference()!!)
                }
                append(" {")
            }
            out.tabIn()
        }
        out.line {
            if(ctx.receiverType() == null) {
                append("public ")
                if (owningStack.lastOrNull()?.second !is KotlinParser.ClassDeclarationContext) {
                    append("static ")
                }
            }
            append("var ")
            append(ctx.variableDeclaration().simpleIdentifier().text)
            append(": ")
            with(swiftAltListener) {
                ctx.variableDeclaration().type()?.let { out.write(it) } ?: run { append("Void") }
            }
            append(" {")
        }
        out.tab {
            if (ctx.VAR() != null) {
                out.line("get {")
                out.tab {
                    out.line("TODO()")
                }
                out.line("}")
                out.line("set(value) {")
                out.tab {
                    out.line("TODO()")
                }
                out.line("}")
            } else {
                out.line("TODO()")
            }
        }
        out.line("}")
        ctx.receiverType()?.let {
            out.tabOut()
            out.line("}")
        }
    }

    fun handleFunctionDeclaration(ctx: KotlinParser.FunctionDeclarationContext) {
        if (!ctx.visibility().isExposed) return

        ctx.receiverType()?.let {
            section(it.text + "." + ctx.simpleIdentifier().text + (ctx.functionValueParameters()?.functionValueParameter() ?: listOf()).joinToString(", ", "(", ")"){
                it.parameter()?.type()?.text?.filter { it != '\n' } ?: "???"
            })
        } ?: run {
            section(ctx.simpleIdentifier().text + (ctx.functionValueParameters()?.functionValueParameter() ?: listOf()).joinToString(", ", "(", ")"){
                it.parameter()?.type()?.text?.filter { it != '\n' } ?: "???"
            })
        }

        ctx.receiverType()?.let {
            if(it.typeReference() == null) return
            out.line {
                append("public extension ")
                with(swiftAltListener) {
                    out.write(it.typeReference()!!)
                }
                append(" {")
            }
            out.tabIn()
        }
        out.line {
            if(ctx.receiverType() == null) {
                append("public ")
                if (owningStack.lastOrNull()?.second !is KotlinParser.ClassDeclarationContext) {
                    append("static ")
                }
            }
            append("func ")
            append(ctx.simpleIdentifier().text)
            ctx.typeParameters()?.typeParameter()?.let {
                append("<")
                it.forEachBetween(
                    forItem = {
                        append(it.simpleIdentifier().text)
                    },
                    between = {
                        append(", ")
                    }
                )
                append(">")
            }
            append("(")
            ctx.functionValueParameters()?.functionValueParameter()?.forEachBetween(
                forItem = {
                    append("_ ")
                    append(it.parameter().simpleIdentifier().text)
                    append(": ")
                    with(swiftAltListener) {
                        out.write(it.parameter().type())
                    }
                },
                between = { append(", ") }
            )
            append(") -> ")
            with(swiftAltListener) {
                ctx.type()?.let { out.write(it) } ?: run { append("Void") }
            }
            append(" {")
        }
        out.tab {
            out.line("TODO()")
        }
        out.line("}")

        val needsAlternateWriting = ctx.functionValueParameters()?.functionValueParameter()?.let {
            it.size >= 2 || (it.size == 1 && it.lastOrNull()?.parameter()?.type()?.functionType() == null)
        } ?: false
        if(needsAlternateWriting) {
            out.line {
                if(ctx.receiverType() == null) {
                    append("public ")
                    if (owningStack.lastOrNull()?.second is KotlinParser.ObjectDeclarationContext) {
                        append("static ")
                    }
                }
                append("func ")
                append(ctx.simpleIdentifier().text)
                ctx.typeParameters()?.typeParameter()?.let {
                    append("<")
                    it.forEachBetween(
                        forItem = {
                            append(it.simpleIdentifier().text)
                        },
                        between = {
                            append(", ")
                        }
                    )
                    append(">")
                }
                append("(")
                ctx.functionValueParameters()?.functionValueParameter()?.forEachBetween(
                    forItem = {
                        append(it.parameter().simpleIdentifier().text)
                        append(": ")
                        with(swiftAltListener) {
                            out.write(it.parameter().type())
                        }
                    },
                    between = { append(", ") }
                )
                append(") -> ")
                with(swiftAltListener) {
                    ctx.type()?.let { out.write(it) } ?: run { append("Void") }
                }
                append(" {")
            }
            out.tab {
                out.line {
                    append("return ")
                    append(ctx.simpleIdentifier().text)
                    append("(")
                    ctx.functionValueParameters()?.functionValueParameter()?.forEachBetween(
                        forItem = {
                            append(it.parameter().simpleIdentifier().text)
                        },
                        between = { append(", ") }
                    )
                    append(")")
                }
            }
            out.line("}")
        }
        ctx.receiverType()?.let {
            out.tabOut()
            out.line("}")
        }
    }
}

fun File.typescriptStubs(swift: TypescriptAltListener, to: File) {
    var inUndoneComment = false
    val correctedText = useLines {
        it.map {
            (if (inUndoneComment) {
                if (it.trim().startsWith("*/")) {
                    inUndoneComment = false
                    ""
                } else {
                    it
                }
            } else {
                if (it.trim().startsWith("/* SHARED DECLARATIONS")) {
                    inUndoneComment = true
                    ""
                } else {
                    it
                }
            }).replace("@PlatformSpecific", "private")
        }.joinToString("\n")
    }
    try {
        val lexer = KotlinLexer(ANTLRInputStream(correctedText))
        val tokenStream = CommonTokenStream(lexer)
        val parser = KotlinParser(tokenStream)
        var errorOccurred = false
        parser.addErrorListener(object : ANTLRErrorListener {
            override fun reportAttemptingFullContext(
                p0: Parser?,
                p1: DFA?,
                p2: Int,
                p3: Int,
                p4: BitSet?,
                p5: ATNConfigSet?
            ) {
            }

            override fun syntaxError(
                p0: Recognizer<*, *>?,
                p1: Any?,
                p2: Int,
                p3: Int,
                p4: String?,
                p5: RecognitionException?
            ) {
                errorOccurred = true
            }

            override fun reportAmbiguity(
                p0: Parser?,
                p1: DFA?,
                p2: Int,
                p3: Int,
                p4: Boolean,
                p5: BitSet?,
                p6: ATNConfigSet?
            ) {
            }

            override fun reportContextSensitivity(
                p0: Parser?,
                p1: DFA?,
                p2: Int,
                p3: Int,
                p4: Int,
                p5: ATNConfigSet?
            ) {
            }
        })
        if(errorOccurred) throw Exception("Parsing failed")
        val result = buildString {
            TypescriptStubEmitter(swift, TabWriter(this)).handleKotlinFile(parser.kotlinFile())
        }
        to.writeText(CodeSection.merge(to.takeIf { it.exists() }?.readText() ?: "", result))
    } catch (e: Exception) {
        throw Exception("Failed to create stubs parsing $correctedText", e)
    }
}

internal enum class Visibility(val isExposed: Boolean) {
    Private(false),
    Internal(false),
    Protected(true),
    Public(true)
}

internal fun KotlinParser.ModifiersContext.visibility2(): Visibility {
    val v = this.modifier()?.asSequence()?.mapNotNull { it.visibilityModifier() }?.firstOrNull()
    return when {
        v == null -> Visibility.Public
        v.INTERNAL() != null -> Visibility.Internal
        v.PRIVATE() != null -> Visibility.Private
        v.PROTECTED() != null -> Visibility.Protected
        v.PUBLIC() != null -> Visibility.Public
        else -> Visibility.Public
    }
}

internal fun RuleContext.visibility(): Visibility {
    if (this is KotlinParser.ModifiersContext) return this.visibility2()
    if (this is ParserRuleContext) this.getRuleContext(
        KotlinParser.ModifiersContext::class.java,
        0
    )?.let { return it.visibility2() }
    this.parent?.let { return it.visibility() }
    return Visibility.Public
}


