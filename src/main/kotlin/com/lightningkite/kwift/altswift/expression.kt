package com.lightningkite.kwift.altswift

import org.jetbrains.kotlin.KotlinParser

fun SwiftAltListener.registerExpression() {
    handle<KotlinParser.AsOperatorContext> { item ->
        when {
            item.AS() != null -> direct.append("as!")
            item.AS_SAFE() != null -> direct.append("as?")
        }
    }
    handle<KotlinParser.DotQualifiedExpressionContext> { item ->
        write(item.assignableExpression())
        item.memberAccessOperator()?.forEachIndexed { index, it ->
            direct.append('.')
            write(item.postfixUnaryExpression(index))
        }
    }
}
