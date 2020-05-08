package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.typescript.replacements.TemplatePart
import com.lightningkite.khrysalis.util.forEachBetween
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

fun TypescriptTranslator.registerControl() {

    val dontReturnTypes = setOf("kotlin.Unit", "kotlin.Nothing")
    handle<KtBlockExpression> {
        if (typedRule.resolvedExpressionTypeInfo?.type != null && typedRule.resolvedExpressionTypeInfo?.type?.getJetTypeFqName(
                true
            ) !in dontReturnTypes
        ) {
            val lastStatement = typedRule.statements.lastOrNull()
            typedRule.allChildren.forEach {
                if (it === lastStatement) {
                    -"return "
                }
                -it
                if(it is KtExpression && it !is KtDeclaration && it !is KtLoopExpression && it !is KtIfExpression) {
                    -';'
                }
            }
        } else {
            typedRule.allChildren.forEach {
                -it
                if(it is KtExpression && it !is KtDeclaration && it !is KtLoopExpression && it !is KtIfExpression) {
                    -';'
                }
            }
        }
    }

    handle<KtContainerNodeForControlStructureBody>(
        condition = { (typedRule.parent as? KtExpression)?.resolvedUsedAsExpression == true },
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

        if (typedRule.resolvedUsedAsExpression == true && typedRule.parent !is KtContainerNodeForControlStructureBody) {
            -"(() => {"
        }
        -typedRule.allChildren
        if (typedRule.resolvedUsedAsExpression == true && typedRule.parent !is KtContainerNodeForControlStructureBody) {
            -"})()"
        }
    }

    handle<KtWhenExpression>(
        condition = { typedRule.subjectExpression != null && typedRule.entries.flatMap { it.conditions.toList() }.all { it is KtWhenConditionWithExpression } },
        priority = 100
    ) {
        if (typedRule.resolvedUsedAsExpression == true) {
            -"(() => {"
        }
        val subj = typedRule.subjectExpression
        if(subj is KtProperty){
            -subj
            -";\n"
        }
        -"switch("
        if(subj is KtProperty) {
            -subj.nameIdentifier
        } else {
            -subj
        }
        -"){\n"
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
                    if (typedRule.resolvedUsedAsExpression == true) {
                        if (index == children.lastIndex) {
                            -"return "
                        }
                    }
                    -it
                }
            } else {
                if (typedRule.resolvedUsedAsExpression == true) {
                    -"return "
                }
                -entry.expression
            }
            -"\nbreak;\n"
        }
        -"}\n"
        if (typedRule.resolvedUsedAsExpression == true) {
            -"})()"
        }
    }

    handle<KtWhenExpression>(
        condition = { typedRule.subjectExpression == null },
        priority = 100
    ) {
        if (typedRule.resolvedUsedAsExpression == true) {
            -"(() => {"
        }
        -typedRule.entries.forEachBetween(
            forItem = { it ->
                if(!it.isElse) {
                    -"if("
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
                if(it.expression is KtBlockExpression) {
                    -it.expression
                }else{
                    -"{\n"
                    if (typedRule.resolvedUsedAsExpression == true) {
                        -"return "
                    }
                    -it.expression
                    -"\n}"
                }
            },
            between = {
                -"else "
            }
        )

        if (typedRule.resolvedUsedAsExpression == true) {
            -"})()"
        }
    }

    handle<KtWhenExpression>(
        condition = { typedRule.subjectExpression != null },
        priority = 10
    ) {
        if (typedRule.resolvedUsedAsExpression == true) {
            -"(() => {"
        }
        val subj = typedRule.subjectExpression
        if(subj is KtProperty){
            -subj
            -";\n"
        }
        fun subjExpr(): Any? {
            return if(subj is KtProperty) {
                subj.nameIdentifier
            } else {
                subj
            }
        }
        -typedRule.entries.forEachBetween(
            forItem = { it ->
                if(!it.isElse) {
                    -"if("
                    it.conditions.asIterable().forEachBetween(
                        forItem = {
                            when(it){
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
                if(it.expression is KtBlockExpression) {
                    -it.expression
                }else{
                    -"{\n"
                    if (typedRule.resolvedUsedAsExpression == true) {
                        -"return "
                    }
                    -it.expression
                    -"\n}"
                }
            },
            between = {
                -"else "
            }
        )

        if (typedRule.resolvedUsedAsExpression == true) {
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
                -"const "
                -it.name
                -" = "
                val rule = it.resolvedComponentResolvedCall?.resultingDescriptor?.let { replacements.getCall(it) }
                if(rule != null) {
                    rule.template.forEach { part ->
                        when (part) {
                            is TemplatePart.Import -> out.addImport(part)
                            is TemplatePart.Text -> -part.string
                            TemplatePart.Receiver -> -"toDestructure"
                            TemplatePart.DispatchReceiver -> -"toDestructure"
                            TemplatePart.ExtensionReceiver -> -"toDestructure"
                            TemplatePart.Value -> {
                            }
                            is TemplatePart.Parameter -> {
                            }
                            is TemplatePart.ParameterByIndex -> {
                            }
                            is TemplatePart.TypeParameter -> {
                            }
                            is TemplatePart.TypeParameterByIndex -> {
                            }
                        }
                    }
                } else {
                    -"toDestructure["
                    -index.toString()
                    -']'
                }
                -'\n'
            }
            val body = typedRule.body
            if(body is KtBlockExpression){
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

