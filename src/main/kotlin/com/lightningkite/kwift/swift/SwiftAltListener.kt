//package com.lightningkite.kwift.swift
//
//import com.lightningkite.kwift.utils.forEachBetween
//import org.antlr.v4.runtime.ParserRuleContext
//import org.jetbrains.kotlin.KotlinParser
//
//fun TabWriter.write(item: KotlinParser.KotlinFileContext) {
//    line("//Package: ${item.packageHeader()}")
//    line("//Converted using Mirror")
//    line("")
//    line("import Foundation")
//    line("")
//    line("")
//    for (obj in item.topLevelObject()){
//        write(obj)
//    }
//}
//
//fun TabWriter.write(item: KotlinParser.TopLevelObjectContext) {
//    item.classDeclaration()?.let{ write(it) }
//    item.functionDeclaration()?.let{ write(it) }
//    item.objectDeclaration()?.let{ write(it) }
//    item.propertyDeclaration()?.let{ write(it) }
//}
//
//fun TabWriter.write(item: ParserRuleContext) {
//    item.
//}
//fun TabWriter.write(item: KotlinParser.ExpressionContext) {}
//
//fun TabWriter.write(item: KotlinParser.ClassDeclarationContext) {
//    line("public class ${item.simpleIdentifier()}")
//    line {
//        append("public class ")
//        append(item.simpleIdentifier().text)
//        item.delegationSpecifiers()?.let {dg ->
//            append(": ")
//            dg.delegationSpecifier().forEachBetween(
//                forItem = {
//                    it.constructorInvocation()?.let {
//                        append(it.userType().text)
//                    }
//                    it.userType()?.let{ append(it.text) }
//                    it.explicitDelegation()?.let {
//                        throw IllegalArgumentException("Explicit delegation not supported")
//                    }
//                },
//                between = {
//                    append(", ")
//                }
//            )
//        }
//        append(" {")
//    }
//    tab {
//        line("")
//
//        item.primaryConstructor()?.classParameters()?.classParameter()?.forEach {
//            line("public var ${it.simpleIdentifier().text}: ${it.type().text}")
//        }
//
//        line("")
//
//        line {
//            append("init(")
//            item.primaryConstructor()?.classParameters()?.classParameter()?.forEachBetween(
//                forItem = {
//                    append(it.simpleIdentifier().text)
//                    append(": ")
//                    append(it.type().text)
//                    it.expression()?.let {
//                        append(" = ")
//                        write(it)
//                    }
//                },
//                between = {
//                    append(", ")
//                }
//            )
//            append(")")
//
//
//        }
//
//        line("")
//    }
//    line("}")
//}
//
//
//fun TabWriter.write(item: KotlinParser.FunctionDeclarationContext) {
//
//}
//
//
//fun TabWriter.write(item: KotlinParser.ObjectDeclarationContext) {
//
//}
//
//
//fun TabWriter.write(item: KotlinParser.PropertyDeclarationContext) {
//
//}
