package com.lightningkite.khrysalis.typescript

import java.lang.IllegalArgumentException

fun TypescriptTranslator.registerControl() {
//    handle<ForStatementContext> {
//        val rule = typedRule
//        -"for (const "
//        -rule.variableDeclaration()!!
//        -" of "
//        -rule.expression()
//        -") "
//        -rule.controlStructureBody()
//    }
//
//    handle<BlockContext> {
//        val rule = typedRule
//        -"{\n"
//        rule.statements().statement().forEachBetween(
//            forItem = { -it },
//            between = { -";\n" }
//        )
//        -"\n}"
//    }
//
//    handle<ControlStructureBodyContext>(
//        condition = { typedRule.getOwningExpression()?.usedAsStatement() == false },
//        priority = 100
//    ) {
//
//        typedRule.block()?.let {
//            -"{\n"
//            it.statements()?.statement()?.dropLast(1)?.forEach {
//                -it
//                -";\n"
//            }
//            it.statements()?.statement()?.lastOrNull()?.let {
//                if (it.expression()
//                        ?.disjunction()
//                        ?.conjunction()?.oneOnly()
//                        ?.equality()?.oneOnly()
//                        ?.comparison()?.oneOnly()
//                        ?.infixOperation()?.oneOnly()
//                        ?.elvisExpression()?.oneOnly()
//                        ?.infixFunctionCall()?.oneOnly()
//                        ?.rangeExpression()?.oneOnly()
//                        ?.additiveExpression()?.oneOnly()
//                        ?.multiplicativeExpression()?.oneOnly()
//                        ?.asExpression()?.oneOnly()
//                        ?.prefixUnaryExpression()
//                        ?.postfixUnaryExpression()
//                        ?.primaryExpression()
//                        ?.let {
//                            it.ifExpression() ?: it.whenExpression()
//                        } == null
//                ) {
//                    -"return "
//                }
//                -it
//                -";\n"
//            }
//            -"}"
//        } ?: typedRule.statement()?.let {
//            if (it.expression()
//                    ?.disjunction()
//                    ?.conjunction()?.oneOnly()
//                    ?.equality()?.oneOnly()
//                    ?.comparison()?.oneOnly()
//                    ?.infixOperation()?.oneOnly()
//                    ?.elvisExpression()?.oneOnly()
//                    ?.infixFunctionCall()?.oneOnly()
//                    ?.rangeExpression()?.oneOnly()
//                    ?.additiveExpression()?.oneOnly()
//                    ?.multiplicativeExpression()?.oneOnly()
//                    ?.asExpression()?.oneOnly()
//                    ?.prefixUnaryExpression()
//                    ?.postfixUnaryExpression()
//                    ?.primaryExpression()
//                    ?.let {
//                        it.ifExpression() ?: it.whenExpression()
//                    } == null
//            ) {
//                -"{\n"
//                -"return "
//                -it
//                -";\n"
//                -"}"
//            }else{
//                -it
//                -"\n"
//            }
//        }
//    }
//
//    handle<WhileStatementContext> {
//        val rule = typedRule
//        -"while ("
//        -rule.expression()
//        -") "
//        -rule.controlStructureBody()
//    }
//
//    handle<IfExpressionContext>(
//        condition = { rule.getPlainStatement()?.getDirectControlStructure() != null },
//        priority = 100
//    ) {
//        val rule = typedRule
//        -"if ("
//        -rule.expression()
//        -")"
//        -rule.controlStructureBody(0)
//        rule.controlStructureBody(1)?.let {
//            -" else "
//            -it
//        }
//    }
//
//    handle<IfExpressionContext>(
//        condition = { rule.usedAsStatement() },
//        priority = 100
//    ) {
//        val rule = typedRule
//        -"if ("
//        -rule.expression()
//        -")"
//        -rule.controlStructureBody(0)
//        rule.controlStructureBody(1)?.let {
//            -" else "
//            -it
//        }
//    }
//
//    handle<IfExpressionContext> {
//        val rule = typedRule
//
//        -"(() => {"
//        -"if ("
//        -rule.expression()
//        -")"
//        -rule.controlStructureBody(0)
//        rule.controlStructureBody(1)?.let {
//            -" else "
//            -it
//        }
//        -"})()"
//    }
//
//    val normalWhen = handle<WhenExpressionContext>(
//        condition = {
//            val rule = typedRule
//            rule.usedAsStatement() && rule.whenSubject() != null &&
//                    rule.whenEntry()
//                        .all { it.ELSE() != null || it.whenCondition().all { it.expression() != null } }
//        },
//        priority = 100
//    ) {
//        val rule = typedRule
//        rule.whenSubject().variableDeclaration()?.let {
//            -it
//            -" = "
//            -rule.whenSubject().expression()
//            -";\n"
//            -"switch("
//            -it.simpleIdentifier()
//            -") {\n"
//        } ?: run {
//            -"switch("
//            -rule.whenSubject().expression()
//            -") {\n"
//        }
//        for (entry in rule.whenEntry()) {
//            entry.ELSE()?.let {
//                -"default:\n"
//                entry.controlStructureBody()?.let {
//                    it.block()?.let {
//                        it.statements()?.statement()?.forEach {
//                            -it
//                            -";\n"
//                        }
//                    } ?: it.statement()?.let {
//                        -it
//                        -";\n"
//                    }
//                }
//                -"break;\n"
//            } ?: entry.whenCondition().let {
//                for (cond in it) {
//                    -"case "
//                    -cond.expression()
//                    -":\n"
//                }
//                entry.controlStructureBody()?.let {
//                    it.block()?.let {
//                        it.statements()?.statement()?.forEach {
//                            -it
//                            -";\n"
//                        }
//                    } ?: it.statement()?.let {
//                        -it
//                        -";\n"
//                    }
//                }
//                -"break;\n"
//            }
//        }
//        -"}"
//    }
//
//    handle<WhenExpressionContext>(
//        condition = {
//            val rule = typedRule
//            rule.whenSubject() != null &&
//                    rule.whenEntry()
//                        .all { it.ELSE() != null || it.whenCondition().all { it.expression() != null } }
//        },
//        priority = 50
//    ) {
//        val rule = typedRule
//        -"(() => {\n"
//        rule.whenSubject().variableDeclaration()?.let {
//            -it
//            -" = "
//            -rule.whenSubject().expression()
//            -";\n"
//            -"switch("
//            -it.simpleIdentifier()
//            -") {\n"
//        } ?: run {
//            -"switch("
//            -rule.whenSubject().expression()
//            -") {\n"
//        }
//        for (entry in rule.whenEntry()) {
//            entry.ELSE()?.let {
//                -"default:\n"
//                entry.controlStructureBody()?.let {
//                    it.block()?.let {
//                        it.statements()?.statement()?.dropLast(1)?.forEach {
//                            -it
//                            -";\n"
//                        }
//                        it.statements()?.statement()?.lastOrNull()?.let {
//                            -"return "
//                            -it
//                            -";\n"
//                        }
//                    } ?: it.statement()?.let {
//                        -"return "
//                        -it
//                        -";\n"
//                    }
//                }
//            } ?: entry.whenCondition().let {
//                for (cond in it) {
//                    -"case "
//                    -cond.expression()
//                    -":\n"
//                }
//                entry.controlStructureBody()?.let {
//                    it.block()?.let {
//                        it.statements()?.statement()?.dropLast(1)?.forEach {
//                            -it
//                            -";\n"
//                        }
//                        it.statements()?.statement()?.lastOrNull()?.let {
//                            -"return "
//                            -it
//                            -";\n"
//                        }
//                    } ?: it.statement()?.let {
//                        -"return "
//                        -it
//                        -";\n"
//                    }
//                }
//            }
//        }
//        -"}"
//        -"\n})()"
//    }
//
//
//    handle<WhenExpressionContext>(
//        condition = {
//            val rule = typedRule
//            rule.whenSubject() == null &&
//                    rule.whenEntry()
//                        .all { it.ELSE() != null || it.whenCondition().all { it.expression() != null } }
//        },
//        priority = 51
//    ) {
//        val rule = typedRule
//        -"(() => {\n"
//        rule.whenEntry().forEachIndexed { index, entry ->
//            when {
//                index == 0 -> {
//                    -"if ("
//                }
//                entry.ELSE() != null -> {
//                    -"else "
//                }
//                else -> {
//                    -"else if ("
//                }
//            }
//
//            if (entry.ELSE() != null) {
//                entry.controlStructureBody()?.let {
//                    -it
////                    it.block()?.let {
////                        it.statements()?.statement()?.dropLast(1)?.forEach {
////                            -it
////                            -";\n"
////                        }
////                        it.statements()?.statement()?.lastOrNull()?.let {
////                            -"return "
////                            -it
////                            -";\n"
////                        }
////                    } ?: it.statement()?.let {
////                        -"return "
////                        -it
////                        -";\n"
////                    }
//                }
//            } else {
//                entry.whenCondition().let { conditions ->
//                    conditions.forEachBetween(
//                        {
//                            -it.expression()
//                        },
//                        {
//                            -" || "
//                        })
//                    -")"
//                }
//                entry.controlStructureBody()?.let {
//                    -it
////                    it.block()?.let {
////                        it.statements()?.statement()?.dropLast(1)?.forEach {
////                            -it
////                            -";\n"
////                        }
////                        it.statements()?.statement()?.lastOrNull()?.let {
////                            -"return "
////                            -it
////                            -";\n"
////                        }
////                    } ?: it.statement()?.let {
////                        -"return "
////                        -it
////                        -";\n"
////                    }
//                }
//            }
//        }
//        -"\n})()"
//    }
//
//    handle<WhenExpressionContext>(
//        condition = {
//            val rule = typedRule
//            rule.usedAsStatement() && rule.whenSubject() == null &&
//                    rule.whenEntry()
//                        .all { it.ELSE() != null || it.whenCondition().all { it.expression() != null } }
//        },
//        priority = 120
//    ) {
//        val rule = typedRule
//        rule.whenEntry().forEachIndexed { index, entry ->
//            when {
//                index == 0 -> {
//                    -"if ("
//                }
//                entry.ELSE() != null -> {
//                    -"else { \n"
//                }
//                else -> {
//                    -"else if ("
//                }
//            }
//
//            if (entry.ELSE() != null) {
//                entry.controlStructureBody()?.let {
//                    it.block()?.let {
//                        it.statements()?.statement()?.forEach {
//                            -it
//                            -";\n"
//                        }
//                    } ?: it.statement()?.let {
//                        -it
//                        -";\n"
//                    }
//                }
//                -"}"
//            } else {
//                entry.whenCondition().let { conditions ->
//                    conditions.forEachBetween(
//                        {
//                            -it.expression()
//                        },
//                        {
//                            -" || "
//                        })
//                    -") {\n"
//                }
//                entry.controlStructureBody()?.let {
//                    it.block()?.let {
//                        it.statements()?.statement()?.forEach {
//                            -it
//                            -";\n"
//                        }
//                    } ?: it.statement()?.let {
//                        -it
//                        -";\n"
//                    }
//                }
//                -"}"
//            }
//        }
//    }
}

