package com.lightningkite.kwift.swift

import com.lightningkite.kwift.utils.forEachBetween
import org.antlr.v4.runtime.ParserRuleContext
import org.jetbrains.kotlin.KotlinParser


fun SwiftAltListener.startExtension(
    writer: TabWriter,
    typeParameters: KotlinParser.TypeParametersContext?,
    receiverType: KotlinParser.ReceiverTypeContext
): Set<String> = with(writer){
    val typeArgumentNames =
        typeParameters?.typeParameter()?.map { it.simpleIdentifier().text.trim('?') }?.toSet() ?: setOf()

    fun findUsages(item: ParserRuleContext): Sequence<String> {
        return when (item) {
            is KotlinParser.SimpleIdentifierContext -> sequenceOf(item.text)
            else -> item.children.asSequence().mapNotNull { it as? ParserRuleContext }.flatMap { findUsages(it).asSequence() }
        }
    }

    val typeArgumentsInReceiver =
        findUsages(receiverType).distinct().filter { it in typeArgumentNames }.toSet()
    val otherTypeArguments = typeArgumentNames - typeArgumentsInReceiver
    val receiverWithoutParameters =
        receiverType.getUserType().simpleUserType()?.joinToString(".") {
            it.simpleIdentifier()!!.text.trim(
                '?'
            )
        } ?: ""
    val receiverDirectUsages =
        receiverType.getUserType().simpleUserType()?.lastOrNull()?.typeArguments()?.typeProjection()
            ?.filter { it.type().text.trim('?') !in typeArgumentNames }

    val whereConditions = ArrayList<() -> Unit>()
    typeParameters?.typeParameter()
        ?.filter { it.simpleIdentifier().text.trim('?') in typeArgumentsInReceiver }
        ?.filter { it.type() != null }
        ?.takeUnless { it.isEmpty() }
        ?.map {
            { ->
                direct.append(it.simpleIdentifier().text.trim('?'))
                if (receiverType.typeParamFinal(it.type())) {
                    direct.append(" == ")
                } else {
                    direct.append(": ")
                }
                write(it.type())
                Unit
            }
        }
        ?.let { whereConditions += it }
    receiverDirectUsages?.forEachIndexed { index, it ->
        whereConditions += {
            direct.append(
                receiverType.typeParamName(
                    type = it.type(),
                    annotations = it.typeProjectionModifiers()?.typeProjectionModifier()?.mapNotNull { it.annotation() },
                    totalCount = receiverDirectUsages.size,
                    index = index
                )
            )
            if (receiverType.typeParamFinal(
                    type = it.type(),
                    annotations = it.typeProjectionModifiers()?.typeProjectionModifier()?.mapNotNull { it.annotation() }
                )
            ) {
                direct.append(" == ")
            } else {
                direct.append(": ")
            }
            write(it.type())
        }
    }

    line {
        append("extension ${receiverWithoutParameters.let { typeReplacements[it] ?: it }}")
        if (whereConditions.isNotEmpty()) {
            append(" where ")
            whereConditions.forEachBetween(
                forItem = { it() },
                between = { append(", ") }
            )
        }
        append(" {")
    }
    return otherTypeArguments
}
