package com.lightningkite.khrysalis.swift

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClassOrObject


val DeclarationDescriptor.swiftTopLevelMessedUp: Boolean
    get() {
        val containing = (this.containingDeclaration as? ClassDescriptor) ?: return false
        return containing.kind == ClassKind.INTERFACE || containing.declaredTypeParameters.isNotEmpty() || (this as? ClassDescriptor)?.kind == ClassKind.INTERFACE
    }

private val DeclarationDescriptor.swiftNameByAnnotation: String?
    get() = (this.annotations.findAnnotation(FqName("com.lightningkite.butterfly.SwiftName"))?.allValueArguments?.entries?.firstOrNull()?.value?.value as? String)
val DeclarationDescriptor.swiftTopLevelNameRaw: String
    get() = swiftNameByAnnotation ?: (containingDeclaration as? ClassDescriptor)?.let {
        it.swiftTopLevelNameRaw + this.name.asString()
    } ?: this.name.asString()
val DeclarationDescriptor.swiftTopLevelName: String
    get() = swiftNameByAnnotation
        ?: if (swiftTopLevelMessedUp) (containingDeclaration as ClassDescriptor).swiftTopLevelNameRaw + name.asString() else name.asString()
            .safeSwiftIdentifier()
val DeclarationDescriptor.swiftTopLevelReference: String
    get() = swiftNameByAnnotation ?: if (swiftTopLevelMessedUp)
        (containingDeclaration as ClassDescriptor).swiftTopLevelNameRaw + name.asString()
    else if (containingDeclaration is ClassDescriptor)
        (containingDeclaration as ClassDescriptor).swiftTopLevelReference + "." + name.asString().safeSwiftIdentifier()
    else
        name.asString()

fun SwiftTranslator.swiftTopLevelNameElement(forElement: KtClassOrObject): Any? {
    val decl = forElement.resolvedClass ?: return forElement.nameIdentifier
    return decl.swiftNameByAnnotation ?: if (decl.swiftTopLevelMessedUp) {
        listOf((decl.containingDeclaration as ClassDescriptor).swiftTopLevelNameRaw, forElement.nameIdentifier ?: forElement.name)
    } else forElement.nameIdentifier ?: "Companion"
}
