package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.util.forEachBetween
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

data class SafeLetChain(
    val outermost: KtExpression,
    val entries: List<Pair<KtExpression, KtLambdaExpression>>,
    val default: KtExpression?
)

fun TypescriptTranslator.registerSpecialLet() {

    /**
     * Returns true if we're an entry in a `x?.let {} ?: y?.let{} ?: run {}` chain.
     */
    fun KtExpression.specialLetSituation(): Boolean {
        if (!this.actuallyCouldBeExpression) return true
        (this.parent as? KtBinaryExpression)?.let { p ->
            if (p.isSafeLetChain() && !p.safeLetChainRoot().actuallyCouldBeExpression) return true
        }
        return false
    }

    /**
     * Returns true if we're at the end of a `x?.let {} ?: y?.let{} ?: run {}` chain.
     */
    fun KtExpression.specialLetSituationEnd(): Boolean {
        if (!this.actuallyCouldBeExpression) return true
        (this.parent as? KtBinaryExpression)?.let { p ->
            if (p.right == this && p.isSafeLetChain()) {
                val root = p.safeLetChainRoot()
                if (p == root && !p.actuallyCouldBeExpression) return true
            }
        }
        return false
    }

    handle<SafeLetChain> {
        if(typedRule.outermost.actuallyCouldBeExpression){
            -"(()=>{\n"
        }
        val temp = "temp_${uniqueNumber.getAndIncrement()}"
        -"let $temp;\n"
        typedRule.entries.forEachBetween(
            forItem = { (basis, lambda) ->
                -"if (($temp = "
                -basis
                -") !== null) { \n"
                withName(lambda.valueParameters.firstOrNull()?.name ?: "it", temp) {
                    -lambda.bodyExpression
                }
                -"\n}"
            },
            between = { -" else " }
        )
        typedRule.default?.let {
            if (it.isRunDirect()) {
                -" else {\n"
                -(it as KtCallExpression).lambdaArguments.first().getLambdaExpression()!!.bodyExpression
                -"\n}"
            } else {
                -" else {\n"
                if(typedRule.outermost.actuallyCouldBeExpression){
                    -"return "
                }
                -it
                -"\n}"
            }
        } ?: run {
            if(typedRule.outermost.actuallyCouldBeExpression){
                -" else { return null }"
            }
        }
        if(typedRule.outermost.actuallyCouldBeExpression){
            -"\n})()"
        }
    }

    handle<KtSafeQualifiedExpression>(
        condition = {
            typedRule.isSafeLetDirect()
        },
        priority = 20_000
    ) {
        -SafeLetChain(
            outermost = typedRule,
            entries = listOf(
                typedRule.receiverExpression to (typedRule.selectorExpression as KtCallExpression).lambdaArguments.first()
                    .getLambdaExpression()!!
            ),
            default = null
        )
    }

    handle<KtBinaryExpression>(
        condition = {
            typedRule.isSafeLetChain()
        },
        priority = 20_000
    ) {
        val entries = ArrayList<Pair<KtExpression, KtLambdaExpression>>()
        val default: KtExpression? = typedRule.right

        var current = typedRule
        outer@ while (true) {
            (current.right as? KtSafeQualifiedExpression)?.let { r ->
                entries.add(
                    r.receiverExpression to (r.selectorExpression as KtCallExpression).lambdaArguments.first()
                        .getLambdaExpression()!!
                )
            }
            when (val l = current.left) {
                is KtSafeQualifiedExpression -> {
                    entries.add(
                        l.receiverExpression to (l.selectorExpression as KtCallExpression).lambdaArguments.first()
                            .getLambdaExpression()!!
                    )
                    break@outer
                }
                is KtBinaryExpression -> {
                    current = l
                }
                else -> throw IllegalStateException()
            }
        }

        -SafeLetChain(
            outermost = typedRule,
            entries = entries.reversed(),
            default = default
        )
    }

//    handle<KtSafeQualifiedExpression>(
//        condition = {
//            typedRule.isSafeLetDirect() && typedRule.specialLetSituation()
//        },
//        priority = 20_000
//    ) {
//        val callExpression = typedRule.selectorExpression as KtCallExpression
//        if(typedRule.receiverExpression.isSimple()) {
//            -"if("
//            -typedRule.receiverExpression
//            -" !== null) {\nconst "
//            val lambda = callExpression.lambdaArguments.first().getLambdaExpression()!!
//            -(lambda.valueParameters.firstOrNull()?.nameIdentifier ?: "it")
//            -" = "
//            -typedRule.receiverExpression
//            -";\n"
//            -"\n}"
//        } else {
//            val temp = "temp_${uniqueNumber.getAndIncrement()}"
//            -"const $temp = "
//            -typedRule.receiverExpression
//            -";\nif($temp !== null) {\nconst "
//            val lambda = callExpression.lambdaArguments.first().getLambdaExpression()!!
//            -(lambda.valueParameters.firstOrNull()?.nameIdentifier ?: "it")
//            -" = $temp;\n"
//            -lambda.bodyExpression
//            -"\n}"
//        }
//    }
//    handle<KtCallExpression>(
//        condition = {
//            if(!typedRule.specialLetSituationEnd()) return@handle false
//            typedRule.isRunDirect()
//        },
//        priority = 20_000
//    ) {
//        -typedRule.lambdaArguments.first().getLambdaExpression()!!.bodyExpression
//    }
//    handle<KtBinaryExpression>(
//        condition = {
//            typedRule.isSafeLetChain()
//        },
//        priority = 20_000
//    ) {
//        -typedRule.left
//        -" else {\n"
//        -typedRule.right
//        -"\n}"
//    }
}