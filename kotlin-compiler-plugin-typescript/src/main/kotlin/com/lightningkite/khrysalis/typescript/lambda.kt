package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.generic.line
import com.lightningkite.khrysalis.util.forEachBetween
import com.lightningkite.khrysalis.util.simpleFqName
import org.jetbrains.kotlin.builtins.functions.FunctionInvokeDescriptor
import org.jetbrains.kotlin.builtins.getReceiverTypeFromFunctionType
import org.jetbrains.kotlin.builtins.isFunctionType
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.FlexibleType
import org.jetbrains.kotlin.types.SimpleType
import org.jetbrains.kotlin.types.WrappedType

fun TypescriptTranslator.registerLambda() {
    handle<KtFunctionLiteral>(
        condition = { typedRule.resolvedFunction?.extensionReceiverParameter != null },
        priority = 100,
        action = {
            val resolved = typedRule.resolvedFunction!!
            withReceiverScope(resolved, "this_") { name ->
                -typedRule.typeParameterList
                -'('
                -name
                typedRule.valueParameters.takeUnless { it.isEmpty() }?.forEach {
                    -", "
                    -it
                } ?: run {
                    if (resolved.valueParameters.size == 1) {
                        -", it"
                    }
                }
                -") => "
                when (typedRule.bodyExpression?.statements?.size) {
                    null, 0 -> -"{}"
                    1 -> {
                        val s = typedRule.bodyExpression?.firstStatement
                        if(s!!.actuallyCouldBeExpression){
                            -s
                        } else {
                            -"{\n"
                            -s
                            -"\n}"
                        }
                    }
                    else -> {
                        -"{\n"
                        -typedRule.bodyExpression
                        -"\n}"
                    }
                }
            }
        }
    )
    handle<KtFunctionLiteral> {
        val resolved = typedRule.resolvedFunction
        -typedRule.typeParameterList
        typedRule.valueParameterList?.let {
            -'('
            -it
            -')'
        } ?: run {
            if (resolved?.valueParameters?.size == 1) {
                -"(it)"
            } else {
                -"()"
            }
        }
        -" => "
        when (typedRule.bodyExpression?.statements?.size) {
            null, 0 -> -"{}"
            1 -> {
                val s = typedRule.bodyExpression?.firstStatement
                if(s!!.actuallyCouldBeExpression){
                    -s
                } else {
                    -"{\n"
                    -s
                    -"\n}"
                }
            }
            else -> {
                -"{\n"
                -typedRule.bodyExpression
                -"\n}"
            }
        }
    }

    handle<KtLabeledExpression>(
        condition = { typedRule.baseExpression is KtLambdaExpression },
        priority = 100
    ) {
        -typedRule.baseExpression
    }

    handle<KtReturnExpression> {
        -"return"
        typedRule.returnedExpression?.let {
            -" "
            -it
        }
    }



//    handle<KtDotQualifiedExpression>(
//        condition = {
//            val result = ((typedRule.selectorExpression as? KtCallExpression)?.resolvedCall?.candidateDescriptor is FunctionInvokeDescriptor)
//            println("KDQE ${typedRule.text} = " + (typedRule.selectorExpression as? KtCallExpression)?.resolvedCall?.candidateDescriptor + " therefore " + result)
//            result
//        },
//        priority = 30_001,
//        action = {
//            println("WTFFFF therefore true")
//            -"/*TRIGGERED LAMBDA CALL DQ*/"
//            val callExp = typedRule.selectorExpression as KtCallExpression
//            val nre = callExp.calleeExpression as KtNameReferenceExpression
//            val type = nre.resolvedExpressionTypeInfo!!.type!!
//            val receiverType = type.getReceiverTypeFromFunctionType()
//
//            if (receiverType == null) {
//                -typedRule.receiverExpression
//                -"."
//            }
//            -nre
//            -'('
//            var first = false
//            fun comma() {
//                if (first) {
//                    first = false; return
//                }
//                -", "
//            }
//            if (receiverType != null) {
//                comma()
//                -typedRule.receiverExpression
//            }
//            for (arg in callExp.valueArguments) {
//                comma()
//                -arg.getArgumentExpression()
//            }
//            callExp.lambdaArguments.forEach {
//                comma()
//                -it.getArgumentExpression()
//            }
//            -')'
//        }
//    )
//
//    handle<KtSafeQualifiedExpression>(
//        condition = {
//            (typedRule.selectorExpression as? KtCallExpression)?.resolvedCall?.candidateDescriptor is FunctionInvokeDescriptor
//        },
//        priority = 30_002,
//        action = {
//            -"/*TRIGGERED LAMBDA CALL*/"
//            val callExp = typedRule.selectorExpression as KtCallExpression
//            val nre = callExp.calleeExpression as KtNameReferenceExpression
//            val type = nre.resolvedExpressionTypeInfo!!.type!!
//            val receiverType = type.getReceiverTypeFromFunctionType()
//
//            -"((_it)=>{\n"
//            -"if(_it === null) return null;\nreturn "
//            if (receiverType == null) {
//                -"_it"
//                -"."
//            }
//            -nre
//            -'('
//            var first = false
//            fun comma() {
//                if (first) {
//                    first = false; return
//                }
//                -", "
//            }
//            if (receiverType != null) {
//                comma()
//                -"_it"
//            }
//            for (arg in callExp.valueArguments) {
//                comma()
//                -arg.getArgumentExpression()
//            }
//            callExp.lambdaArguments.forEach {
//                comma()
//                -it.getArgumentExpression()
//            }
//            -')'
//            -"\n})("
//            -typedRule.receiverExpression
//            -')'
//        }
//    )
//
//    handle<KtSafeQualifiedExpression>(
//        condition = {
//            (typedRule.selectorExpression as? KtCallExpression)?.resolvedCall?.candidateDescriptor is FunctionInvokeDescriptor && typedRule.actuallyCouldBeExpression
//        },
//        priority = 30_003,
//        action = {
//            -"/*TRIGGERED LAMBDA CALL*/"
//            val callExp = typedRule.selectorExpression as KtCallExpression
//            val nre = callExp.calleeExpression as KtNameReferenceExpression
//            val type = nre.resolvedExpressionTypeInfo!!.type!!
//            val receiverType = type.getReceiverTypeFromFunctionType()
//
//            val tempName = "temp${uniqueNumber.getAndIncrement()}"
//            -"const $tempName = "
//            -typedRule.receiverExpression
//            -";\nif($tempName !== null) "
//            if (receiverType == null) {
//                -tempName
//                -"."
//            }
//            -nre
//            -'('
//            var first = false
//            fun comma() {
//                if (first) {
//                    first = false; return
//                }
//                -", "
//            }
//            if (receiverType != null) {
//                comma()
//                -tempName
//            }
//            for (arg in callExp.valueArguments) {
//                comma()
//                -arg.getArgumentExpression()
//            }
//            callExp.lambdaArguments.forEach {
//                comma()
//                -it.getArgumentExpression()
//            }
//            -')'
//        }
//    )
//
//    handle<KtCallExpression>(
//        condition = {
//            typedRule.resolvedCall?.candidateDescriptor is FunctionInvokeDescriptor
//        },
//        priority = 30_000
//    ) {
//        -"/*TRIGGERED LAMBDA CALL*/"
//        val nre = typedRule.calleeExpression as KtNameReferenceExpression
//        -nre
//        -'('
//        var first = false
//        fun comma() {
//            if (first) {
//                first = false; return
//            }
//            -", "
//        }
//        for (arg in typedRule.valueArguments) {
//            comma()
//            -arg.getArgumentExpression()
//        }
//        typedRule.lambdaArguments.forEach {
//            comma()
//            -it.getArgumentExpression()
//        }
//        -')'
//    }
}
