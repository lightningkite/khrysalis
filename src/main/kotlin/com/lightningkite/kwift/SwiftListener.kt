package com.lightningkite.kwift

import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.TerminalNode
import org.jetbrains.kotlin.KotlinParser

class SwiftListener(
    tokenStream: CommonTokenStream,
    parser: KotlinParser,
    val interfaces: List<InterfaceListener.InterfaceData>
) : RewriteListener(tokenStream, parser) {

    val handleFunctionReplacement = HashMap<String, SwiftListener.() -> Unit>()

    init {
        handleFunctionReplacement["listOf"] = {
            val valueArguments = text(KotlinParser.RULE_valueArguments)
            if (valueArguments != null && valueArguments.length > 2) {
                overridden = default.copy(
                    text = "[" + text(KotlinParser.RULE_valueArguments)
                        ?.removePrefix("(")
                        ?.removeSuffix(")")
                            + "]"
                )
            } else {
                overridden = default.copy(
                    text = "[" + text(KotlinParser.RULE_typeArguments)
                        ?.removePrefix("<")
                        ?.removeSuffix(">")
                            + "]()"
                )
            }
        }
        handleFunctionReplacement["arrayOf"] = {
            val valueArguments = text(KotlinParser.RULE_valueArguments)
            if (valueArguments != null && valueArguments.length > 2) {
                overridden = default.copy(
                    text = "[" + text(KotlinParser.RULE_valueArguments)
                        ?.removePrefix("(")
                        ?.removeSuffix(")")
                            + "]"
                )
            } else {
                overridden = default.copy(
                    text = "[" + text(KotlinParser.RULE_typeArguments)
                        ?.removePrefix("<")
                        ?.removeSuffix(">")
                            + "]()"
                )
            }
        }
        handleFunctionReplacement["arrayListOf"] = {
            val valueArguments = text(KotlinParser.RULE_valueArguments)
            if (valueArguments != null && valueArguments.length > 2) {
                overridden = default.copy(
                    text = "[" + text(KotlinParser.RULE_valueArguments)
                        ?.removePrefix("(")
                        ?.removeSuffix(")")
                            + "]"
                )
            } else {
                overridden = default.copy(
                    text = "[" + text(KotlinParser.RULE_typeArguments)
                        ?.removePrefix("<")
                        ?.removeSuffix(">")
                            + "]()"
                )
            }
        }
        handleFunctionReplacement["mutableListOf"] = {
            val valueArguments = text(KotlinParser.RULE_valueArguments)
            if (valueArguments != null && valueArguments.length > 2) {
                overridden = default.copy(
                    text = "[" + text(KotlinParser.RULE_valueArguments)
                        ?.removePrefix("(")
                        ?.removeSuffix(")")
                            + "]"
                )
            } else {
                overridden = default.copy(
                    text = "[" + text(KotlinParser.RULE_typeArguments)
                        ?.removePrefix("<")
                        ?.removeSuffix(">")
                            + "]()"
                )
            }
        }
        handleFunctionReplacement["mapOf"] = {
            val valueArguments = text(KotlinParser.RULE_valueArguments)
            if (valueArguments != null && valueArguments.length > 2) {
                overridden = default.copy(
                    text = "[" + text(KotlinParser.RULE_valueArguments)
                        ?.removePrefix("(")
                        ?.removeSuffix(")")
                        ?.replace(" to ", ":") + "]"
                )
            } else {
                overridden = default.copy(
                    text = "[" + text(KotlinParser.RULE_typeArguments)
                        ?.removePrefix("<")
                        ?.removeSuffix(">")
                        ?.replace(",", ":") + "]"
                            + "]()"
                )
            }
        }
        handleFunctionReplacement["mutableMapOf"] = {
            val valueArguments = text(KotlinParser.RULE_valueArguments)
            if (valueArguments != null && valueArguments.length > 2) {
                overridden = default.copy(
                    text = "[" + text(KotlinParser.RULE_valueArguments)
                        ?.removePrefix("(")
                        ?.removeSuffix(")")
                        ?.replace(" to ", ":") + "]"
                )
            } else {
                overridden = default.copy(
                    text = "[" + text(KotlinParser.RULE_typeArguments)
                        ?.removePrefix("<")
                        ?.removeSuffix(">")
                        ?.replace(",", ":") + "]"
                            + "]()"
                )
            }
        }
        handleFunctionReplacement["Array"] = {
            overridden =
                    default.copy(text = "[" + text(KotlinParser.RULE_typeArguments)?.removePrefix("<")?.removeSuffix(">") + "]()")
        }
        handleFunctionReplacement["List"] = {
            overridden =
                    default.copy(text = "[" + text(KotlinParser.RULE_typeArguments)?.removePrefix("<")?.removeSuffix(">") + "]()")
        }
        handleFunctionReplacement["ArrayList"] = {
            overridden =
                    default.copy(text = "[" + text(KotlinParser.RULE_typeArguments)?.removePrefix("<")?.removeSuffix(">") + "]()")
        }
        handleFunctionReplacement["Map"] = {
            overridden = default.copy(
                text = "[" + text(KotlinParser.RULE_typeArguments)?.removePrefix("<")?.removeSuffix(">")?.replace(
                    ",",
                    ":"
                ) + "]()"
            )
        }
        handleFunctionReplacement["HashMap"] = {
            overridden = default.copy(
                text = "[" + text(KotlinParser.RULE_typeArguments)?.removePrefix("<")?.removeSuffix(">")?.replace(
                    ",",
                    ":"
                ) + "]()"
            )
        }
        handleFunctionReplacement["LinkedHashMap"] = {
            overridden = default.copy(
                text = "[" + text(KotlinParser.RULE_typeArguments)?.removePrefix("<")?.removeSuffix(">")?.replace(
                    ",",
                    ":"
                ) + "]()"
            )
        }
        handleFunctionReplacement["LinkedHashMap"] = {
            overridden = default.copy(
                text = "[" + text(KotlinParser.RULE_typeArguments)?.removePrefix("<")?.removeSuffix(">")?.replace(
                    ",",
                    ":"
                ) + "]()"
            )
        }
    }

    val handleTypeReplacement = HashMap<String, SwiftListener.() -> Unit>()

    init {
        handleTypeReplacement["Array"] = {
            overridden =
                    default.copy(text = "[" + text(KotlinParser.RULE_typeArguments)?.removePrefix("<")?.removeSuffix(">") + "]")
        }
        handleTypeReplacement["List"] = {
            overridden =
                    default.copy(text = "[" + text(KotlinParser.RULE_typeArguments)?.removePrefix("<")?.removeSuffix(">") + "]()")
        }
        handleTypeReplacement["ArrayList"] = {
            overridden =
                    default.copy(text = "[" + text(KotlinParser.RULE_typeArguments)?.removePrefix("<")?.removeSuffix(">") + "]()")
        }
        handleTypeReplacement["Map"] = {
            overridden = default.copy(
                text = "[" + text(KotlinParser.RULE_typeArguments)?.removePrefix("<")?.removeSuffix(">")?.replace(
                    ",",
                    ":"
                ) + "]()"
            )
        }
        handleTypeReplacement["HashMap"] = {
            overridden = default.copy(
                text = "[" + text(KotlinParser.RULE_typeArguments)?.removePrefix("<")?.removeSuffix(">")?.replace(
                    ",",
                    ":"
                ) + "]()"
            )
        }
        handleTypeReplacement["LinkedHashMap"] = {
            overridden = default.copy(
                text = "[" + text(KotlinParser.RULE_typeArguments)?.removePrefix("<")?.removeSuffix(">")?.replace(
                    ",",
                    ":"
                ) + "]()"
            )
        }
        handleTypeReplacement["LinkedHashMap"] = {
            overridden = default.copy(
                text = "[" + text(KotlinParser.RULE_typeArguments)?.removePrefix("<")?.removeSuffix(">")?.replace(
                    ",",
                    ":"
                ) + "]()"
            )
        }
    }

    init {
        terminalRewrites[KotlinParser.EOF] = { "" }
        terminalRewrites[KotlinParser.LineStrRef] = { "\\(" + it.removePrefix("$") + ")" }
        terminalRewrites[KotlinParser.MultiLineStrRef] = { "\\(" + it.removePrefix("$") + ")" }
        terminalRewrites[KotlinParser.NullLiteral] = { "nil" }
        terminalRewrites[KotlinParser.TRY] = { "do" }
        terminalRewrites[KotlinParser.VAL] = { "let" }
        terminalRewrites[KotlinParser.THIS] = { "self" }
    }

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

    override fun exitWhenExpression(ctx: KotlinParser.WhenExpressionContext) {
        if (get(KotlinParser.RULE_expression) != null) {
            overridden = layers.last().asSequence().map {
                when (it.rule) {
                    -KotlinParser.WHEN -> it.copy(text = "switch")
                    else -> it
                }
            }.joinClean()
        } else {

        }
    }

    override fun exitWhenEntry(ctx: KotlinParser.WhenEntryContext) {
        val body = get(KotlinParser.RULE_controlStructureBody)?.let {
            it.copy(
                text = it.text.trim().removePrefix("{").removeSuffix("}")
            )
        } ?: Section("")
        val condition = layers.last().asSequence()
            .take(layers.last().indexOfFirst { it.rule == -KotlinParser.ARROW })
            .joinClean()
            .copy(spacingBefore = "")
        if (condition.text.contains("else")) {
            overridden = default.copy(text = "default: ${body.toOutputString()}\n")
        } else {
            overridden = default.copy(text = "case ${condition.toOutputString()}: ${body.toOutputString()}\n")
        }
    }

    override fun exitValueArgument(ctx: KotlinParser.ValueArgumentContext?) {
        overridden = layers.last().asSequence().map {
            if (it.rule == -KotlinParser.ASSIGNMENT) {
                it.copy(text = ":")
            } else it
        }.joinClean()
    }

    fun handleStringExpr() {
        overridden = layers.last().asSequence().map {
            when (it.rule) {
                -KotlinParser.MultiLineStrExprStart -> it.copy(text = "\\(")
                -KotlinParser.LineStrExprStart -> it.copy(text = "\\(")
                -KotlinParser.RCURL -> it.copy(text = ")")
                else -> it
            }
        }.joinClean()
    }

    override fun exitLineStringExpression(ctx: KotlinParser.LineStringExpressionContext?) = handleStringExpr()

    override fun exitMultiLineStringExpression(ctx: KotlinParser.MultiLineStringExpressionContext?) = handleStringExpr()

    val subPattern = "([a-zA-Z0-9]+ *!= *nil|[a-zA-Z0-9]+ +is +[a-zA-Z0-9.]+)"
    val ifLetPattern = Regex("$subPattern *(&& $subPattern)*")
    override fun exitIfExpression(ctx: KotlinParser.IfExpressionContext) {
        val ifLetMatch = text(KotlinParser.RULE_expression)?.trim()
            ?.let { ifLetPattern.matchEntire(it) }

        if (ifLetMatch != null) {
            val variables =
                text(KotlinParser.RULE_expression)!!.split("&&").map { it.split("!=").flatMap { it.split(" is ") } }
            val endPart = layers.last().asSequence()
                .drop(layers.last().indexOfFirst { it.rule == -KotlinParser.RPAREN } + 1)
            val startPart = Section(
                text = "if " + variables.joinToString {
                    val name = it.getOrElse(0) { "" }.trim()
                    val compare = it.getOrElse(1) { "" }.trim()
                    if (compare == "nil")
                        "let $name = $name"
                    else
                        "let $name = $name as? $compare"
                },
                spacingBefore = layers.last().first().spacingBefore
            )
            overridden = sequenceOf(startPart).plus(endPart).joinClean()
        } else {
            overridden = layers.last().asSequence().map {
                when (it.rule) {
                    -KotlinParser.LPAREN -> it.copy(text = " ")
                    -KotlinParser.RPAREN -> it.copy(text = "")
                    else -> it
                }
            }.joinClean()
        }
    }

    override fun exitForExpression(ctx: KotlinParser.ForExpressionContext?) {
        overridden = layers.last().asSequence().map {
            when (it.rule) {
                -KotlinParser.LPAREN -> it.copy(text = " ")
                -KotlinParser.RPAREN -> it.copy(text = " ")
                else -> it
            }
        }.joinClean()
    }

    override fun exitWhileExpression(ctx: KotlinParser.WhileExpressionContext?) {
        overridden = layers.last().asSequence().map {
            when (it.rule) {
                -KotlinParser.LPAREN -> it.copy(text = " ")
                -KotlinParser.RPAREN -> it.copy(text = " ")
                else -> it
            }
        }.joinClean()
    }

    override fun exitRangeExpression(ctx: KotlinParser.RangeExpressionContext?) {
        overridden = layers.last().asSequence().map {
            when (it.rule) {
                -KotlinParser.RANGE -> it.copy(text = "...")
                else -> it
            }
        }.joinClean()
    }

    override fun exitCallExpression(ctx: KotlinParser.CallExpressionContext?) {
        val functionCallName = text(KotlinParser.RULE_assignableExpression)?.trim()?.substringAfterLast('.')
        val alternateCallName = text(KotlinParser.RULE_assignableExpression)?.trim()
        handleFunctionReplacement[functionCallName]?.invoke(this)
            ?: handleFunctionReplacement[alternateCallName]?.invoke(this)
    }

    override fun exitFunctionDeclaration(ctx: KotlinParser.FunctionDeclarationContext) {
        overridden = layers.last().asSequence().map {
            when (it.rule) {
                -KotlinParser.FUN -> it.copy(text = "func")
                -KotlinParser.COLON -> it.copy(text = "->", spacingBefore = " ")
                KotlinParser.RULE_modifierList -> {
                    val functionName = text(KotlinParser.RULE_identifier)
                    val implementsStackSet = implementsStack.lastOrNull()?.toSet() ?: setOf()
                    val fromInterfaces = interfaces
                        .filter { functionName in it.methods }
                        .filter { it.name in implementsStackSet }
                    if (fromInterfaces.isNotEmpty()) {
                        it.copy(text = it.text.replace("override", "").replace("  ", " "))
                    } else it
                }
                else -> it
            }
        }.joinClean()
    }

    override fun exitSimpleUserType(ctx: KotlinParser.SimpleUserTypeContext?) {
        val typeName = text(KotlinParser.RULE_simpleIdentifier)?.trim()
        handleTypeReplacement[typeName]?.invoke(this)
    }

    var inTryEnabled = false
    var inTry = 0
    override fun enterTryExpression(ctx: KotlinParser.TryExpressionContext?) {
        inTry++
        inTryEnabled = true
    }

    override fun exitTryExpression(ctx: KotlinParser.TryExpressionContext?) {
        if (inTryEnabled) inTry--
        inTryEnabled = false
    }

    override fun enterCatchBlock(ctx: KotlinParser.CatchBlockContext?) {
        if (inTryEnabled) inTry--
        inTryEnabled = false
    }

    override fun exitStatement(ctx: KotlinParser.StatementContext?) {
        if (inTry > 0) {
            val default = default
            overridden = default.copy(
                text = default.text.let {
                    it.substringBefore(
                        "return ",
                        ""
                    ) + "try " + it.substring(it.indexOf("return ").let { if (it == -1) 0 else it })

                }
            )
        }
    }

    override fun exitCatchBlock(ctx: KotlinParser.CatchBlockContext?) {
        val startPart = Section(
            text = "catch ",
            spacingBefore = layers.last().first().spacingBefore
        )
        overridden = sequenceOf(startPart).plus(get(KotlinParser.RULE_block)!!).joinClean()
    }


    val implementsStack = ArrayList<List<String>>()
    override fun enterClassDeclaration(ctx: KotlinParser.ClassDeclarationContext) {
        implementsStack.add(ctx.delegationSpecifiers()?.delegationSpecifier()?.flatMap {
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
        } ?: listOf())
    }

    override fun exitClassDeclaration(ctx: KotlinParser.ClassDeclarationContext?) {

        val modifiers = (get(KotlinParser.RULE_modifierList) ?: Section("public ")).let {
            val text = it.text.let {
                if (it.contains("private") || it.contains("public"))
                    it
                else
                    "public " + it
            }.replace("data ", "").replace(" data", "")
            it.copy(text = text)
        }

        val primaryConstructor = get(KotlinParser.RULE_primaryConstructor)
        val header = layers.last().asSequence()
            .take(
                layers.last()
                    .indexOfFirst { it.rule == KotlinParser.RULE_classBody || it.rule == KotlinParser.RULE_enumClassBody }
                    .let { if (it == -1) layers.last().size else it }
            )
            .filter {
                when (it.rule) {
                    KotlinParser.RULE_modifierList -> false
                    KotlinParser.RULE_primaryConstructor -> false
                    else -> true
                }
            }

        val splitIndex = body.indexOfFirst {
            (it.rule == KotlinParser.RULE_classMemberDeclaration && it.text.length > 2) || it.rule == -KotlinParser.RCURL
        }
        val startMemberSpacing = body.getOrNull(splitIndex)?.let {
            if (it.rule == -KotlinParser.RCURL) it.spacingBefore + "    "
            else it.spacingBefore
        }?.let {
            if (it.contains('\n'))
                it
            else
                "\n" + it
        } ?: "\n    "
        val startMemberSpacingPlus = "$startMemberSpacing    "

        val constructorVariableDeclarations = classParameters
            .asSequence()
            .filter { it.savedAs != null }
            .map {
                Section(
                    text = it.savedAs!! + " " + it.section.text.substringBefore("=") + "\n",
                    spacingBefore = startMemberSpacing
                )
            }

        val swiftPrimaryConstructor = sequenceOf(
            Section(
                text = "init${primaryConstructor?.text ?: "()"} {\n",
                spacingBefore = startMemberSpacing
            )
        ) +
                (if (superConstructorCall == null) sequenceOf<Section>() else sequenceOf<Section>(
                    Section(
                        text = "super.init",
                        spacingBefore = startMemberSpacingPlus
                    ),
                    superConstructorCall!!
                )) +
                classParameters
                    .asSequence()
                    .filter { it.savedAs != null }
                    .map {
                        Section(
                            text = "self." + it.name + " = " + it.name,
                            spacingBefore = startMemberSpacingPlus
                        )
                    } +
                initializers.asSequence().map { it.copy(it.text.substringAfter('{').substringBeforeLast('}')) } +
                Section(
                    text = "}\n",
                    spacingBefore = startMemberSpacing
                )
        val bodyPreConstructor = if (body.isEmpty()) {
            sequenceOf(Section("{"))
        } else {
            body.asSequence().take(splitIndex)
        }
        val bodyPostConstructor = if (body.isEmpty()) {
            sequenceOf(Section("}"))
        } else {
            body.asSequence().drop(splitIndex)
        }

        overridden = (
                sequenceOf(modifiers) + header + bodyPreConstructor + constructorVariableDeclarations + swiftPrimaryConstructor + bodyPostConstructor
                ).joinClean()

        body = listOf()
        initializers.clear()
        classParameters.clear()
        implements.clear()
        superConstructorCall = null
    }

    var body: List<Section> = listOf()
    override fun exitClassBody(ctx: KotlinParser.ClassBodyContext?) {
        overridden = Section("")
        body = layers.last()
    }

    val classParameters = ArrayList<ClassParameterData>()

    data class ClassParameterData(val savedAs: String? = null, val name: String, val section: Section)

    override fun exitClassParameter(ctx: KotlinParser.ClassParameterContext?) {
        var savedAs: String? = null
        val section = layers.last().asSequence().filter {
            when {
                it.rule == -KotlinParser.VAL -> {
                    savedAs = "let"
                    false
                }
                it.rule == -KotlinParser.VAR -> {
                    savedAs = "var"
                    false
                }
                else -> true
            }
        }.joinClean()
        overridden = section

        classParameters.add(
            ClassParameterData(
                savedAs = savedAs,
                name = text(KotlinParser.RULE_simpleIdentifier) ?: "[no name found]",
                section = section
            )
        )
    }

    override fun exitEnumClassBody(ctx: KotlinParser.EnumClassBodyContext?) {
        exitClassBody(null)
    }

    var initializers = ArrayList<Section>() //blocks
    override fun exitAnonymousInitializer(ctx: KotlinParser.AnonymousInitializerContext?) {
        overridden = Section("")
        get(KotlinParser.RULE_block)?.let { initializers.add(it) }
    }

    var implements = ArrayList<String>()
    override fun exitDelegationSpecifier(ctx: KotlinParser.DelegationSpecifierContext?) {
        val sub = layers.last().first()
        if (sub.rule != KotlinParser.RULE_constructorInvocation) {
            implements.add(sub.text.substringBefore('<'))
        }
    }

    var superConstructorCall: Section? = null
    override fun exitCallSuffixLambdaless(ctx: KotlinParser.CallSuffixLambdalessContext?) {
        overridden = get(KotlinParser.RULE_typeArguments)
        superConstructorCall = get(KotlinParser.RULE_valueArguments)
    }

    //Configuration

    fun basicFunctionReplacement(name: String, swift: String) {
        handleFunctionReplacement[name] = {
            overridden = layers.last().asSequence().map {
                when (it.rule) {
                    KotlinParser.RULE_assignableExpression -> it.copy(text = swift)
                    else -> it
                }
            }.joinClean()
        }
    }

    fun basicTypeReplacement(name: String, swift: String) {
        handleTypeReplacement[name] = {
            overridden = layers.last().asSequence().map {
                when (it.rule) {
                    KotlinParser.RULE_simpleIdentifier -> it.copy(text = swift)
                    else -> it
                }
            }.joinClean()
        }
    }

    init {
        basicFunctionReplacement("println", "print")
        basicTypeReplacement("Boolean", "Bool")
        basicTypeReplacement("Byte", "Int8")
        basicTypeReplacement("Short", "Int16")
        basicTypeReplacement("Long", "Int64")
    }

    override fun enterEveryRule(ctx: ParserRuleContext) {
//        println("Entering ${ctx.ruleIndex}, text: (${ctx.text})")
        super.enterEveryRule(ctx)
    }

    override fun visitTerminal(node: TerminalNode) {
        super.visitTerminal(node)
//        println("Visiting ${node.symbol.type}, text: (${node.text})")
    }

    override fun exitEveryRule(ctx: ParserRuleContext) {
        super.exitEveryRule(ctx)
//        println("Exiting ${ctx.ruleIndex}, text: (${ctx.text})")
    }
}
