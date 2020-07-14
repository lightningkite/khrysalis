package com.lightningkite.khrysalis.swift

import com.lightningkite.khrysalis.util.forEachBetween
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.descriptors.ValueDescriptor
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.allChildren

data class IfCondition(val expression: KtExpression)

fun SwiftTranslator.registerControl() {

    handle<KtBlockExpression> {
        val lastStatement = typedRule.statements.lastOrNull()
        typedRule.allChildren.forEach {
            if (it === lastStatement && it.actuallyCouldBeExpression) {
                -"return "
            }
            -it
        }
    }

    handle<KtContainerNodeForControlStructureBody>(
        condition = { (typedRule.parent as? KtExpression)?.actuallyCouldBeExpression == true },
        priority = 100
    ) {
        if (typedRule.expression is KtBlockExpression || typedRule.expression is KtIfExpression) {
            -typedRule.expression
        } else {
            -"{\n"
            if (typedRule.expression!!.actuallyCouldBeExpression) {
                -"return "
            }
            -typedRule.expression
            -"\n}\n"
        }
    }

    handle<IfCondition>(
        condition = {
            val exp = typedRule.expression as? KtBinaryExpression ?: return@handle false
            exp.operationToken == KtTokens.EXCLEQ && exp.left is KtNameReferenceExpression && exp.right!!.text == "null"
        },
        priority = 10,
        action = {
            val exp = typedRule.expression as KtBinaryExpression
            val vd = (exp.left as KtNameReferenceExpression).resolvedReferenceTarget as? ValueDescriptor
            vd?.let { ignoreSmartcast(it) }
            -"let "
            -exp.left
            -" = "
            -exp.left
        }
    )

    handle<IfCondition>(
        condition = {
            val exp = typedRule.expression as? KtBinaryExpression ?: return@handle false
            exp.operationToken == KtTokens.EXCLEQ && exp.right is KtNameReferenceExpression && exp.left!!.text == "null"
        },
        priority = 10,
        action = {
            val exp = typedRule.expression as KtBinaryExpression
            val vd = (exp.right as KtNameReferenceExpression).resolvedReferenceTarget as? ValueDescriptor
            vd?.let { ignoreSmartcast(it) }
            -"let "
            -exp.right
            -" = "
            -exp.right
        }
    )

    handle<IfCondition>(
        condition = {
            val exp = typedRule.expression as? KtIsExpression ?: return@handle false
            exp.leftHandSide is KtNameReferenceExpression
        },
        priority = 10,
        action = {
            val exp = typedRule.expression as KtIsExpression
            val vd = (exp.leftHandSide as KtNameReferenceExpression).resolvedReferenceTarget as? ValueDescriptor
            vd?.let { ignoreSmartcast(it) }
            -"let "
            -exp.leftHandSide
            -" = "
            -exp.leftHandSide
            -" as? "
            -exp.typeReference
        }
    )

    handle<IfCondition> {
        -typedRule.expression
    }

    handle<KtBinaryExpression>(
        condition = { typedRule.operationToken == KtTokens.ANDAND && typedRule.parentOfType<KtIfExpression>()?.condition == typedRule },
        priority = 100,
        action = {
            val elements = ArrayList<KtExpression>()
            var current: KtExpression = typedRule
            while (current is KtBinaryExpression && current.operationToken == KtTokens.ANDAND) {
                elements.add(current.right!!)
                current = current.left!!
            }
            elements.add(current)
            elements.reverse()
            elements.forEachBetween(
                forItem = {
                    -IfCondition(it)
                },
                between = {
                    -", "
                }
            )
        }
    )

    handle<KtIfExpression> {
        beginSmartcastBlock()
        if (typedRule.actuallyCouldBeExpression && typedRule.parent !is KtContainerNodeForControlStructureBody) {
            runWithTypeHeader(typedRule)
        }
        -"if "
        -IfCondition(typedRule.condition!!)
        -" "
        typedRule.then?.let {
            if (it is KtBlockExpression) {
                -it
            } else {
                -"{ "
                if (typedRule.actuallyCouldBeExpression && it.actuallyCouldBeExpression && typedRule.parent !is KtContainerNodeForControlStructureBody) {
                    -"return "
                }
                -it
                -" }"
            }
        }
        typedRule.`else`?.let {
            -" else "
            if (it is KtBlockExpression) {
                -it
            } else {
                -"{ "
                if (typedRule.actuallyCouldBeExpression && it.actuallyCouldBeExpression && typedRule.parent !is KtContainerNodeForControlStructureBody) {
                    -"return "
                }
                -it
                -" }"
            }
        }
        if (typedRule.actuallyCouldBeExpression && typedRule.parent !is KtContainerNodeForControlStructureBody) {
            -"}"
        }
        endSmartcastBlock()
    }

    handle<KtIfExpression>(
        condition = {
            typedRule.actuallyCouldBeExpression &&
                    typedRule.then.let { it != null && it !is KtBlockExpression && it !is KtStatementExpression } &&
                    typedRule.`else`.let { it != null && it !is KtBlockExpression && it !is KtStatementExpression }
        },
        priority = 1,
        action = {
            -typedRule.condition
            -" ? "
            -typedRule.then
            -" : "
            -typedRule.`else`
        }
    )

//    handle<KtTryExpression>{
//    }
//
//    handle<KtCatchClause>{
//    }

    handle<KtWhenExpression>(
        condition = {
            typedRule.subjectExpression != null && typedRule.entries.flatMap { it.conditions.toList() }
                .all { it is KtWhenConditionWithExpression }
        },
        priority = 100
    ) {
        if (typedRule.actuallyCouldBeExpression) {
            runWithTypeHeader(typedRule)
        }
        val subj = typedRule.subjectExpression
        if (subj is KtProperty) {
            -subj
            -"\n"
        }
        -"switch "
        if (subj is KtProperty) {
            -subj.nameIdentifier
        } else {
            -subj
        }
        -" {\n"
        typedRule.entries.forEach { entry ->
            beginSmartcastBlock()
            entry.elseKeyword?.let {
                -"default:\n"
            } ?: entry.conditions.takeUnless { it.isEmpty() }?.let { cons ->
                -"case "
                cons.toList().forEachBetween(
                    forItem = { -it },
                    between = { -", " }
                )
                -":\n"
            }
            if (entry.expression is KtBlockExpression) {
                val children = entry.expression?.allChildren?.toList()
                    ?.drop(1)
                    ?.dropLast(1)
                    ?.dropWhile { it is PsiWhiteSpace }
                    ?.dropLastWhile { it is PsiWhiteSpace }
                children?.forEachIndexed { index, it ->
                    if (typedRule.actuallyCouldBeExpression) {
                        if (index == children.lastIndex) {
                            -"return "
                        }
                    }
                    -it
                }
            } else {
                if (typedRule.actuallyCouldBeExpression && entry.expression!!.actuallyCouldBeExpression) {
                    -"return "
                }
                -entry.expression
            }
            -"\nbreak\n"
            endSmartcastBlock()
        }
        -"}\n"
        if (typedRule.actuallyCouldBeExpression) {
            -"}"
        }
    }

    handle<KtWhenExpression>(
        condition = { typedRule.subjectExpression == null },
        priority = 100
    ) {
        if (typedRule.actuallyCouldBeExpression) {
            runWithTypeHeader(typedRule)
        }
        -typedRule.entries.forEachBetween(
            forItem = { it ->
                beginSmartcastBlock()
                if (!it.isElse) {
                    -"if "
                    it.conditions.asIterable().forEachBetween(
                        forItem = { c ->
                            if (it.conditions.size == 1) {
                                -IfCondition((c as KtWhenConditionWithExpression).expression!!)
                            } else {
                                -it
                            }
                        },
                        between = {
                            -" || "
                        }
                    )
                }
                if (it.expression is KtBlockExpression) {
                    -it.expression
                } else {
                    -" {\n"
                    if (typedRule.actuallyCouldBeExpression && it.expression!!.actuallyCouldBeExpression) {
                        -"return "
                    }
                    -it.expression
                    -"\n}"
                }
                endSmartcastBlock()
            },
            between = {
                -" else "
            }
        )

        if (typedRule.actuallyCouldBeExpression) {
            -"}"
        }
    }

    handle<KtWhenExpression>(
        condition = { typedRule.subjectExpression != null },
        priority = 10
    ) {
        if (typedRule.actuallyCouldBeExpression) {
            runWithTypeHeader(typedRule)
        }
        val subj = typedRule.subjectExpression
        if (subj is KtProperty) {
            -subj
            -"\n"
        }
        fun subjExpr(): Any? {
            return if (subj is KtProperty) {
                subj.nameIdentifier
            } else {
                subj
            }
        }
        -typedRule.entries.forEachBetween(
            forItem = { it ->
                beginSmartcastBlock()
                if (!it.isElse) {
                    -"if "
                    if (it.conditions.size == 1) {
                        when (val it = it.conditions.first()) {
                            is KtWhenConditionWithExpression -> {
                                -subjExpr()
                                -" == "
                                -it
                            }
                            is KtWhenConditionInRange -> {
                                -it
                                -".contains("
                                -subjExpr()
                                -")"
                            }
                            is KtWhenConditionIsPattern -> {
                                val expr = subjExpr()
                                when {
                                    expr is KtNameReferenceExpression -> {
                                        val vd = expr.resolvedReferenceTarget as? ValueDescriptor
                                        vd?.let { ignoreSmartcast(it) }
                                        -"let "
                                        -expr
                                        -" = "
                                        -expr
                                        -" as? "
                                        -it.typeReference
                                    }
                                    else -> {
                                        -expr
                                        -" is "
                                        -it.typeReference
                                    }
                                }
                            }
                        }
                    } else {
                        it.conditions.asIterable().forEachBetween(
                            forItem = {
                                when (it) {
                                    is KtWhenConditionWithExpression -> {
                                        -subjExpr()
                                        -" == "
                                        -it
                                    }
                                    is KtWhenConditionInRange -> {
                                        -it
                                        -".contains("
                                        -subjExpr()
                                        -")"
                                    }
                                    is KtWhenConditionIsPattern -> {
                                        -subjExpr()
                                        -" is "
                                        -it.typeReference
                                    }
                                }
                            },
                            between = {
                                -" || "
                            }
                        )
                    }
                }
                if (it.expression is KtBlockExpression) {
                    -it.expression
                } else {
                    -" {\n"
                    if (typedRule.actuallyCouldBeExpression && it.expression!!.actuallyCouldBeExpression) {
                        -"return "
                    }
                    -it.expression
                    -"\n}"
                }
                endSmartcastBlock()
            },
            between = {
                -" else "
            }
        )

        if (typedRule.actuallyCouldBeExpression) {
            -"}"
        }
    }

    handle<KtForExpression>(
        condition = { typedRule.loopParameter?.destructuringDeclaration != null },
        priority = 100,
        action = {
            val destructuringDeclaration = typedRule.loopParameter?.destructuringDeclaration!!
            -"for ("
            destructuringDeclaration.entries.forEachBetween(
                forItem = {
                    -(it.name ?: "_")
                },
                between = {
                    -", "
                }
            )
            -") in "
            -typedRule.loopRange
            -" "
            -typedRule.body
        }
    )

    handle<KtForExpression> {
        -"for "
        -typedRule.loopParameter
        -" in ("
        -typedRule.loopRange
        -")"
        -typedRule.body
    }

    handle<KtLabeledExpression> {
        -typedRule.name
        -": "
        -typedRule.baseExpression
    }

    handle<KtBreakExpression> {
        -"break "
        -typedRule.getLabelName()
    }

    handle<KtWhileExpression> {
        -"while "
        -typedRule.condition
        -" "
        -typedRule.body
    }

    handle<KtDoWhileExpression> {
        -"repeat "
        -typedRule.body
        -" while("
        -typedRule.condition
        -")"
    }
}

