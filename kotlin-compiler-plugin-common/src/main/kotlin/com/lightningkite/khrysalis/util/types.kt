package com.lightningkite.khrysalis.util

import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.supertypes

fun KotlinType.cannotSatisfy(stringRequirement: String): Boolean = !satisfies(stringRequirement)
fun KotlinType.satisfies(stringRequirement: String): Boolean {
    if(this.getJetTypeFqName(true) == stringRequirement) return true
    if(this.getJetTypeFqName(false) == stringRequirement) return true
    this.supertypes().forEach {
        if(it.getJetTypeFqName(true) == stringRequirement) return true
        if(it.getJetTypeFqName(false) == stringRequirement) return true
    }
    return false
}