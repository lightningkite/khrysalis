package com.lightningkite.kwift.swift

import com.lightningkite.kwift.interfaces.InterfaceListener
import com.lightningkite.kwift.utils.RewriteListener
import com.lightningkite.kwift.utils.getMany
import com.lightningkite.kwift.utils.joinClean
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.TerminalNode
import org.jetbrains.kotlin.KotlinParser

class SwiftListener(
    tokenStream: CommonTokenStream,
    parser: KotlinParser,
    val interfaces: Map<String, InterfaceListener.InterfaceData>
) : RewriteListener(tokenStream, parser) {

    val handleFunctionReplacement = HashMap<String, SwiftListener.() -> Unit>()

    val swiftStart = Regex("/\\* *Swift *Only")
    val swiftEnd = Regex("End *Swift *Only *\\*/")

    override fun String.whitespaceReplacements(): String = this
        .replace(swiftEnd, "/* End Swift Only */")
        .replace(swiftStart, "/* Swift Only */")

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
        handleFunctionReplacement["isEmpty"] = {
            overridden = default.copy(
                text = text(KotlinParser.RULE_assignableExpression)!!
            )
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
                        ?.replace(",", ":")
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
                        ?.replace(",", ":")
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
        handleFunctionReplacement["weakLambda"] = {
            overridden = get(KotlinParser.RULE_annotatedLambda)
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
                default.copy(text = "[" + text(KotlinParser.RULE_typeArguments)?.removePrefix("<")?.removeSuffix(">") + "]")
        }
        handleTypeReplacement["ArrayList"] = {
            overridden =
                default.copy(text = "[" + text(KotlinParser.RULE_typeArguments)?.removePrefix("<")?.removeSuffix(">") + "]")
        }
        handleTypeReplacement["Map"] = {
            overridden = default.copy(
                text = "[" + text(KotlinParser.RULE_typeArguments)?.removePrefix("<")?.removeSuffix(">")?.replace(
                    ",",
                    ":"
                ) + "]"
            )
        }
        handleTypeReplacement["HashMap"] = {
            overridden = default.copy(
                text = "[" + text(KotlinParser.RULE_typeArguments)?.removePrefix("<")?.removeSuffix(">")?.replace(
                    ",",
                    ":"
                ) + "]"
            )
        }
        handleTypeReplacement["LinkedHashMap"] = {
            overridden = default.copy(
                text = "[" + text(KotlinParser.RULE_typeArguments)?.removePrefix("<")?.removeSuffix(">")?.replace(
                    ",",
                    ":"
                ) + "]"
            )
        }
        handleTypeReplacement["LinkedHashMap"] = {
            overridden = default.copy(
                text = "[" + text(KotlinParser.RULE_typeArguments)?.removePrefix("<")?.removeSuffix(">")?.replace(
                    ",",
                    ":"
                ) + "]"
            )
        }
    }

    init {
        terminalRewrites[KotlinParser.EOF] = { "" }
        terminalRewrites[KotlinParser.LineStrRef] = { "\\(" + it.removePrefix("$") + ")" }
        terminalRewrites[KotlinParser.MultiLineStrRef] = { "\\(" + it.removePrefix("$") + ")" }
        terminalRewrites[KotlinParser.NullLiteral] = { "nil" }
        terminalRewrites[KotlinParser.TRY] = { "do" }
        terminalRewrites[KotlinParser.VAL] = { "var" }
        terminalRewrites[KotlinParser.THIS] = { "self" }
        terminalRewrites[KotlinParser.INTERFACE] = { "protocol" }
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
        currentPackage = ctx.text.removePrefix("package").removeSuffix(";").trim()
    }

    override fun exitImportHeader(ctx: KotlinParser.ImportHeaderContext?) {
        val default = default
        overridden = default.copy(text = "//${default.text}")
    }

    override fun exitPackageHeader(ctx: KotlinParser.PackageHeaderContext?) {
        val default = default
        overridden = default.copy(text = "//${default.text}")
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
            overridden = default.copy(text = "default: ${body.toOutputString()}")
        } else {
            overridden = default.copy(text = "case ${condition.toOutputString()}: ${body.toOutputString()}")
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

    val callIsWeakLambdaStack = BooleanArray(256) { false }
    var callIsWeakLambdaStackIndex = -1
    var callIsWeakCurrent: Boolean
        get() = callIsWeakLambdaStack[callIsWeakLambdaStackIndex]
        set(value) {
            callIsWeakLambdaStack[callIsWeakLambdaStackIndex] = value
        }

    override fun enterCallExpression(ctx: KotlinParser.CallExpressionContext) {
        callIsWeakLambdaStackIndex++
        callIsWeakCurrent = ctx.assignableExpression().text.trim() == "weakLambda"
    }

    override fun exitCallExpression(ctx: KotlinParser.CallExpressionContext?) {
        val functionCallName = text(KotlinParser.RULE_assignableExpression)?.trim()?.substringAfterLast('.')
        val alternateCallName = text(KotlinParser.RULE_assignableExpression)?.trim()
        handleFunctionReplacement[functionCallName]?.invoke(this)
            ?: handleFunctionReplacement[alternateCallName]?.invoke(this)
        callIsWeakLambdaStackIndex--
    }

    override fun exitFunctionDeclaration(ctx: KotlinParser.FunctionDeclarationContext) {
        var defaultOrder = -1f
        val mainSeq = layers.last().asSequence().map {
            when (it.rule) {
                -KotlinParser.FUN -> it.copy(text = "func")
                -KotlinParser.COLON -> it.copy(text = "->", spacingBefore = " ")
                KotlinParser.RULE_modifierList -> {
                    val functionName = text(KotlinParser.RULE_identifier)
                    val implementsStackSet = currentClass?.implements?.toSet() ?: setOf()
                    val fromInterfaces = interfaces
                        .getMany(implementsStackSet)
                        .filter { functionName in it.methods }
                        .toList()
                    if (fromInterfaces.isNotEmpty()) {
                        it.copy(text = it.text.replace("override", "public"))
                    } else it
                }
                else -> it
            }
        }.toMutableList()

        //Reorder generics
        mainSeq
            .indexOfFirst { it.rule == KotlinParser.RULE_typeParameters }
            .let {
                if (it == -1) return@let
                val typeParams = mainSeq.removeAt(it).copy(spacingBefore = "")
                val identifierPos = mainSeq.indexOfFirst { it.rule == KotlinParser.RULE_identifier }
                mainSeq.add(identifierPos + 1, typeParams)
            }

        val fvps = ctx.functionValueParameters().functionValueParameter()
        val thereIsVarargParam = fvps.any { it.modifierList()?.modifier()?.any { it.parameterModifier()?.VARARG() != null } ?: false }
        if (fvps.isEmpty() || (fvps.size == 1 && fvps.first().parameter().type().functionType() != null) || thereIsVarargParam) {
            overridden = mainSeq.joinClean()
        } else {
            val secondarySeq = mainSeq.map {
                when (it.rule) {
                    KotlinParser.RULE_functionValueParameters -> {
                        lastFunctionValueParameters!!.map {
                            if (it.rule == KotlinParser.RULE_functionValueParameter) {
                                it.copy(text = "_ " + it.text)
                            } else {
                                it
                            }
                        }.joinClean()
                    }
                    KotlinParser.RULE_functionBody -> {
                        val returnTypeIsUnit = when (ctx.type().firstOrNull()?.text) {
                            null -> true
                            "Unit" -> true
                            "Empty" -> true
                            "Nothing" -> true
                            else -> false
                        }
                        it.copy(text = "{ ${if (returnTypeIsUnit) "" else "return "}${ctx.identifier().text}(${ctx.functionValueParameters().functionValueParameter().joinToString {
                            "${it.parameter().simpleIdentifier().text}: ${it.parameter().simpleIdentifier().text}"
                        }}) }")
                    }
                    else -> it
                }
            }
            overridden = (mainSeq + Section("", spacingBefore = "\n") + secondarySeq).joinClean()
        }
    }

    var lastFunctionValueParameters: List<Section>? = null
    override fun exitFunctionValueParameters(ctx: KotlinParser.FunctionValueParametersContext?) {
        lastFunctionValueParameters = this.layers.last()
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


    class ClassInformation(var name: String = "") {
        var implements: List<String> = listOf()
        val initializers = ArrayList<Section>()
        val classParameters = ArrayList<ClassParameterData>()
        var superConstructorCall: Section? = null
        var body: List<Section> = listOf()
        var isInterface: Boolean = false
    }

    val classStack = ArrayList<ClassInformation>()
    val currentClass get() = classStack.lastOrNull()


    override fun enterClassDeclaration(ctx: KotlinParser.ClassDeclarationContext) {
        classStack.add(ClassInformation(ctx.simpleIdentifier().text))
        if (ctx.INTERFACE() != null) {
            currentClass?.isInterface = true
        }
        currentClass?.implements = (ctx.delegationSpecifiers()?.delegationSpecifier()?.flatMap {
            val userType = it.userType() ?: it.constructorInvocation()?.userType() ?: return@flatMap listOf<String>()
            val id = userType.text.substringBefore('<')
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
                }.plus(currentPackage + "." + id)
            }
        } ?: listOf())
    }

    override fun exitClassDeclaration(ctx: KotlinParser.ClassDeclarationContext) {
        val isSerializable = text(KotlinParser.RULE_delegationSpecifiers)?.contains("Serializable") ?: false

        val modifiers = (get(KotlinParser.RULE_modifierList) ?: Section("public ")).let {
            val text = it.text.let {
                if (it.contains("open") || it.contains("enum") || it.contains("abstract") || (currentClass?.isInterface != false))
                    it
                else
                    "final " + it
            }.let {
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
            .mapNotNull {
                when (it.rule) {
                    KotlinParser.RULE_modifierList -> null
                    KotlinParser.RULE_primaryConstructor -> null
                    -KotlinParser.CLASS -> if (ctx.enumClassBody() != null) null else it
                    else -> it
                }
            }

        val splitIndex = currentClass!!.body.indexOfFirst {
            (it.rule == KotlinParser.RULE_classMemberDeclaration && it.text.length > 2) || it.rule == -KotlinParser.RCURL
        }

        val fields = currentClass!!.classParameters
            .asSequence()
            .filter { it.savedAs != null }
            .toList()

        val constructorVariableDeclarations = fields
            .asSequence()
            .map {
                Section(
                    text = it.savedAs!! + " " + it.section.text.replace("@escaping", "").trim().substringBefore("="),
                    spacingBefore = "\n"
                )
            }

        val swiftPrimaryConstructor =
            if (get(-KotlinParser.INTERFACE) != null || text(KotlinParser.RULE_modifierList)?.contains("enum") == true) sequenceOf() else ArrayList<Section>().apply {
                add(
                    Section(
                        text = "init${primaryConstructor?.text?.replace("\n", " ") ?: "()"} {",
                        spacingBefore = "\n\n"
                    )
                )
                if (currentClass!!.superConstructorCall != null) addAll(
                    sequenceOf<Section>(
                        Section(
                            text = "super.init",
                            spacingBefore = "\n"
                        ),
                        currentClass!!.superConstructorCall!!
                    )
                )
                addAll(currentClass!!.classParameters
                    .asSequence()
                    .filter { it.savedAs != null }
                    .map {
                        Section(
                            text = "self." + it.name + " = " + it.name,
                            spacingBefore = "\n"
                        )
                    })
                addAll(currentClass!!.initializers.asSequence().map {
                    it.copy(
                        it.text.substringAfter('{').substringBeforeLast(
                            '}'
                        )
                    )
                })
                add(
                    Section(
                        text = "}",
                        spacingBefore = "\n"
                    )
                )
                if(currentClass!!.classParameters.isNotEmpty()) {
                    add(
                        Section(
                            text = run {
                                val parametersWithUnderscores =
                                    currentClass!!.classParameters.joinToString(",\n", "\n", "\n") { "_ ${it.section.text}" }
                                val parametersWithNames =
                                    currentClass!!.classParameters.joinToString(",\n", "\n", "\n") { "${it.name}: ${it.name}" }
                                "convenience init($parametersWithUnderscores){ \nself.init($parametersWithNames) \n}"
                            },
                            spacingBefore = "\n"
                        )
                    )
                }
            }.asSequence()


        val additionalThings = ArrayList<Section>()

        if (isSerializable) {
            val name = text(KotlinParser.RULE_simpleIdentifier)
            val mapConstructor = ArrayList<Section>()
            mapConstructor.add(
                Section(
                    text = "public static func fromData(data: Any?) -> $name {",
                    spacingBefore = "\n\n"
                )
            )
            mapConstructor.add(
                Section(
                    text = "let map = data as! [String: Any?]",
                    spacingBefore = "\n"
                )
            )
            mapConstructor.add(
                Section(
                    text = "return $name(",
                    spacingBefore = "\n"
                )
            )
            fields.forEachIndexed { index, field ->
                val comma = if (index == fields.lastIndex) "" else ","
                mapConstructor.add(
                    Section(
                        text = "${field.name}: ${field.type}.fromData(data: map[\"${field.name.snakeCase()}\"] as Any?)$comma",
                        spacingBefore = "\n"
                    )
                )
            }
            mapConstructor.add(
                Section(
                    text = ")",
                    spacingBefore = "\n"
                )
            )
            mapConstructor.add(
                Section(
                    text = "}",
                    spacingBefore = "\n"
                )
            )
            additionalThings.add(mapConstructor.joinClean())

            val mapWriter = ArrayList<Section>()
            mapWriter.add(
                Section(
                    text = "public func toData() -> Any? {",
                    spacingBefore = "\n\n"
                )
            )
            mapWriter.add(
                Section(
                    text = "return [",
                    spacingBefore = "\n"
                )
            )
            fields.forEachIndexed { index, field ->
                val comma = if (index == fields.lastIndex) "" else ","
                mapWriter.add(
                    Section(
                        text = "\"${field.name.snakeCase()}\": ${field.name}.toData()$comma",
                        spacingBefore = "\n"
                    )
                )
            }
            mapWriter.add(
                Section(
                    text = "]",
                    spacingBefore = "\n"
                )
            )
            mapWriter.add(
                Section(
                    text = "}\n",
                    spacingBefore = "\n"
                )
            )
            additionalThings.add(mapWriter.joinClean())
        }


        val bodyPreConstructor = if (currentClass!!.body.isEmpty()) {
            sequenceOf(Section("{"))
        } else {
            currentClass!!.body.asSequence().take(splitIndex)
        }
        val bodyPostConstructor = if (currentClass!!.body.isEmpty()) {
            sequenceOf(Section("}", spacingBefore = "\n"))
        } else {
            currentClass!!.body.asSequence().drop(splitIndex).also { it.firstOrNull()?.spacingBefore = "\n\n" }
        }

        overridden = (
                sequenceOf(modifiers) + header + bodyPreConstructor + constructorVariableDeclarations + swiftPrimaryConstructor + additionalThings + bodyPostConstructor
                ).joinClean()

        classStack.removeAt(classStack.lastIndex)
    }

    override fun enterCompanionObject(ctx: KotlinParser.CompanionObjectContext?) {
        classStack.add(ClassInformation(currentClass!!.name))
    }

    override fun exitCompanionObject(ctx: KotlinParser.CompanionObjectContext?) {
        overridden = (
                currentClass!!.body.mapNotNull {
                    when {
                        it.text.startsWith("func") -> it.copy(text = "static " + it.text)
                        it.text.startsWith("var") -> it.copy(text = "static " + it.text)
                        it.text.startsWith("let") -> it.copy(text = "static " + it.text)
                        it.rule == -KotlinParser.LCURL -> null
                        it.rule == -KotlinParser.RCURL -> null
                        else -> it
                    }
                }
                ).joinClean()
        classStack.removeAt(classStack.lastIndex)
    }

    override fun enterObjectDeclaration(ctx: KotlinParser.ObjectDeclarationContext) {
        classStack.add(ClassInformation(ctx.simpleIdentifier().text))
    }

    override fun exitObjectDeclaration(ctx: KotlinParser.ObjectDeclarationContext) {

        val modifiers = (get(KotlinParser.RULE_modifierList) ?: Section("public ")).let {
            val text = it.text.let {
                if (it.contains("private") || it.contains("public"))
                    it
                else
                    "public " + it
            }.replace("data ", "").replace(" data", "")
            it.copy(text = text)
        }

        val header = layers.last().asSequence()
            .filter {
                when (it.rule) {
                    KotlinParser.RULE_modifierList -> false
                    KotlinParser.RULE_primaryConstructor -> false
                    else -> true
                }
            }.map {
                when (it.rule) {
                    -KotlinParser.OBJECT -> it.copy(text = "enum")
                    else -> it
                }
            }

        overridden = (
                sequenceOf(modifiers) + header + currentClass!!.body.map {
                    when {
                        it.text.startsWith("func") -> it.copy(text = "static " + it.text)
                        it.text.startsWith("var") -> it.copy(text = "static " + it.text)
                        it.text.startsWith("let") -> it.copy(text = "static " + it.text)
                        else -> it
                    }
                }
                ).joinClean()

        classStack.removeAt(classStack.lastIndex)
    }


    override fun exitClassBody(ctx: KotlinParser.ClassBodyContext?) {
        overridden = Section("")
        currentClass!!.body = layers.last()
    }

    data class ClassParameterData(val savedAs: String? = null, val name: String, val section: Section, val type: String)

    override fun exitClassParameter(ctx: KotlinParser.ClassParameterContext?) {
        var escaping = false
        var savedAs: String? = null
        var typeText = "MISSED_TYPE"
        val section = layers.last().asSequence().mapNotNull {
            when (it.rule) {
                -KotlinParser.VAL, -KotlinParser.VAR -> {
                    savedAs = "var"
                    null
                }
                KotlinParser.RULE_modifierList -> {
                    escaping = it.text.contains("@escaping")
                    it.copy(text = it.text.replace("@escaping", "").trim())
                }
                KotlinParser.RULE_type -> {
                    typeText = it.text
                    if (escaping) {
                        it.copy(text = "@escaping " + it.text)
                    } else {
                        it
                    }
                }
                else -> it
            }
        }.joinClean()
        overridden = section

        currentClass!!.classParameters.add(
            ClassParameterData(
                savedAs = savedAs,
                name = text(KotlinParser.RULE_simpleIdentifier) ?: "[no path found]",
                section = section,
                type = typeText
            )
        )
    }

    override fun exitEnumClassBody(ctx: KotlinParser.EnumClassBodyContext?) {
        exitClassBody(null)
    }

    override fun exitAnonymousInitializer(ctx: KotlinParser.AnonymousInitializerContext?) {
        overridden = Section("")
        get(KotlinParser.RULE_block)?.let { currentClass!!.initializers.add(it) }
    }

    override fun exitCallSuffixLambdaless(ctx: KotlinParser.CallSuffixLambdalessContext?) {
        overridden = get(KotlinParser.RULE_typeArguments)
        currentClass!!.superConstructorCall = get(KotlinParser.RULE_valueArguments)
    }

    override fun exitLambdaParameters(ctx: KotlinParser.LambdaParametersContext?) {
        overridden = default.copy(text = "(" + default.text + ")")
    }

    override fun exitLambdaParameter(ctx: KotlinParser.LambdaParameterContext) {
        overridden = default.copy(text = ctx.variableDeclaration()?.simpleIdentifier()?.text ?: "BROKEN_PARAM")
    }

    override fun exitFunctionLiteral(ctx: KotlinParser.FunctionLiteralContext?) {
        overridden = layers.last().map {
            when (it.rule) {
                -KotlinParser.LCURL -> {
                    if (callIsWeakCurrent) {
                        it.copy(text = "{ [weak self]")
                    } else it
                }
                -KotlinParser.ARROW -> it.copy(text = "in")
                KotlinParser.RULE_statements -> {
                    if (callIsWeakCurrent) {
                        it.copy(text = "if let self = self {\n${it.text}\n}\n")
                    } else it
                }
                else -> it
            }
        }.joinClean()
    }

    var propertyIsBacked = false
    override fun enterPropertyDeclaration(ctx: KotlinParser.PropertyDeclarationContext) {
        propertyIsBacked = ctx.ASSIGNMENT() != null
    }

    override fun exitPropertyDeclaration(ctx: KotlinParser.PropertyDeclarationContext) {
        if(ctx.BY() != null && ctx.expression()?.text?.startsWith("weak") == true) {
            val default = default
            overridden = default.copy(text = "weak var ${text(KotlinParser.RULE_variableDeclaration)} = ${text(KotlinParser.RULE_expression)!!.removePrefix("weak(").removeSuffix(")")}")
            return
        }

        val additionalGetSetNeeded = ctx.parent is KotlinParser.ClassMemberDeclarationContext &&
                currentClass?.isInterface == true
                && ctx.getter() == null
                && ctx.setter() == null

        val firstCalculationIndex =
            layers.last().indexOfFirst { it.rule == KotlinParser.RULE_getter || it.rule == KotlinParser.RULE_setter }
        val preCalculationTextIndex = if (firstCalculationIndex == -1) -1 else
            layers.last().subList(0, firstCalculationIndex).indexOfLast { !it.text.isBlank() } + 1

        val preJoin: Sequence<Section> = if (firstCalculationIndex == -1) {
            if (additionalGetSetNeeded) {
                layers.last().asSequence()
                    .filter { it.rule != KotlinParser.RULE_semi }
                    .plus(
                        Section(
                            text = if (ctx.VAL() != null) " { get }\n" else " { get set }\n",
                            spacingBefore = " "
                        )
                    )
            } else {
                layers.last().asSequence()
            }
        } else {
            layers.last()
                .asSequence()
                .take(preCalculationTextIndex)
                .plus(Section("{\n", spacingBefore = " "))
                .plus(layers.last().drop(preCalculationTextIndex))
                .plus(Section("}\n", spacingBefore = "\n"))
        }

        overridden = preJoin.map {
            when (it.rule) {
                KotlinParser.RULE_modifierList -> {
                    val propertyName = text(KotlinParser.RULE_variableDeclaration)?.substringBefore(':')?.trim()
                    val implementsStackSet = currentClass?.implements?.toSet() ?: setOf()
                    val fromInterfaces = interfaces
                        .getMany(implementsStackSet)
                        .filter { propertyName in it.properties }
                        .toList()
                    if (fromInterfaces.isNotEmpty()) {
                        it.copy(text = it.text.replace("override", "public"))
                    } else it
                }
                else -> it
            }
        }.joinClean()
    }

    override fun exitGetter(ctx: KotlinParser.GetterContext) {
        overridden = layers.last()
            .mapNotNull {
                when (it.rule) {
                    -KotlinParser.GETTER -> if (propertyIsBacked) it.copy(text = "didGet") else it
                    -KotlinParser.LPAREN -> null
                    -KotlinParser.RPAREN -> null
                    -KotlinParser.ASSIGNMENT -> null
                    KotlinParser.RULE_expression -> it.copy(text = "{ return ${it.text} }")
                    else -> it
                }
            }
            .joinClean()
    }

    override fun exitSetter(ctx: KotlinParser.SetterContext) {
        val propName = ctx.simpleIdentifier()?.text ?: ctx.parameter()?.simpleIdentifier()?.text
        overridden = layers.last()
            .map {
                when (it.rule) {
                    -KotlinParser.SETTER -> if (propertyIsBacked) it.copy(text = "didSet") else it
                    KotlinParser.RULE_functionBody -> it.copy(
                        text = it.text.replace(Regex("field += +$propName"), "")
                    )
                    else -> it
                }
            }
            .joinClean()
    }

    override fun exitFunctionBody(ctx: KotlinParser.FunctionBodyContext) {
        if (ctx.ASSIGNMENT() != null) {
            val start = Section("{ return ", " ")
            val exp = this.get(KotlinParser.RULE_expression)!!
            val end = Section("}", " ")
            overridden = listOf(start, exp, end).joinClean()
        }
    }

    override fun exitFunctionTypeParameters(ctx: KotlinParser.FunctionTypeParametersContext?) {
        overridden = layers.last().map {
            when (it.rule) {
                KotlinParser.RULE_parameter -> it.copy(text = it.text.substringAfter(":").trim())
                else -> it
            }
        }.joinClean()
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

    override fun exitCallableReference(ctx: KotlinParser.CallableReferenceContext) {
        overridden = Section(text = "type(of: " + ctx.userType().text + ".self)", spacingBefore = default.spacingBefore)
    }

    override fun exitFunctionValueParameter(ctx: KotlinParser.FunctionValueParameterContext) {
        var escaping = false
        var variadic = false
        overridden = layers.last().map {
            when (it.rule) {
                KotlinParser.RULE_modifierList -> {
                    var current = it

                    variadic = current.text.contains("vararg")
                    current = current.copy(text = it.text.replace("vararg", "").trim())

                    escaping = current.text.contains("@escaping")
                    current = current.copy(text = current.text.replace("@escaping", "").trim())

                    current
                }
                KotlinParser.RULE_parameter -> {
                    (if (escaping) {
                        it.copy(text = it.text.substringBefore(':') + ": @escaping" + it.text.substringAfter(':'))
                    } else {
                        it
                    }).let {
                        if (variadic) {
                            it.copy(text = "_ " + it.text + "...")
                        } else {
                            it
                        }
                    }
                }
                else -> it
            }
        }.joinClean()
    }

    override fun exitEnumEntries(ctx: KotlinParser.EnumEntriesContext?) {
        val default = default
        overridden = default.copy(text = "case " + default.text)
    }

    override fun exitDotQualifiedExpression(ctx: KotlinParser.DotQualifiedExpressionContext) {
        val default = default
        val text = default.text.trim()
        if (text.startsWith("R.string.")) {
            overridden = Section(
                text = "\"" + text.removePrefix("R.string.") + "\"",
                spacingBefore = default.spacingBefore
            )
        }
    }

    override fun exitSimpleIdentifier(ctx: KotlinParser.SimpleIdentifierContext?) {
        val default = default
        overridden = if (default.text == "this") default.copy(text = "self") else default
    }


    init {
        basicFunctionReplacement("println", "print")
        basicTypeReplacement("Boolean", "Bool")
        basicTypeReplacement("Byte", "Int8")
        basicTypeReplacement("Short", "Int16")
        basicTypeReplacement("Long", "Int64")
        basicTypeReplacement("Unit", "Void")
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

    private fun String.snakeCase(): String = this.replace(Regex("[A-Z]+")) { "_" + it.value.toLowerCase() }.trim('_')
}
