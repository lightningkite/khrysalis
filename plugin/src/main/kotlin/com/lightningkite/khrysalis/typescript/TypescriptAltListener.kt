package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.preparse.PreparseData
import com.lightningkite.khrysalis.swift.TabWriter
import com.lightningkite.khrysalis.utils.forEachBetween
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNode
import org.jetbrains.kotlin.KotlinParser
import java.lang.IllegalStateException
import java.util.*
import kotlin.collections.HashMap

class TypescriptAltListener {

    var preparseData: PreparseData = PreparseData()
        set(value){
            field = value
            identifiers.clear()
            identifiers.putAll(value.declarations)
        }
    /*

    UNSOLVED PROBLEMS

    UNSOLVED BUT LESS WORRYING PROBLEMS

    Circular import dependencies
        - Check usage location for types; if so, just use `import type {} from "/asdf/asdf"`
        - This might not be an issue, because unless the top level directly calls immediately, you're fine
            - One hairy immediate call is extension, though.  That might cause problems.
            - As long as superclasses NEVER refer to their children in a different file, you're good.
    Operator overloading
        - This is outright not possible.  This could be somewhat an issue.
        - This is only used for date manipulation.
    Equality/Hashability for data classes
        - This might be passable.  Maybe even just disallow data classes.


    SOLVED PROBLEMS

    Extension Properties
        - https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/defineProperty
    Function overloading
        - https://howtodoinjava.com/typescript/function-overloading/
        - https://github.com/lightningkite/lovesac-web/blob/master/client/js/utils/geometry/Point.ts#L57
    Extension function typing
        - https://medium.com/my-coding-life/extension-method-in-typescript-66d801488589
    Extension function overloading
        - Are there any cases of this occurring?
        - This could be done with chained overloading - call existing if arguments do not match, call super

    */

    /*

    Handling imports for TypeScript

    All classes are stored in independent files, located based on their package
    All top-level declarations are stored in a single file called 'topLevel'.
    The 'topLevel' file is special and uses marker separators for source files, like:
    //--- String.extensions.shared.kt
    In this sense, we have virtual files stored within.

    Imports now work like this:
    import x.y.z.Class -> import { Class } from "x/y/z/Class"
    import x.y.z.topLevelFunction -> import { topLevelFunction } from "x/y/z/topLevel"

    */

    /**
     * This is a map from a Kotlin fully-qualified name to TypeScript file location.
     */
    var identifiers = TreeMap<String, String>()
    fun identifiersForPackage(packageName: String) = identifiers.tailMap(packageName).asSequence().takeWhile { it.key.startsWith(packageName) }.filter { it.key.substringBeforeLast('.') == packageName }

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
        registerFile()
//        registerClass()
//        registerFunction()
//        registerVariable()
//        registerExpression()
//        registerLiterals()
//        registerLambda()
//        registerControl()
//        registerType()
//        registerStatement()

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

        typeReplacements["Map"] = "Record"
        typeReplacements["List"] = "Array"
        typeReplacements["MutableMap"] = "Record"
        typeReplacements["MutableList"] = "Array"
        typeReplacements["MutableSet"] = "Set"
        typeReplacements["HashMap"] = "Record"
        typeReplacements["HashSet"] = "Set"
        typeReplacements["ArrayList"] = "Array"

        typeReplacements["Boolean"] = "boolean"
        typeReplacements["Unit"] = "void"
        typeReplacements["Char"] = "string"
        typeReplacements["Byte"] = "number"
        typeReplacements["Short"] = "number"
        typeReplacements["Int"] = "number"
        typeReplacements["Long"] = "number"
        typeReplacements["String"] = "string"
        typeReplacements["Any"] = "any"
        typeReplacements["Exception"] = "Error"

        //TODO: Typealias pair and triple

        simpleFunctionReplacement("println", "console.log")
        simpleFunctionReplacement("ArrayList", "Array")
        simpleFunctionReplacement("HashMap", "Record")
        simpleFunctionReplacement("HashSet", "Set")

        functionReplacements["run"] = {
            direct.append("function(){")
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
            direct.append("null")
        }

        functionReplacements["Pair"] = {
            val (typeArgs, params) = it.typeArgsAndParams()
            val valArgs = params.valueArguments().valueArgument()
            direct.append('[')
            write(valArgs[0])
            direct.append(", ")
            write(valArgs[1])
            direct.append(']')
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
                direct.append(">()")
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

        //TODO: setOf

        functionReplacements["mapOf"] = {
            val (typeArgs, params) = it.typeArgsAndParams()
            params.valueArguments()?.valueArgument()?.takeUnless { it.isEmpty() }?.let { valueArgs ->
                direct.append("{")
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
                direct.append("}")
            } ?: typeArgs?.let {
                direct.append("Record<")
                write(it[0])
                direct.append(", ")
                write(it[1])
                direct.append(">")
            } ?: run {
                direct.append("{}")
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
