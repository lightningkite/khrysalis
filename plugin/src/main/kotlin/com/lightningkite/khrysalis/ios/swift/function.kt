package com.lightningkite.khrysalis.ios.swift

import com.lightningkite.khrysalis.ios.swift.TabWriter
import com.lightningkite.khrysalis.utils.forEachBetween
import com.lightningkite.khrysalis.utils.forEachBetweenIndexed
import org.antlr.v4.runtime.ParserRuleContext
import org.jetbrains.kotlin.KotlinParser
import java.lang.IllegalStateException


fun SwiftAltListener.handleFunctionBodyAfterOpeningBrace(writer: TabWriter, item: KotlinParser.FunctionBodyContext) {
    with(writer) {
        tab {
            item.expression()?.let {
                line {
                    append("return ")
                    write(it)
                }
            }
            item.block()?.statements()?.let {
                it.statement().forEach {
                    startLine()
                    write(it)
                }
            }
        }
        line("}")
    }
}

fun SwiftAltListener.handleNormalFunction(
    tabWriter: TabWriter,
    item: KotlinParser.FunctionDeclarationContext,
    excludeBody: Boolean = false,
    usingTypeParameters: List<KotlinParser.TypeParameterContext>? = item.typeParameters()?.typeParameter()
) = with(tabWriter) {

    val myName = item.simpleIdentifier().text
    val owningClass = item.parentIfType<KotlinParser.DeclarationContext>()
        ?.parentIfType<KotlinParser.ClassMemberDeclarationContext>()
        ?.parentIfType<KotlinParser.ClassMemberDeclarationsContext>()
        ?.parentIfType<KotlinParser.ClassBodyContext>()
        ?.parentIfType<KotlinParser.ClassDeclarationContext>()
    val owningCompanion = item.parentIfType<KotlinParser.DeclarationContext>()
        ?.parentIfType<KotlinParser.ClassMemberDeclarationContext>()
        ?.parentIfType<KotlinParser.ClassMemberDeclarationsContext>()
        ?.parentIfType<KotlinParser.ClassBodyContext>()
        ?.parentIfType<KotlinParser.CompanionObjectContext>()
    val owningObject = item.parentIfType<KotlinParser.DeclarationContext>()
        ?.parentIfType<KotlinParser.ClassMemberDeclarationContext>()
        ?.parentIfType<KotlinParser.ClassMemberDeclarationsContext>()
        ?.parentIfType<KotlinParser.ClassBodyContext>()
        ?.parentIfType<KotlinParser.ObjectDeclarationContext>()
    val isTopLevel = item.parentIfType<KotlinParser.DeclarationContext>()
        ?.parentIfType<KotlinParser.TopLevelObjectContext>() != null
    val originalUsesOverride =
        item.modifiers()?.modifier()?.any { it.memberModifier()?.OVERRIDE() != null } ?: false
    val needsOverrideKeyword = originalUsesOverride && owningClass?.implements()
        ?.any { myName in it.methods } != true

    fun Appendable.writeFunctionHeader(addUnderscore: Boolean) {
        if (needsOverrideKeyword) append("override ")
        item.modifiers()?.annotation()?.forEach {
            write(it)
            append(' ')
        }
        if (owningClass != null && owningClass.INTERFACE() == null || isTopLevel) {
            append(item.modifiers().visibilityString())
            append(" ")
        } else if (owningCompanion != null || owningObject != null) {
            append("static ")
            append(item.modifiers().visibilityString())
            append(" ")
        }
        append("func ")
        append(item.simpleIdentifier().text)
        usingTypeParameters?.let {
            writeTypeArguments(tabWriter, it, item.typeConstraints())
        }
        append("(")
        item.functionValueParameters().functionValueParameter().forEachBetweenIndexed(
            forItem = { index, it ->
                if (addUnderscore) {
                    append("_ ")
                }
                append(it.parameter().simpleIdentifier().text)
                append(": ")
                write(it.parameter().type())
                if (!addUnderscore || index != 0) {
                    it.expression()?.let {
                        append(" = ")
                        write(it)
                    }
                }
            },
            between = { append(", ") }
        )
        append(") -> ")

        item.type()?.let {
            write(it)
        } ?: run {
            append("Void")
        }
    }

    val isAbstract = item.modifiers()?.modifier()?.any { it.inheritanceModifier()?.ABSTRACT() != null } == true
    line {
        writeFunctionHeader(false)

        if (isAbstract) {
            append(" { fatalError() }")
        } else if (!excludeBody && item.functionBody() != null) {
            append(" {")
        }
    }

    if (!isAbstract && !excludeBody) {
        item.functionBody()?.let { handleFunctionBodyAfterOpeningBrace(this, it) }
    }

    val needsAlternateWriting = !item.functionValueParameters().functionValueParameter().let {
        it.size == 0 || (it.size == 1 && it.first()?.parameter()?.type()?.getUnderlyingType()?.functionType() != null)
    }
    if (needsAlternateWriting) {
        line {
            writeFunctionHeader(true)

            if (isAbstract) {
                append(" { fatalError() }")
            } else if (!excludeBody && item.functionBody() != null) {
                append(" {")
            }
        }
        if (!isAbstract && !excludeBody) {
            tab {
                line {
                    append("return ${item.simpleIdentifier().text}(")
                    item.functionValueParameters().functionValueParameter().forEachBetween(
                        forItem = {
                            append(it.parameter().simpleIdentifier().text)
                            append(": ")
                            append(it.parameter().simpleIdentifier().text)
                        },
                        between = { append(", ") }
                    )
                    append(")")
                }
            }
            line("}")
        }
    }
}

