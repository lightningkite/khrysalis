package com.lightningkite.kwift.swift

import com.lightningkite.kwift.interfaces.InterfaceListener
import com.lightningkite.kwift.swift.TabWriter
import com.lightningkite.kwift.utils.forEachBetween
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.TerminalNode
import org.jetbrains.kotlin.KotlinParser

class SwiftAltListener {
    var interfaces: Map<String, InterfaceListener.InterfaceData> = mapOf()
    var currentFile: KotlinParser.KotlinFileContext? = null
    var filterEscapingAnnotation: Boolean = false
    var imports = listOf<String>()

    fun KotlinParser.ClassDeclarationContext.implements(): Sequence<InterfaceListener.InterfaceData>{
        val currentFile = currentFile ?: return sequenceOf()
        return this.delegationSpecifiers()?.annotatedDelegationSpecifier()
            ?.asSequence()
            ?.map { it.delegationSpecifier() }
            ?.mapNotNull { it.userType()?.text }
            ?.flatMap {
                if(it.firstOrNull()?.isUpperCase() == true){
                    currentFile.importList().importHeader().asSequence()
                        .filter { it.MULT() != null }
                        .map { import ->
                            import.identifier().text + "." + it
                        }
                        .plus(currentFile.packageHeader().identifier().text + "." + it)
                } else {
                    sequenceOf(it)
                }
            }
            ?.mapNotNull { interfaces[it] }
            ?: sequenceOf()
    }

    val options = HashMap<Class<*>, TabWriter.(ParserRuleContext)->Unit>()
    val tokenOptions = HashMap<Int, TabWriter.(TerminalNode)->Unit>()
    val functionReplacements = HashMap<String, TabWriter.(KotlinParser.PostfixUnaryExpressionContext)->Unit>()
    fun simpleFunctionReplacement(kotlinFunctionName: String, swiftFunctionName: String) {
        functionReplacements[kotlinFunctionName] = {
            direct.append(swiftFunctionName)
            it.postfixUnarySuffix().forEach { write(it) }
        }
    }
    val typeReplacements = HashMap<String, String>()

    inline fun <reified T: ParserRuleContext> handle(noinline action: TabWriter.(T)->Unit){
        options[T::class.java] = { action(it as T) }
    }
    fun handle(rule: Int, action: TabWriter.(TerminalNode)->Unit){
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
        typeReplacements["HashMap"] = "Dictionary"
        typeReplacements["ArrayList"] = "Array"
        typeReplacements["Boolean"] = "Bool"
        typeReplacements["Unit"] = "Void"
        typeReplacements["Byte"] = "Int8"
        typeReplacements["Short"] = "Int16"
        typeReplacements["Int"] = "Int32"
        typeReplacements["Long"] = "Int64"

        simpleFunctionReplacement("println", "print")
        simpleFunctionReplacement("ArrayList", "Array")
        simpleFunctionReplacement("HashMap", "Dictionary")
        functionReplacements["run"] = {
            direct.append("{ () in ")
            it.postfixUnarySuffix()[0]!!.callSuffix()!!.annotatedLambda()!!.lambdaLiteral()!!.statements()!!.statement().forEach {
                startLine()
                write(it)
            }
            startLine()
            direct.append("}()")
        }

        functionReplacements["listOf"] = {
            direct.append("[")
            it.postfixUnarySuffix()[0]!!.callSuffix()!!.valueArguments()?.valueArgument()?.forEachBetween(
                forItem = { write(it) },
                between = { direct.append(", ") }
            )
            direct.append("]")
        }
        functionReplacements["arrayListOf"] = functionReplacements["listOf"]!!
        functionReplacements["mutableListOf"] = functionReplacements["listOf"]!!

        functionReplacements["mapOf"] = {
            direct.append("[")
            it.postfixUnarySuffix()[0]!!.callSuffix()?.valueArguments()?.valueArgument()?.takeUnless { it.isEmpty() }?.forEachBetween(
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
            ) ?: run {
                direct.append(":")
            }
            direct.append("]")
        }
        functionReplacements["hashMapOf"] = functionReplacements["mapOf"]!!
        functionReplacements["mutableMapOf"] = functionReplacements["mapOf"]!!
    }

    fun TabWriter.write(node: TerminalNode){
//        println("Appending node '${node.text}' of type ${KotlinParser.VOCABULARY.getDisplayName(node.symbol.type)}")
        tokenOptions[node.symbol.type]?.invoke(this, node) ?: this.direct.append(node.text)
    }
    fun TabWriter.write(item: ParserRuleContext){
        options[item::class.java]?.invoke(this, item) ?: defaultWrite(item)
    }
    fun TabWriter.defaultWrite(item: ParserRuleContext, between: String = " "){
        item.children?.forEachBetween(
            forItem = { child ->
                when(child){
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
