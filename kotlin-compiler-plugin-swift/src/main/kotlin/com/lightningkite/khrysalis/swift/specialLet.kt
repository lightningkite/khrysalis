package com.lightningkite.khrysalis.swift

import com.lightningkite.khrysalis.util.forEachBetween
import org.jetbrains.kotlin.psi.*

data class SafeLetChain(
    val outermost: KtExpression,
    val entries: List<Pair<KtExpression, KtLambdaExpression>>,
    val default: KtExpression?
)

fun SwiftTranslator.registerSpecialLet() {

    /**
     * Returns true if we're an entry in a `x?.let {} ?: y?.let{} ?: run {}` chain.
     */
    fun KtExpression.specialLetSituation(): Boolean {
        if (!this.actuallyCouldBeExpression) return true
        (this.parent as? KtBinaryExpression)?.let { p ->
            if (p.isSafeLetChain()) return true
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
                if (p == root) return true
            }
        }
        return false
    }

    handle<SafeLetChain> {
        typedRule.entries.forEachBetween(
            forItem = { (basis, lambda) ->
                -"if let "
                -(lambda.valueParameters.firstOrNull()?.name ?: "it")
                -" = ("
                -basis
                -") { \n"
                -lambda.bodyExpression
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
                -it
                -"\n}"
            }
        }
    }

    handle<SafeLetChain>(
        condition = { typedRule.outermost.actuallyCouldBeExpression },
        priority = 10
    ) {
        typedRule.entries.forEachBetween(
            forItem = { (basis, lambda) ->
                val basisMode = (basis as? KtQualifiedExpression)?.let { getAccessMode(this@registerSpecialLet, it) } ?: AccessMode.PLAIN_DOT
                if(!basisMode.resultAllowsOptionalOp) -"("
                -basis
                if(!basisMode.resultAllowsOptionalOp) -")"
                -".map { ("
                -(lambda.valueParameters.firstOrNull()?.name ?: "it")
                -") in \n"
                -lambda.bodyExpression
                -"\n}"
            },
            between = { -" ?? " }
        )
        typedRule.default?.let {
            -" ?? "
            -it
        }
    }

    handle<KtSafeQualifiedExpression>(
        condition = {
            typedRule.isSafeLetDirect() && !typedRule.actuallyCouldBeExpression
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
}