fun SwiftAltListener.writeTypeArguments(
    writer: TabWriter,
    it: List<KotlinParser.TypeParameterContext>,
    whereClause: KotlinParser.TypeConstraintsContext?
) = with(writer) {
    direct.append("<")
    it.forEachBetween(
        forItem = {
            val name = it.simpleIdentifier().text
            direct.append(name)
            it.type()?.let {
                direct.append(": ")
                write(it)
            }
            whereClause?.typeConstraint()?.filter { it.simpleIdentifier().text == name }?.map { it.type() }?.let {
                direct.append(": ")
                it.forEachBetween(
                    forItem = { write(it) },
                    between = { direct.append(" & ") }
                )
            }
        },
        between = { direct.append(", ") }
    )
    direct.append(">")
}

fun List<KotlinParser.AnnotationContext>.get(name: String): List<String>? {
    return find {
        it.singleAnnotation()?.unescapedAnnotation()?.userType()?.text == name ||
                it.singleAnnotation()?.unescapedAnnotation()?.constructorInvocation()?.userType()?.text == name
    }?.let {
        it.singleAnnotation()!!.unescapedAnnotation()!!.constructorInvocation()?.valueArguments()?.valueArgument()
            ?.map { it.text }
            ?: listOf()
    }
}

fun KotlinParser.ReceiverTypeContext.typeParamName(
    type: KotlinParser.TypeContext,
    annotations: List<KotlinParser.AnnotationContext>? = type.typeModifiers()?.typeModifier()
        ?.mapNotNull { it.annotation() },
    totalCount: Int,
    index: Int
): String {
    val receiverWithoutParameters = getUserType().simpleUserType()?.joinToString(".") { it.simpleIdentifier()!!.text }
        ?: ""
    return annotations?.let {
        println("Modifiers: ${it.joinToString { it.text }}")
        it.get("swiftExactly")?.firstOrNull()?.trim('"') ?: it.get("swiftDescendsFrom")?.firstOrNull()?.trim('"')
    } ?: when (totalCount) {
        0 -> "T"
        1 -> when (receiverWithoutParameters) {
            "Sequence" -> "Element"
            "List" -> "Element"
            else -> "T"
        }
        2 -> when (receiverWithoutParameters) {
            "Map" -> when (index) {
                0 -> "Key"
                1 -> "Value"
                else -> "T"
            }
            else -> ('A' + index).toString()
        }
        else -> ('A' + index).toString()
    }
}

fun KotlinParser.ReceiverTypeContext.typeParamFinal(
    type: KotlinParser.TypeContext,
    annotations: List<KotlinParser.AnnotationContext>? = type.typeModifiers()?.typeModifier()
        ?.mapNotNull { it.annotation() }
): Boolean {
    return annotations?.let {
        when {
            it.get("swiftExactly") != null -> true
            it.get("swiftDescendsFrom") != null -> false
            else -> null
        }
    } ?: when (type.text) {
        "Unit",
        "Boolean",
        "Int",
        "Byte",
        "Short",
        "Long",
        "Float",
        "Double",
        "String",
        "Char" -> true
        else -> false
    }
}

