package com.lightningkite.kwift.altswift

import org.jetbrains.kotlin.KotlinParser

fun SwiftAltListener.registerExpression() {
    handle<KotlinParser.AsOperatorContext> { item ->
        when {
            item.AS() != null -> direct.append("as!")
            item.AS_SAFE() != null -> direct.append("as?")
        }
    }
    handle<KotlinParser.AssignableSuffixContext> { item ->
        defaultWrite(item, "")
    }
    handle<KotlinParser.NavigationSuffixContext> { item ->
        defaultWrite(item, "")
    }
    handle<KotlinParser.PrimaryExpressionContext> { item ->
        if(item.simpleIdentifier()?.text == "Unit") direct.append("()")
        else defaultWrite(item)
    }
}
