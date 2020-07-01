package com.lightningkite.khrysalis.swift

import com.lightningkite.khrysalis.util.forEachBetween
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.allChildren

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
            -"return "
            -typedRule.expression
            -"\n}\n"
        }
    }

    handle<KtIfExpression> {
        if (typedRule.actuallyCouldBeExpression && typedRule.parent !is KtContainerNodeForControlStructureBody) {
            -"(() => {"
        }
        -"if "
        -typedRule.condition
        -" "
        typedRule.then?.let {
            if(it is KtBlockExpression){
                -it
            } else {
                -"{ "
                if (typedRule.actuallyCouldBeExpression && typedRule.parent !is KtContainerNodeForControlStructureBody) {
                    -"return "
                }
                -it
                -" }"
            }
        }
        typedRule.`else`?.let {
            -" else "
            if(it is KtBlockExpression){
                -it
            } else {
                -"{ "
                if (typedRule.actuallyCouldBeExpression && typedRule.parent !is KtContainerNodeForControlStructureBody) {
                    -"return "
                }
                -it
                -" }"
            }
        }
        if (typedRule.actuallyCouldBeExpression && typedRule.parent !is KtContainerNodeForControlStructureBody) {
            -"})()"
        }
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
            -"(() => {"
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
                if (typedRule.actuallyCouldBeExpression) {
                    -"return "
                }
                -entry.expression
            }
            -"\nbreak\n"
        }
        -"}\n"
        if (typedRule.actuallyCouldBeExpression) {
            -"})()"
        }
    }

    handle<KtWhenExpression>(
        condition = { typedRule.subjectExpression == null },
        priority = 100
    ) {
        if (typedRule.actuallyCouldBeExpression) {
            -"(() => {"
        }
        -typedRule.entries.forEachBetween(
            forItem = { it ->
                if (!it.isElse) {
                    -"if "
                    it.conditions.asIterable().forEachBetween(
                        forItem = {
                            -it
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
                    if (typedRule.actuallyCouldBeExpression) {
                        -"return "
                    }
                    -it.expression
                    -"\n}"
                }
            },
            between = {
                -" else "
            }
        )

        if (typedRule.actuallyCouldBeExpression) {
            -"})()"
        }
    }

    handle<KtWhenExpression>(
        condition = { typedRule.subjectExpression != null },
        priority = 10
    ) {
        if (typedRule.actuallyCouldBeExpression) {
            -"(() => {"
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
                if (!it.isElse) {
                    -"if "
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
                                    emitIsExpression(
                                        subjExpr(),
                                        it.typeReference!!.resolvedType!!
                                    )
                                }
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
                    if (typedRule.actuallyCouldBeExpression) {
                        -"return "
                    }
                    -it.expression
                    -"\n}"
                }
            },
            between = {
                -" else "
            }
        )

        if (typedRule.actuallyCouldBeExpression) {
            -"})()"
        }
    }

    handle<KtForExpression>(
        condition = { typedRule.loopParameter?.destructuringDeclaration != null },
        priority = 100,
        action = {
            val destructuringDeclaration = typedRule.loopParameter?.destructuringDeclaration!!
            -"for toDestructure of "
            -typedRule.loopRange
            -" {\n"
            destructuringDeclaration.entries.forEachIndexed { index, it ->
                val rule = it.resolvedComponentResolvedCall?.let { replacements.getCall(this@registerControl, it) }
                if (rule != null) {
                    emitTemplate(
                        requiresWrapping = false,
                        prefix = listOf("let ", it.name, " = "),
                        template = rule.template,
                        receiver = "toDestructure"
                    )
                } else {
                    -"let "
                    -it.name
                    -" = "
                    -"toDestructure["
                    -index.toString()
                    -']'
                }
                -'\n'
            }
            val body = typedRule.body
            if (body is KtBlockExpression) {
                -body.allChildren.toList().drop(1).dropLast(1)
            } else {
                -body
            }
            -"\n}"
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
}