fun SwiftAltListener.registerFunction() {
    handle<KotlinParser.ParameterContext> { item ->
        direct.append(item.simpleIdentifier().text)
        direct.append(": ")
        write(item.type())
    }

    fun TabWriter.handleExtensionFunction(item: KotlinParser.FunctionDeclarationContext) {
        val otherTypeArguments = startExtension(this, item.typeParameters(), item.receiverType())
        tab {
            handleNormalFunction(
                this,
                item,
                usingTypeParameters = item.typeParameters()?.typeParameter()
                    ?.filter { it.simpleIdentifier().text in otherTypeArguments }?.takeUnless { it.isEmpty() }
            )
        }
        line("}")
    }

    handle<KotlinParser.FunctionDeclarationContext> { item ->
        //Handle operators
        val owningClass = item.parentIfType<KotlinParser.DeclarationContext>()
            ?.parentIfType<KotlinParser.ClassMemberDeclarationContext>()
            ?.parentIfType<KotlinParser.ClassMemberDeclarationsContext>()
            ?.parentIfType<KotlinParser.ClassBodyContext>()
            ?.parentIfType<KotlinParser.ClassDeclarationContext>()
        run {
            if (item.modifiers()?.modifier()?.any { it.functionModifier()?.OPERATOR() != null } == true) {
                val tabWriter = this
                val (isBinary, operator) = when (item.simpleIdentifier()?.text) {
                    "plus" -> true to "+"
                    "minus" -> true to "-"
                    "times" -> true to "*"
                    "div" -> true to "/"
                    "rem" -> true to "%"
                    "unaryMinus" -> false to "-"
                    "unaryPlus" -> false to "+"
                    "not" -> false to "!"
                    else -> {
                        return@run
                    }
                }
                val param = item.functionValueParameters().functionValueParameter(0)
                if (owningClass != null) {
                    if (isBinary) {
                        line {
                            append("static func $operator(receiver: ")
                            append(owningClass.simpleIdentifier()?.text)
                            append(", ")
                            append(param.parameter().simpleIdentifier().text)
                            append(": ")
                            write(param.parameter().type())
                            append(") -> ")
                            write(item.type())
                            append(" { return receiver.")
                            append(item.simpleIdentifier().text)
                            append("(")
                            append(param.parameter().simpleIdentifier().text)
                            append(": ")
                            append(param.parameter().simpleIdentifier().text)
                            append(") }")
                        }
                    } else {
                        line {
                            append("static prefix func $operator(receiver: ")
                            append(owningClass.simpleIdentifier()?.text)
                            append(") -> ")
                            write(item.type())
                            append(" { return receiver.")
                            append(item.simpleIdentifier().text)
                            append("() }")
                        }
                    }
                } else {
                    item.receiverType()?.let { receiver ->
                        if (isBinary) {
                            line {
                                append("func $operator(self: ")
                                write(receiver)
                                append(", ")
                                append(param.parameter().simpleIdentifier().text)
                                append(": ")
                                write(param.parameter().type())
                                append(") -> ")
                                write(item.type())
                                append(" { return self.")
                                append(item.simpleIdentifier().text)
                                append("(")
                                append(param.parameter().simpleIdentifier().text)
                                append(": ")
                                append(param.parameter().simpleIdentifier().text)
                                append(") }")
                            }
                        } else {
                            line {
                                append("prefix func $operator(self: ")
                                write(receiver)
                                append(") -> ")
                                write(item.type())
                                append(" { return self.")
                                append(item.simpleIdentifier().text)
                                append("() }")
                            }
                        }
                    }
                }
            }
        }

        //Normal
        if (item.receiverType() != null) this.handleExtensionFunction(item)
        else handleNormalFunction(this, item)
    }
    handle<KotlinParser.FunctionValueParametersContext> {
        direct.append("(")
        it.functionValueParameter().forEachBetween(
            forItem = { write(it) },
            between = { direct.append(", ") }
        )
        direct.append(")")
    }
    handle<KotlinParser.CallSuffixContext> { ctx ->
        val argList = ArrayList<() -> Unit>()
        ctx.typeArguments()?.let {
            val dontUseFoldedTypeArgs = ctx
                .parentIfType<KotlinParser.PostfixUnarySuffixContext>()
                ?.parentIfType<KotlinParser.PostfixUnaryExpressionContext>()
                ?.takeIf { it.postfixUnarySuffix(0).sourceInterval.a == ctx.sourceInterval.a }
                ?.primaryExpression()
                ?.text
                ?.split('.')
                ?.all {
                    it.firstOrNull()?.isUpperCase() == true && it.all { it.isJavaIdentifierPart() || it == '.' }
                } == true
            if (dontUseFoldedTypeArgs) {
                write(it)
            } else {
                argList.addAll(it.typeProjection().map { it -> fun(): Unit { write(it); direct.append(".self") } })
            }
        }
        ctx.valueArguments()?.let {
            val sampleHasNoLabel = it.valueArgument().firstOrNull()?.MULT() == null
            if (it.valueArgument().any { (it.MULT() == null) != sampleHasNoLabel }) {
                println("WARNING: Function call at line ${it.start.line} has some arguments with keys and some without.  This is not supported by the standard function definition converter.")
            }
            argList.addAll(it.valueArgument().map { it -> { -> write(it) } })
            direct.append("(")
            argList.forEachBetween(
                forItem = { it() },
                between = { direct.append(", ") }
            )
            direct.append(")")
        }
        ctx.annotatedLambda()?.let { write(it) }
    }
    handle<KotlinParser.ValueArgumentsContext> {
        val sampleHasNoLabel = it.valueArgument().firstOrNull()?.MULT() == null
        if (it.valueArgument().any { (it.MULT() == null) != sampleHasNoLabel }) {
            println("WARNING: Function call at line ${it.start.line} has some arguments with keys and some without.  This is not supported by the standard function definition converter.")
        }
        direct.append("(")
        it.valueArgument().forEachBetween(
            forItem = { write(it) },
            between = { direct.append(", ") }
        )
        direct.append(")")
    }
    handle<KotlinParser.ValueArgumentContext> {
        it.simpleIdentifier()?.let {
            direct.append(it.text + ": ")
        }
        write(it.expression())
    }
    handle<KotlinParser.TypeParametersContext> {
        writeTypeArguments(this, it.typeParameter(), null)
    }
}
