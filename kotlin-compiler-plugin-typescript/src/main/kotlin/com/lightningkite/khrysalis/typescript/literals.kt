package com.lightningkite.khrysalis.typescript

fun TypescriptTranslator.registerLiterals() {
//    handle<KotlinParser.LineStringLiteralContext> {
//        val item = typedRule
//        val quoteType = if (item.children.any { it is KotlinParser.LineStringExpressionContext }) "`" else "\""
//        -(quoteType)
//        item.children.forEach {
//            when (it) {
//                is KotlinParser.LineStringContentContext -> {
//                    if (it.text.startsWith("$") && it.text.length > 1) {
//                        -("\${")
//                        -(it.text.removePrefix("$"))
//                        -("}")
//                    } else {
//                        -(it.text.replace("\\$", "$"))
//                    }
//                }
//                is KotlinParser.LineStringExpressionContext -> {
//                    -("\${")
//                    write(it.expression())
//                    -("}")
//                }
//            }
//        }
//        -(quoteType)
//    }
//    handle<KotlinParser.ElvisContext> {
//        -("??")
//    }
//    handle<KotlinParser.PostfixUnaryOperatorContext> {
//        val rule = typedRule
//        val item = rule
//        item.excl()?.let { -("!") } ?: item.INCR()?.let { -(" += 1") } ?: item.DECR()
//            ?.let { -(" -= 1") }
//    }
//    handle(KotlinParser.RealLiteral) {
//        val rule = rule
//        val it = rule
//        if (it.text.endsWith('f', true)) {
//            -(it.text.removeSuffix("f").removeSuffix("F"))
//        } else {
//            if (it.text.startsWith('.')) {
//                -('0')
//            }
//            -(it.text)
//        }
//    }
//    handle(KotlinParser.LongLiteral) {
//        val it = rule
//        -(it.text.removeSuffix("l").removeSuffix("L"))
//    }
//    handle(KotlinParser.CharacterLiteral) {
//        val it = rule
//        -(it.text.trim('\'').let { "\"$it\"" })
//    }
}
