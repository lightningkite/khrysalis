package com.lightningkite.khrysalis.web.typescript

import com.lightningkite.khrysalis.ios.swift.parentIfType
import com.lightningkite.khrysalis.utils.forEachBetween
import org.jetbrains.kotlin.KotlinParser

fun TypescriptTranslator.registerFile() {

    this.handle<KotlinParser.PackageHeaderContext> { }

    handle<KotlinParser.KotlinFileContext> {
        line("//Package: ${rule.packageHeader()?.identifier()?.text}")
        line("//Converted using Khrysalis2")
        line("")
        for (obj in rule.topLevelObject()) {
            line()
            -obj
        }
    }

    this.handle<KotlinParser.ImportListContext> {
        //all imports
        val takenNames = HashSet<String>()

        rule.importHeader()
            .filter { it.MULT() == null }
            .groupBy { identifiers[it.identifier().text] }
            .forEach { (group, values) ->
                if (group == null) return@forEach
                emit("import { ")
                values.forEachBetween(
                    forItem = { import ->
                        emit(import.identifier().text.substringAfterLast('.'))
                    },
                    between = {
                        emit(',')
                    }
                )
                emit(" } from \"/")
                emit(group)
                emit("\"")
                line()
            }

        rule.importHeader().filter { it.MULT() != null }.forEach {
            identifiersForPackage(it.identifier().text).groupBy { it.value }.forEach { group, items ->
                emit("import { ")
                items.filter { it.key !in takenNames }.forEachBetween(
                    forItem = { import ->
                        emit(import.key.substringAfterLast('.'))
                    },
                    between = {
                        emit(',')
                    }
                )
                emit(" } from \"/")
                emit(group)
                emit("\"")

            }
        }
        rule.parentIfType<KotlinParser.KotlinFileContext>()?.packageHeader()?.identifier()?.text?.let { packageId ->
            identifiersForPackage(packageId).groupBy { it.value }.forEach { group, items ->
                emit("import { ")
                items.filter { it.key !in takenNames }.forEachBetween(
                    forItem = { import ->
                        emit(import.key.substringAfterLast('.'))
                    },
                    between = {
                        emit(',')
                    }
                )
                emit(" } from \"/")
                emit(group)
                emit("\"")
                line()

            }
        }
    }
}
