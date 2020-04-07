package com.lightningkite.khrysalis.ios.swift

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.lightningkite.khrysalis.preparse.FileCache
import com.lightningkite.khrysalis.preparse.InterfaceData
import com.lightningkite.khrysalis.preparse.merged
import com.lightningkite.khrysalis.preparse.mergedInterfaces
import com.lightningkite.khrysalis.utils.Versioned
import com.lightningkite.khrysalis.utils.forEachBetween
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNode
import org.jetbrains.kotlin.KotlinParser
import java.io.File
import java.lang.IllegalStateException

class SwiftAltListener {
    val interfaces: HashMap<String, InterfaceData> = hashMapOf()
    var currentFile: KotlinParser.KotlinFileContext? = null
    var filterEscapingAnnotation: Boolean = false
    var imports = listOf<String>("RxSwift", "RxRelay")

    fun loadInterfaces(file: File) {
        interfaces += jacksonObjectMapper().readValue<Versioned<Map<String, FileCache>>>(file).value.values.asSequence().map { it.data }.mergedInterfaces()
    }

    fun KotlinParser.ClassDeclarationContext.implements(): Sequence<InterfaceData> {
        val currentFile = currentFile ?: return sequenceOf()
        return this.delegationSpecifiers()?.annotatedDelegationSpecifier()
            ?.asSequence()
            ?.map { it.delegationSpecifier() }
            ?.mapNotNull { it.userType()?.text }
            ?.flatMap { name ->
                if (name.firstOrNull()?.isUpperCase() == true) {
                    currentFile.importList().importHeader().asSequence()
                        .mapNotNull { it.identifier()?.text }
                        .find { it.endsWith(name) }
                        ?.let { sequenceOf(it) }
                        ?: currentFile.importList().importHeader().asSequence()
                            .filter { it.MULT() != null }
                            .map { import ->
                                import.identifier().text + "." + name
                            }
                            .plus(currentFile.packageHeader().identifier().text + "." + name)
                } else {
                    sequenceOf(name)
                }
            }
            ?.mapNotNull { interfaces[it] }
            ?: sequenceOf()
    }

    val options = HashMap<Class<*>, TabWriter.(ParserRuleContext) -> Unit>()
    val tokenOptions = HashMap<Int, TabWriter.(TerminalNode) -> Unit>()
    val functionReplacements = HashMap<String, TabWriter.(KotlinParser.PostfixUnaryExpressionContext) -> Unit>()
    fun simpleFunctionReplacement(kotlinFunctionName: String, swiftFunctionName: String) {
        functionReplacements[kotlinFunctionName] = {
            direct.append(swiftFunctionName)
            it.postfixUnarySuffix().forEach { write(it) }
        }
    }

    val typeReplacements = HashMap<String, String>()

    inline fun <reified T : ParserRuleContext> handle(noinline action: TabWriter.(T) -> Unit) {
        options[T::class.java] = { action(it as T) }
    }

    fun handle(rule: Int, action: TabWriter.(TerminalNode) -> Unit) {
        tokenOptions[rule] = action
    }

