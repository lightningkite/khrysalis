package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.swift.parentIfType
import com.lightningkite.khrysalis.utils.forEachBetween
import org.jetbrains.kotlin.KotlinParser

fun TypescriptAltListener.registerFile() {

    this.handle<KotlinParser.ImportListContext> { ctx ->
        //all imports
        val takenNames = HashSet<String>()

        ctx.importHeader()
            .filter { it.MULT() == null }
            .groupBy { identifiers[it.identifier().text] }
            .forEach { (group, values) ->
                if(group == null) return@forEach
                line {
                    append("import { ")
                    values.forEachBetween(
                        forItem = { import ->
                            append(import.identifier().text.substringAfterLast('.'))
                        },
                        between = {
                            append(',')
                        }
                    )
                    append(" } from \"/")
                    append(group)
                    append("\"")
                }
            }

        ctx.importHeader().filter { it.MULT() != null }.forEach {
            identifiersForPackage(it.identifier().text).groupBy { it.value }.forEach { group, items ->
                line {
                    append("import { ")
                    items.filter { it.key !in takenNames }.forEachBetween(
                        forItem = { import ->
                            append(import.key.substringAfterLast('.'))
                        },
                        between = {
                            append(',')
                        }
                    )
                    append(" } from \"/")
                    append(group)
                    append("\"")
                }
            }
        }
        ctx.parentIfType<KotlinParser.KotlinFileContext>()?.packageHeader()?.identifier()?.text?.let { packageId ->
            identifiersForPackage(packageId).groupBy { it.value }.forEach { group, items ->
                line {
                    append("import { ")
                    items.filter { it.key !in takenNames }.forEachBetween(
                        forItem = { import ->
                            append(import.key.substringAfterLast('.'))
                        },
                        between = {
                            append(',')
                        }
                    )
                    append(" } from \"/")
                    append(group)
                    append("\"")
                }
            }
        }
    }
}
