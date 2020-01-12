package com.lightningkite.kwift.swift

import com.lightningkite.kwift.swift.TabWriter
import com.lightningkite.kwift.utils.forEachBetween
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.RuleContext
import org.jetbrains.kotlin.KotlinParser

fun SwiftAltListener.registerVariable() {
    handle<KotlinParser.PropertyDeclarationContext> { item ->

        item.receiverType()?.let {
            startExtension(this, item.typeParameters(), it)
            tabIn()
            startLine()
        }

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

        val myName = item.variableDeclaration().simpleIdentifier().text
        val originalUsesOverride =
            item.modifiers()?.modifier()?.any { it.memberModifier()?.OVERRIDE() != null } ?: false
        val needsOverrideKeyword = originalUsesOverride && owningClass?.implements()
            ?.any { myName in it.properties } != true
        val isTopLevel = item.parentIfType<KotlinParser.DeclarationContext>()
            ?.parentIfType<KotlinParser.TopLevelObjectContext>() != null

        val isAbstract = item.modifiers()?.modifier()?.any { it.inheritanceModifier()?.ABSTRACT() != null } == true

        var initialSetExpression = item.expression()
        var useWeak = false
        item.propertyDelegate()?.expression()?.let {
            val x = it.disjunction()
                ?.conjunction()?.oneOnly()
                ?.equality()?.oneOnly()
                ?.comparison()?.oneOnly()
                ?.infixOperation()?.oneOnly()
                ?.elvisExpression()?.oneOnly()
                ?.infixFunctionCall()?.oneOnly()
                ?.rangeExpression()?.oneOnly()
                ?.additiveExpression()?.oneOnly()
                ?.multiplicativeExpression()?.oneOnly()
                ?.asExpression()?.oneOnly()
                ?.prefixUnaryExpression()
                ?.postfixUnaryExpression()
            if (x?.primaryExpression()?.text == "weak") {
                initialSetExpression =
                    x.postfixUnarySuffix()?.oneOnly()?.callSuffix()?.valueArguments()?.valueArgument(0)?.expression()
                useWeak = true
            }
        }

        if (owningClass != null && needsOverrideKeyword && initialSetExpression != null) {
            owningClass.additionalDeclarations.add { writer ->
                with(writer) {
                    line {
                        if (owningCompanion != null) {
                            append("static ")
                        }
                        append("private var _$myName")
                        item.variableDeclaration().type()?.let {
                            append(": ")
                            write(it)
                        }
                    }
                }
            }
            startLine()
        }

        with(direct) {
            if (needsOverrideKeyword) {
                append("override ")
            }
            if (owningClass != null || isTopLevel) {
                append(item.modifiers().visibilityString())
                append(" ")
            } else if (owningCompanion != null || owningObject != null) {
                append("static ")
                append(item.modifiers().visibilityString())
                append(" ")
            }
            if (useWeak) {
                append("weak ")
            }
            append("var ")
            append(myName)
            item.variableDeclaration().type()?.let {
                append(": ")
                write(it)
            }
            initialSetExpression?.let {
                if (owningClass != null) {
                    if (needsOverrideKeyword) {
                        append(" { get { return _$myName } set(value) { _$myName = value } }")
                        owningClass.additionalInits.add { writer ->
                            with(writer) {
                                line {
                                    append("self._")
                                    append(myName)
                                    append(" = ")
                                    write(it)
                                }
                            }
                        }
                    } else {
                        owningClass.additionalInits.add { writer ->
                            with(writer) {
                                if (myName in owningClass.constructorParameterNames()) {
                                    line {
                                        append("self.")
                                        append(myName)
                                        append(" = ")
                                        write(it)
                                    }
                                } else {
                                    line {
                                        append("let ")
                                        append(myName)
                                        item.variableDeclaration().type()?.let {
                                            append(": ")
                                            write(it)
                                        }
                                        append(" = ")
                                        write(it)
                                    }
                                    line {
                                        append("self.")
                                        append(myName)
                                        append(" = ")
                                        append(myName)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    append(" = ")
                    write(it)
                }
            }
        }

        if (isAbstract) {
            with(direct) {
                if (item.VAL() != null) {
                    append(" { get { fatalError() } }")
                } else {
                    append(" { get { fatalError() } set(value) { fatalError()  } }")
                }
            }
        } else {
            item.getter()?.let { getter ->
                direct.append(" {")
                tab {
                    line("get {")
                    handleFunctionBodyAfterOpeningBrace(this, getter.functionBody())

                    item.setter()?.let { setter ->
                        line("set(value) {")
                        handleFunctionBodyAfterOpeningBrace(this, setter.functionBody())
                    }
                }
                line("}")
            }
        }

        item.receiverType()?.let {
            tabOut()
            line("}")
        }
    }
}