    init {
        registerClass()
        registerFile()
        registerFunction()
        registerVariable()
        registerExpression()
        registerLiterals()
        registerLambda()
        registerControl()
        registerType()
        registerStatement()

        tokenOptions[KotlinParser.EOF] = { direct.append("") }
        tokenOptions[KotlinParser.LineStrRef] = { direct.append("\\(" + it.text.removePrefix("$") + ")") }
        tokenOptions[KotlinParser.MultiLineStrRef] = { direct.append("\\(" + it.text.removePrefix("$") + ")") }
        tokenOptions[KotlinParser.NullLiteral] = { direct.append("nil") }
        tokenOptions[KotlinParser.TRY] = { direct.append("do") }
        tokenOptions[KotlinParser.VAL] = { direct.append("var") }
        tokenOptions[KotlinParser.THIS] = { direct.append("self") }
        tokenOptions[KotlinParser.INTERFACE] = { direct.append("protocol") }
        tokenOptions[KotlinParser.AS] = { direct.append("as!") }
        tokenOptions[KotlinParser.AS_SAFE] = { direct.append("as?") }
        tokenOptions[KotlinParser.RETURN_AT] = { direct.append("return") }
        tokenOptions[KotlinParser.RANGE] = { direct.append("...") }

        typeReplacements["Map"] = "Dictionary"
        typeReplacements["List"] = "Array"
        typeReplacements["MutableMap"] = "Dictionary"
        typeReplacements["MutableList"] = "Array"
        typeReplacements["MutableSet"] = "Set"
        typeReplacements["HashMap"] = "Dictionary"
        typeReplacements["HashSet"] = "Set"
        typeReplacements["ArrayList"] = "Array"
        typeReplacements["Boolean"] = "Bool"
        typeReplacements["Unit"] = "Void"
        typeReplacements["Char"] = "Character"
        typeReplacements["Byte"] = "Int8"
        typeReplacements["Short"] = "Int16"
        typeReplacements["Int"] = "Int32"
        typeReplacements["Long"] = "Int64"
        typeReplacements["Exception"] = "Swift.Error"
        typeReplacements["KClass"] = "Any.Type*"
        typeReplacements["Class"] = "Any.Type*"

        simpleFunctionReplacement("println", "print")
        simpleFunctionReplacement("ArrayList", "Array")
        simpleFunctionReplacement("HashMap", "Dictionary")
        simpleFunctionReplacement("HashSet", "Set")
        functionReplacements["run"] = {
            if (it.usedAsStatement())
                direct.append("let _ = ")
            direct.append("{ () in ")
            it.postfixUnarySuffix()[0]!!.callSuffix()!!.annotatedLambda()!!.lambdaLiteral()!!.statements()!!.statement()
                .forEach {
                    startLine()
                    write(it)
                }
            startLine()
            direct.append("}()")
        }

        fun KotlinParser.PostfixUnaryExpressionContext.typeArgsAndParams(): Pair<List<KotlinParser.TypeContext>?, KotlinParser.CallSuffixContext> {
            return postfixUnarySuffix(0)?.callSuffix()?.let {
                it.typeArguments()?.typeProjection()?.map { it.type()!! } to it
            } ?: throw IllegalStateException("No necessary information found for translating collection")
        }

        functionReplacements["nullOf"] = {
            val (typeArgs, params) = it.typeArgsAndParams()
            direct.append("Optional<")
            write(typeArgs!![0])
            direct.append(">.none")
        }

        functionReplacements["Pair"] = {
            val (typeArgs, params) = it.typeArgsAndParams()
            val valArgs = params.valueArguments().valueArgument()
            direct.append('(')
            write(valArgs[0])
            direct.append(", ")
            write(valArgs[1])
            direct.append(')')
        }

        functionReplacements["listOf"] = {
            val (typeArgs, params) = it.typeArgsAndParams()
            params.valueArguments()?.valueArgument()?.takeUnless { it.isEmpty() }?.let { valueArgs ->
                direct.append("[")
                valueArgs.forEachBetween(
                    forItem = { write(it) },
                    between = { direct.append(", ") }
                )
                direct.append("]")
            } ?: typeArgs?.let {
                direct.append("Array<")
                write(it[0])
                direct.append(">")
            } ?: run {
                direct.append("[]")
            }
            it.postfixUnarySuffix().drop(1).forEach {
                write(it)
            }
        }
        functionReplacements["arrayListOf"] = functionReplacements["listOf"]!!
        functionReplacements["mutableListOf"] = functionReplacements["listOf"]!!
        functionReplacements["arrayOf"] = functionReplacements["listOf"]!!
        functionReplacements["byteArrayOf"] = functionReplacements["listOf"]!!
        functionReplacements["shortArrayOf"] = functionReplacements["listOf"]!!
        functionReplacements["intArrayOf"] = functionReplacements["listOf"]!!
        functionReplacements["longArrayOf"] = functionReplacements["listOf"]!!
        functionReplacements["doubleArrayOf"] = functionReplacements["listOf"]!!
        functionReplacements["floatArrayOf"] = functionReplacements["listOf"]!!
        functionReplacements["booleanArrayOf"] = functionReplacements["listOf"]!!

        functionReplacements["setOf"] = {
            val (typeArgs, params) = it.typeArgsAndParams()
            params.valueArguments()?.valueArgument()?.takeUnless { it.isEmpty() }?.let { valueArgs ->
                direct.append("[")
                valueArgs.forEachBetween(
                    forItem = { write(it) },
                    between = { direct.append(", ") }
                )
                direct.append("]")
            } ?: typeArgs?.let {
                direct.append("Set<")
                write(it[0])
                direct.append(">")
            } ?: run {
                direct.append("[]")
            }
            it.postfixUnarySuffix().drop(1).forEach {
                write(it)
            }
        }
        functionReplacements["hashSetOf"] = functionReplacements["setOf"]!!
        functionReplacements["mutableSetOf"] = functionReplacements["setOf"]!!

        functionReplacements["mapOf"] = {
            val (typeArgs, params) = it.typeArgsAndParams()
            params.valueArguments()?.valueArgument()?.takeUnless { it.isEmpty() }?.let { valueArgs ->
                direct.append("[")
                valueArgs.forEachBetween(
                    forItem = {
                        //it is a 'x to y' expression
                        val toCall = it.expression()
                            .disjunction()!!
                            .conjunction(0)!!
                            .equality(0)!!
                            .comparison(0)!!
                            .infixOperation(0)!!
                            .elvisExpression(0)!!
                            .infixFunctionCall(0)!!
                        assert(toCall.simpleIdentifier(0)!!.text == "to")
                        write(toCall.rangeExpression(0)!!)
                        direct.append(": ")
                        write(toCall.rangeExpression(1)!!)
                    },
                    between = { direct.append(", ") }
                )
                direct.append("]")
            } ?: typeArgs?.let {
                direct.append("Dictionary<")
                write(it[0])
                direct.append(", ")
                write(it[1])
                direct.append(">")
            } ?: run {
                direct.append("[:]")
            }
            it.postfixUnarySuffix().drop(1).forEach {
                write(it)
            }
        }
        functionReplacements["hashMapOf"] = functionReplacements["mapOf"]!!
        functionReplacements["mutableMapOf"] = functionReplacements["mapOf"]!!
    }

