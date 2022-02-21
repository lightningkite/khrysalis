package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.util.forEachBetween
import com.lightningkite.khrysalis.util.fqNameWithoutTypeArgs
import com.lightningkite.khrysalis.util.satisfies
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import com.lightningkite.khrysalis.analysis.*

fun TypescriptTranslator.registerControl() {

    handle<KtBlockExpression> {
        val lastStatement = typedRule.statements.lastOrNull()
        typedRule.allChildren.filter { it.text != ";" }.forEach {
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
            if (typedRule.expression?.actuallyCouldBeExpression == true) {
                -"return "
            }
            -typedRule.expression
            -"\n}\n"
        }
    }

    handle<KtIfExpression> {
        if (typedRule.actuallyCouldBeExpression && typedRule.parent !is KtContainerNodeForControlStructureBody) {
            -"(()"
            val type = typedRule.resolvedExpressionTypeInfo?.type
            if (type != null) {
                -": "
                -type
            }
            -" => {\n"
        }
        -"if ("
        -typedRule.condition
        -") "
        typedRule.then?.let {
            if (it is KtBlockExpression) {
                -it
            } else {
                -"{ "
                if (typedRule.actuallyCouldBeExpression && it.actuallyCouldBeExpression) {
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
            } else if(it is KtIfExpression) {
                -it
            } else {
                -"{ "
                if (typedRule.actuallyCouldBeExpression && it.actuallyCouldBeExpression) {
                    -"return "
                }
                -it
                -" }"
            }
        }
        if (typedRule.actuallyCouldBeExpression && typedRule.parent !is KtContainerNodeForControlStructureBody) {
            -"\n})()"
        }
    }

    handle<KtIfExpression>(
        condition = {
            typedRule.actuallyCouldBeExpression &&
                    typedRule.then.let {
                        it != null && it !is KtBlockExpression && it !is KtStatementExpression && !it.textContains(
                            '?'
                        )
                    } &&
                    typedRule.`else`.let {
                        it != null && it !is KtBlockExpression && it !is KtStatementExpression && !it.textContains(
                            '?'
                        )
                    }
        },
        priority = 1,
        action = {
            val useParen = typedRule.parent is KtBinaryExpression
            if(useParen){
                -'('
            }
            -typedRule.condition
            -" ? "
            -typedRule.then
            -" : "
            -typedRule.`else`
            if(useParen){
                -')'
            }
        }
    )

    handle<KtTryExpression> {
        val type = typedRule.resolvedExpressionTypeInfo?.type
        val useFunctionWrapper = typedRule.actuallyCouldBeExpression && typedRule.parent !is KtContainerNodeForControlStructureBody && type != null
        val hasFunctionWrapper = typedRule.actuallyCouldBeExpression && type != null
        if (useFunctionWrapper) {
            -"(()"
            if (type != null) {
                -": "
                -type
            }
            -" => {"
            -'\n'
        }

        -typedRule.allChildren.takeWhile { it !is KtCatchClause }
        if (typedRule.catchClauses.size == 1) {
            -typedRule.catchClauses.first()
        } else if (typedRule.catchClauses.isNotEmpty()) {
            -"catch (e) {\n"
            var generalCatch = false
            typedRule.catchClauses.forEachBetween(
                forItem = { catchClause ->
                    catchClause.catchParameter?.let { p ->
                        val t = catchClause.catchParameter!!.typeReference?.resolvedType ?: return@let
                        if(t.fqNameWithoutTypeArgs == "kotlin.Throwable"){
                            generalCatch = true
                            -"{\n"
                        } else {
                            -"if ("
                            emitIsExpression("e", t)
                            -") {\n"
                        }
                    }
                    val b = catchClause.catchBody
                    if (b is KtBlockExpression) {
                        val children = b.allChildren.toList()
                            .drop(1)
                            .dropLast(1)
                            .dropWhile { it is PsiWhiteSpace }
                            .dropLastWhile { it is PsiWhiteSpace }
                        children.forEachIndexed { index, it ->
                            if(index == children.lastIndex){
                                if (hasFunctionWrapper && it is KtExpression && it.actuallyCouldBeExpression) {
                                    -"return "
                                }
                            }
                            -it
                        }
                    } else {
                        if (hasFunctionWrapper && b?.actuallyCouldBeExpression == true) {
                            -"return "
                        }
                        -b
                    }
                },
                between = {
                    -"\n} else "
                }
            )
            if(generalCatch) {
                -"\n}\n}"
            } else {
                -"\n} else throw e;\n}"
            }
        }

        if (typedRule.actuallyCouldBeExpression && typedRule.parent !is KtContainerNodeForControlStructureBody) {
            -"\n})()"
        }
    }

    handle<KtCatchClause> {
        val parentTry = (typedRule.parent as KtTryExpression)
        val type = parentTry.resolvedExpressionTypeInfo?.type
        val hasFunctionWrapper = (typedRule.parent as KtTryExpression).actuallyCouldBeExpression && type != null
        -"catch (_"
        -(typedRule.catchParameter?.nameIdentifier ?: "e")
        -") { let "
        -(typedRule.catchParameter?.nameIdentifier ?: "e")
        -" = _"
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
                if(index == children.lastIndex){
                    if (hasFunctionWrapper && it is KtExpression && it.actuallyCouldBeExpression) {
                        -"return "
                    }
                }
                -it
            }
        } else {
            if (hasFunctionWrapper) {
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
            if (type != null) {
                -": "
                -type
            }
            -" => {\n"
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
            var hasAbsoluteBreak = false
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
                    if (index == children.lastIndex && typedRule.actuallyCouldBeExpression && it is KtExpression && it.actuallyCouldBeExpression) {
                        hasAbsoluteBreak = true
                        -"return "
                    }
                    if (
                        it is KtBreakExpression ||
                        it is KtContinueExpression ||
                        it is KtReturnExpression ||
                        it is KtThrowExpression ||
                        (it is KtCallExpression && it.resolvedReferenceTarget?.fqNameSafe?.asString() == "com.lightningkite.khrysalis.fatalError")
                    ) {
                        hasAbsoluteBreak = true
                    }
                    -it
                }
            } else {
                val it = entry.expression
                if (typedRule.actuallyCouldBeExpression && it is KtExpression && it.actuallyCouldBeExpression) {
                    hasAbsoluteBreak = true
                    -"return "
                }
                if (
                    it is KtBreakExpression ||
                    it is KtContinueExpression ||
                    it is KtReturnExpression ||
                    it is KtThrowExpression ||
                    (it is KtCallExpression && it.resolvedReferenceTarget?.fqNameSafe?.asString() == "com.lightningkite.khrysalis.fatalError")
                ) {
                    hasAbsoluteBreak = true
                }
                -it
            }
            if (!hasAbsoluteBreak) {
                -"\nbreak;"
            }
            -"\n"
        }
        if(typedRule.entries.none { it.isElse } && typedRule.resolvedExhaustiveWhen == true) {
            -"default:\n"
            -"throw new Exception(\"Exhaustive when turned out to not be so exhaustive.\")"
        }
        -"}\n"
        if (typedRule.actuallyCouldBeExpression) {
            -"\n})()"
        }
    }

    handle<KtWhenExpression>(
        condition = { typedRule.subjectExpression == null },
        priority = 100
    ) {
        if (typedRule.actuallyCouldBeExpression) {
            -"(()"
            val type = typedRule.resolvedExpressionTypeInfo?.type
            if (type != null) {
                -": "
                -type
            }
            -" => {\n"
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
                val expr = it.expression
                if (expr is KtBlockExpression) {
                    if (typedRule.actuallyCouldBeExpression && expr.actuallyCouldBeExpression) {
                        -"return "
                    }
                    -expr
                } else {
                    -" {\n"
                    if (typedRule.actuallyCouldBeExpression && expr is KtExpression && expr.actuallyCouldBeExpression) {
                        -"return "
                    }
                    -expr
                    -"\n}"
                }
            },
            between = {
                -" else "
            }
        )

        if (typedRule.actuallyCouldBeExpression) {
            -"\n})()"
        }
    }

    handle<KtWhenExpression>(
        condition = { typedRule.subjectExpression != null },
        priority = 10
    ) {
        if (typedRule.actuallyCouldBeExpression) {
            -"(()"
            val type = typedRule.resolvedExpressionTypeInfo?.type
            if (type != null) {
                -": "
                -type
            }
            -" => {\n"
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
                val expr = it.expression
                if (expr is KtBlockExpression) {
                    if (typedRule.actuallyCouldBeExpression && expr.actuallyCouldBeExpression) {
                        -"return "
                    }
                    -expr
                } else {
                    -" {\n"
                    if (typedRule.actuallyCouldBeExpression && expr is KtExpression && expr.actuallyCouldBeExpression) {
                        -"return "
                    }
                    -expr
                    -"\n}"
                }
            },
            between = {
                -" else "
            }
        )
        if(typedRule.entries.none { it.isElse } && typedRule.resolvedExhaustiveWhen == true) {
            -"else throw new Exception(\"Exhaustive when turned out to not be so exhaustive.\")"
        }

        if (typedRule.actuallyCouldBeExpression) {
            -"\n})()"
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
                val rule = it.resolvedComponentResolvedCall?.let { replacements.getCall(it) }
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

