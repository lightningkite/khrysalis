package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.typescript.replacements.TemplatePart
import com.lightningkite.khrysalis.util.forEachBetween
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

fun TypescriptTranslator.registerControl() {

    handle<KtBlockExpression> {
        val lastStatement = typedRule.statements.lastOrNull()
        typedRule.allChildren.forEach {
            if (it === lastStatement && it.actuallyCouldBeExpression) {
                -"return "
            }
            -it
            if (it is KtExpression && it.needsSemi()) {
                -';'
            }
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
            -"(()"
            val type = typedRule.resolvedExpressionTypeInfo?.type
            if(type != null){
                -": "
                -type
            }
            -" => {"
        }
        -"if ("
        -typedRule.condition
        -") "
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
            -"})()"
        }
    }

    handle<KtIfExpression>(
        condition = {
            typedRule.actuallyCouldBeExpression &&
                    typedRule.then.let { it != null && it !is KtBlockExpression && it !is KtStatementExpression && !it.textContains('?') } &&
                    typedRule.`else`.let { it != null && it !is KtBlockExpression && it !is KtStatementExpression && !it.textContains('?') }
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

    handle<KtTryExpression>{
        if (typedRule.actuallyCouldBeExpression && typedRule.parent !is KtContainerNodeForControlStructureBody) {
            -"(()"
            val type = typedRule.resolvedExpressionTypeInfo?.type
            if(type != null){
                -": "
                -type
            }
            -" => {"
            -'\n'
        }

        -typedRule.allChildren.takeWhile { it !is KtCatchClause }
        if(typedRule.catchClauses.size == 1){
            -typedRule.catchClauses.first()
        } else if(typedRule.catchClauses.isNotEmpty()) {
            -"catch (e) {\n"
            typedRule.catchClauses.forEachBetween(
                forItem = { catchClause ->
                    catchClause.catchParameter?.let { p ->
                        val t = catchClause.catchParameter!!.typeReference?.resolvedType ?: return@let
                        -"if ("
                        emitIsExpression("e", t)
                        -") {\n"
                    }
                    val b = catchClause.catchBody
                    if (b is KtBlockExpression) {
                        val children = b.allChildren.toList()
                            .drop(1)
                            .dropLast(1)
                            .dropWhile { it is PsiWhiteSpace }
                            .dropLastWhile { it is PsiWhiteSpace }
                        children.forEachIndexed { index, it ->
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
                        -b
                    }
                },
                between = {
                    -"\n} else "
                }
            )
            -"\n} else throw e;\n}"
        }

        if (typedRule.actuallyCouldBeExpression && typedRule.parent !is KtContainerNodeForControlStructureBody) {
            -"\n})()"
        }
    }

    handle<KtCatchClause>{
        val expr = (typedRule.parent as KtTryExpression).actuallyCouldBeExpression
        -"catch (_"
        -(typedRule.catchParameter?.nameIdentifier ?: "e")
        -") { let "
        -(typedRule.catchParameter?.nameIdentifier ?: "e")
        - " = _"
        -(typedRule.catchParameter?.nameIdentifier ?: "e")
        -" as "
        -typedRule.catchParameter!!.typeReference
        -"; "
        val b = typedRule.catchBody
        if (b is KtBlockExpression) {
            val children = b.allChildren.toList()
                .drop(1)
                .dropLast(1)
                .dropWhile { it is PsiWhiteSpace }
                .dropLastWhile { it is PsiWhiteSpace }
            children.forEachIndexed { index, it ->
                if (expr) {
                    if (index == children.lastIndex) {
                        -"return "
                    }
                }
                -it
            }
        } else {
            if (expr) {
                -"return "
            }
            -b
        }
        -"}"
    }

    handle<KtWhenExpression>(
        condition = {
            typedRule.subjectExpression != null && typedRule.entries.flatMap { it.conditions.toList() }
                .all { it is KtWhenConditionWithExpression }
        },
        priority = 100
    ) {
        if (typedRule.actuallyCouldBeExpression) {
            -"(()"
            val type = typedRule.resolvedExpressionTypeInfo?.type
            if(type != null){
                -": "
                -type
            }
            -" => {"
        }
        val subj = typedRule.subjectExpression
        if (subj is KtProperty) {
            -subj
            -";\n"
        }
        -"switch("
        if (subj is KtProperty) {
            -subj.nameIdentifier
        } else {
            -subj
        }
        -") {\n"
        typedRule.entries.forEach { entry ->
            entry.conditions.forEach { con ->
                -"case "
                -con
                -":\n"
            }
            entry.elseKeyword?.let {
                -"default:\n"
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
            -"\nbreak;\n"
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
            -"(()"
            val type = typedRule.resolvedExpressionTypeInfo?.type
            if(type != null){
                -": "
                -type
            }
            -" => {"
        }
        -typedRule.entries.forEachBetween(
            forItem = { it ->
                if (!it.isElse) {
                    -"if ("
                    it.conditions.asIterable().forEachBetween(
                        forItem = {
                            -it
                        },
                        between = {
                            -" || "
                        }
                    )
                    -")"
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
            -"(()"
            val type = typedRule.resolvedExpressionTypeInfo?.type
            if(type != null){
                -": "
                -type
            }
            -" => {"
        }
        val subj = typedRule.subjectExpression
        if (subj is KtProperty) {
            -subj
            -";\n"
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
                    -"if ("
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
                    -")"
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
            -"for (const toDestructure of "
            -typedRule.loopRange
            -") "
            -"{\n"
            destructuringDeclaration.entries.forEachIndexed { index, it ->
                val rule = it.resolvedComponentResolvedCall?.let { replacements.getCall(this@registerControl, it) }
                if (rule != null) {
                    emitTemplate(
                        requiresWrapping = false,
                        type = it.resolvedExpressionTypeInfo?.type,
                        prefix = listOf("const ", it.name, " = "),
                        template = rule.template,
                        receiver = "toDestructure"
                    )
                } else {
                    -"const "
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
        -"for (const "
        -typedRule.loopParameter
        -" of "
        -typedRule.loopRange
        -") "
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

