package com.lightningkite.kwift.altswift

import com.lightningkite.kwift.interfaces.InterfaceListener
import com.lightningkite.kwift.swift.TabWriter
import com.lightningkite.kwift.utils.forEachBetween
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.TerminalNode
import org.jetbrains.kotlin.KotlinParser

class SwiftAltListener {
    var interfaces: Map<String, InterfaceListener.InterfaceData> = mapOf()
    var currentFile: KotlinParser.KotlinFileContext? = null

    fun KotlinParser.ClassDeclarationContext.implements(): Sequence<InterfaceListener.InterfaceData>{
        val currentFile = currentFile ?: return sequenceOf()
        return this.delegationSpecifiers()?.delegationSpecifier()?.asSequence()
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

    fun KotlinParser.ClassDeclarationContext.implementsPossibleNames(): Sequence<String>{
        val currentFile = currentFile ?: return sequenceOf()
        return this.delegationSpecifiers()?.delegationSpecifier()?.asSequence()
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
            ?: sequenceOf()
    }

    val options = HashMap<Class<*>, TabWriter.(ParserRuleContext)->Unit>()
    val tokenOptions = HashMap<Int, TabWriter.(TerminalNode)->Unit>()
    val functionReplacements = HashMap<String, String>()
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
        tokenOptions[KotlinParser.EXCL_EXCL] = { direct.append("!") }
        tokenOptions[KotlinParser.ELVIS] = { direct.append("??") }

        typeReplacements["Map"] = "Dictionary"
        typeReplacements["List"] = "Array"
        typeReplacements["HashMap"] = "Dictionary"
        typeReplacements["ArrayList"] = "Array"

        functionReplacements["println"] = "print"
    }

    fun TabWriter.write(node: TerminalNode){
        tokenOptions[node.symbol.type]?.invoke(this, node) ?: this.direct.append(node.text)
    }
    fun TabWriter.write(item: ParserRuleContext){
        options[item::class.java]?.invoke(this, item) ?: run {
            item.children?.forEachBetween(
                forItem = { child ->
                    when(child){
                        is ParserRuleContext -> write(child)
                        is TerminalNode -> write(child)
                    }
                },
                between = {
                    direct.append(' ')
                }
            )
        }
    }
    fun KotlinParser.TypeContext.toSwift(): String {
        this.functionType()?.let{ return it.toSwift() }
        this.nullableType()?.let{ return it.toSwift() }
        this.parenthesizedType()?.let{ return "(" + it.type().toSwift() + ")" }
        this.typeReference()?.let{ return it.toSwift() }
        throw IllegalStateException()
    }
    fun KotlinParser.SimpleUserTypeContext.toSwift(): String {
        val name = typeReplacements[this.simpleIdentifier().text] ?: this.simpleIdentifier().text
        return name + (this.typeArguments()?.typeProjection()?.joinToString(", ", "<", ">") { it.type().toSwift() } ?: "")
    }
    fun KotlinParser.UserTypeContext.toSwift(): String = this.simpleUserType().joinToString(".") { it.toSwift() }
    fun KotlinParser.FunctionTypeContext.toSwift(): String {
        if(this.functionTypeReceiver() != null){
            throw UnsupportedOperationException("Receiver lambdas are not available in Swift.")
        }
        return this.functionTypeParameters().parameter().joinToString(", ", "(", ")") { it.type().toSwift() } + " -> " + this.type().toSwift()
    }
    fun KotlinParser.TypeReferenceContext.toSwift(): String {
        this.typeReference()?.let { return it.toSwift() + "?" }
        this.userType()?.let { return it.toSwift() }
        throw IllegalStateException()
    }
    fun KotlinParser.NullableTypeContext.toSwift(): String {
        this.typeReference()?.let { return it.toSwift() + "?" }
        this.parenthesizedType()?.let { return "(" + it.type().toSwift() + ")?" }
        throw IllegalStateException()
    }
}
