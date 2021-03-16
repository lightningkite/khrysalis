package com.lightningkite.khrysalis.swift

import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.KtSafeQualifiedExpression
import com.lightningkite.khrysalis.analysis.*

data class MultiSelectorExpression(
    val original: KtQualifiedExpression,
    val receiverExpression: KtExpression,
    val selectorExpressions: List<Part>
) : KtExpression by original {
    data class Part(
        val expression: KtExpression,
        val safe: Boolean = false
    )
}

data class InvertedSelectorExpression(
    val original: KtQualifiedExpression,
    val receiverExpression: KtExpression,
    val selectorExpression: KtExpression,
    val safe: Boolean = false
) : KtExpression by original

fun KtQualifiedExpression.multi(): MultiSelectorExpression {
    val selectors = ArrayList<MultiSelectorExpression.Part>()
    var current = this
    while(true){
        selectors.add(MultiSelectorExpression.Part(current.selectorExpression!!, current is KtSafeQualifiedExpression))
        val e = current.receiverExpression
        if(e is KtQualifiedExpression){
            current = e
        } else {
            break
        }
    }
    selectors.reverse()
    return MultiSelectorExpression(this, receiverExpression, selectors)
}

fun KtQualifiedExpression.invert(): InvertedSelectorExpression {
    val selectors = ArrayList<MultiSelectorExpression.Part>()
    var current = this
    var innermostReceiver = this.receiverExpression
    while(true){
        selectors.add(MultiSelectorExpression.Part(current.selectorExpression!!, current is KtSafeQualifiedExpression))
        val e = current.receiverExpression
        if(e is KtQualifiedExpression){
            current = e
        } else {
            innermostReceiver = e
            break
        }
    }
    var currentOut = selectors[0].expression
    for(index in 1..selectors.lastIndex){
        currentOut = InvertedSelectorExpression(
            original = this,
            receiverExpression = selectors[index].expression,
            selectorExpression = currentOut,
            safe = selectors[index-1].safe
        )
    }
    currentOut = InvertedSelectorExpression(
        original = this,
        receiverExpression = innermostReceiver,
        selectorExpression = currentOut,
        safe = selectors.last().safe
    )
    return currentOut
}

fun SwiftTranslator.registerQualified(){
//    handle<KtSafeQualifiedExpression>{
//        -typedRule.invert()
//    }
//    handle<KtDotQualifiedExpression>{
//        -typedRule.invert()
//    }
//    handle<InvertedSelectorExpression> {
//        -typedRule.receiverExpression
//        if(typedRule.safe){
//            -"?."
//        } else {
//            -'.'
//        }
//        -typedRule.selectorExpression
//    }
}