    fun TabWriter.write(node: TerminalNode) {
//        println("Appending node '${node.text}' of type ${KotlinParser.VOCABULARY.getDisplayName(node.symbol.type)}")
        tokenOptions[node.symbol.type]?.invoke(this, node) ?: this.direct.append(node.text)
    }

    fun TabWriter.write(item: ParserRuleContext) {
        options[item::class.java]?.invoke(this, item) ?: defaultWrite(item)
    }

    fun TabWriter.defaultWrite(item: ParserRuleContext, between: String = " ") {
        item.children?.forEachBetween(
            forItem = { child ->
                when (child) {
                    is ParserRuleContext -> write(child)
                    is TerminalNode -> write(child)
                }
            },
            between = {
                direct.append(between)
            }
        )
    }

    fun TabWriter.defaultWrite(item: ParserRuleContext, between: String = " ", filter: (ParseTree) -> Boolean) {
        item.children?.filter(filter)?.forEachBetween(
            forItem = { child ->
                when (child) {
                    is ParserRuleContext -> write(child)
                    is TerminalNode -> write(child)
                }
            },
            between = {
                direct.append(between)
            }
        )
    }
//    fun KotlinParser.TypeContext.toSwift(): String {
//        val modifiers = this.typeModifiers().typeModifier().wr
//        this.functionType()?.let{ return it.toSwift() }∂
//        this.nullableType()?.let{ return it.toSwift() }
//        this.parenthesizedType()?.let{ return "(" + it.type().toSwift() + ")" }
//        this.typeReference()?.let{ return it.toSwift() }
//        throw IllegalStateException()
//    }
//    fun KotlinParser.SimpleUserTypeContext.toSwift(): String {
//        val name = typeReplacements[this.simpleIdentifier().text] ?: this.simpleIdentifier().text
//        return name + (this.typeArguments()?.typeProjection()?.joinToString(", ", "<", ">") { it.type().toSwift() } ?: "")
//    }
//    fun KotlinParser.UserTypeContext.toSwift(): String = this.simpleUserType().joinToString(".") { it.toSwift() }
//    fun KotlinParser.FunctionTypeContext.toSwift(): String {
//        if(this.receiverType() != null){
//            throw UnsupportedOperationException("Receiver lambdas are not available in Swift.")
//        }
//        return this.functionTypeParameters().parameter().joinToString(", ", "(", ")") { it.type().toSwift() } + " -> " + this.type().toSwift()
//    }
//    fun KotlinParser.TypeReferenceContext.toSwift(): String {
//        this.DYNAMIC()?.let { return "Any" }
//        this.userType()?.let { return it.toSwift() }
//        throw IllegalStateException()
//    }
//    fun KotlinParser.NullableTypeContext.toSwift(): String {
//        this.typeReference()?.let { return it.toSwift() + "?" }
//        this.parenthesizedType()?.let { return "(" + it.type().toSwift() + ")?" }
//        throw IllegalStateException()
//    }
}
