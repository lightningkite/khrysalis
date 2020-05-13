package com.lightningkite.khrysalis.typescript

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.psi.KtClassOrObject


val skippedExtensions = setOf(
    "com.lightningkite.khrysalis.AnyObject",
    "com.lightningkite.khrysalis.AnyHashable",
    "com.lightningkite.khrysalis.Hashable",
    "com.lightningkite.khrysalis.Equatable"
//    "com.lightningkite.khrysalis.SomeEnum"
)

fun MemberDescriptor.description(): String {
    return when (this) {
        is FunctionDescriptor -> this.name.identifier + this.valueParameters.joinToString {
            it.type.getJetTypeFqName(
                true
            )
        }
        is PropertyDescriptor -> this.name.identifier
        else -> return "???"
    }
}

val DeclarationDescriptor.tsTopLevelMessedUp: Boolean get() {
    val containing = (this.containingDeclaration as? ClassDescriptor) ?: return false
    return containing.kind == ClassKind.INTERFACE || (this as? ClassDescriptor)?.kind == ClassKind.INTERFACE
}

val DeclarationDescriptor.tsTopLevelNameRaw: String get() = (containingDeclaration as? ClassDescriptor)?.let {
    it.tsTopLevelNameRaw + this.name.asString()
} ?: this.name.asString()
val DeclarationDescriptor.tsTopLevelName: String get() = if(tsTopLevelMessedUp) (containingDeclaration as ClassDescriptor).tsTopLevelNameRaw + name.asString() else name.asString()
val DeclarationDescriptor.tsTopLevelReference: String get() = if(tsTopLevelMessedUp)
    (containingDeclaration as ClassDescriptor).tsTopLevelNameRaw + name.asString()
else if(containingDeclaration is ClassDescriptor)
    (containingDeclaration as ClassDescriptor).tsTopLevelReference + "/**/." + name.asString()
else
    name.asString()

fun TypescriptTranslator.tsTopLevelNameElement(forElement: KtClassOrObject): Any? {
    val decl = forElement.resolvedClass ?: return forElement.nameIdentifier
    return if(decl.tsTopLevelMessedUp){
        listOf((decl.containingDeclaration as ClassDescriptor).tsTopLevelNameRaw, forElement.nameIdentifier)
    } else forElement.nameIdentifier
}