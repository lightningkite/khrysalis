package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.generic.line
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.load.java.lazy.descriptors.isJavaField
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.toVisibility
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifier
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierTypeOrDefault
import org.jetbrains.kotlin.psi.synthetics.SyntheticClassOrObjectDescriptor
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.parents
import org.jetbrains.kotlin.resolve.descriptorUtil.propertyIfAccessor
import java.util.concurrent.atomic.AtomicInteger

val uniqueNumber = AtomicInteger(0)

fun TypescriptTranslator.registerVariable() {

    //Handle special case of completely virtual property
    handle<KtProperty>(
        condition = { typedRule.getter != null && typedRule.setter != null && typedRule.initializer == null },
        priority = 100,
        action = {
            -typedRule.getter!!
            -typedRule.setter
        }
    )

    handle<KtProperty> {
        if (typedRule.isMember) {
            -typedRule.visibilityModifierTypeOrDefault().toVisibility()
            -" "
            if (!typedRule.isVar) {
                -"readonly "
            }
        } else {
            if (typedRule.isVar) {
                -"let "
            } else {
                -"const "
            }
        }
        if (typedRule.getter != null || typedRule.setter != null) {
            -"_"
        }
        -typedRule.nameIdentifier
        typedRule.typeReference?.let {
            -": "
            -it
        }
        typedRule.initializer?.let {
            -" = "
            -it
        }
        -";\n"
        if (typedRule.getter != null || typedRule.setter != null) {
            typedRule.getter?.let {
                -it
            } ?: run {
                if (typedRule.isMember) {
                    -"get "
                    -typedRule.nameIdentifier
                    -"(): "
                    -(typedRule.typeReference ?: typedRule.resolvedVariable?.name) //TODO: Handle unimported type
                    -" { return "
                    -"this._"
                    -typedRule.nameIdentifier
                    -"; }\n"
                } else {
                    -"function get"
                    -typedRule.nameIdentifier?.text?.capitalize()
                    -"(): "
                    -(typedRule.typeReference ?: typedRule.resolvedVariable?.name) //TODO: Handle unimported type
                    -" { return "
                    -"_"
                    -typedRule.nameIdentifier
                    -"; }\n"
                }
            }
            if (typedRule.isVar) {
                typedRule.setter?.let {
                    -it
                } ?: run {
                    if (typedRule.isMember) {
                        -"set "
                        -typedRule.nameIdentifier
                        -"(value: "
                        -(typedRule.typeReference
                            ?: typedRule.resolvedVariable?.name) //TODO: Handle unimported type
                        -") { this._"
                        -typedRule.nameIdentifier
                        -" = value; }\n"
                    } else {
                        -"function set"
                        -typedRule.nameIdentifier?.text?.capitalize()
                        -"(value: "
                        -(typedRule.typeReference
                            ?: typedRule.resolvedVariable?.name) //TODO: Handle unimported type
                        -") { _"
                        -typedRule.nameIdentifier
                        -" = value; }\n"
                    }
                }
            }
        }
    }

    handle<KtPropertyAccessor>(
        condition = { typedRule.isGetter && typedRule.property.isMember },
        priority = 4,
        action = {
            -typedRule.visibilityModifierTypeOrDefault().toVisibility()
            -" get "
            -typedRule.property.nameIdentifier
            -"(): "
            -(typedRule.property.typeReference
                ?: typedRule.property.resolvedVariable?.name) //TODO: Handle unimported type
            -" "
            typedRule.bodyExpression?.let {
                -"{ return "
                -it
                -"; }"
            }
            -typedRule.bodyBlockExpression
            -"\n"
        }
    )
    handle<KtPropertyAccessor>(
        condition = { typedRule.isSetter && typedRule.property.isMember },
        priority = 3,
        action = {
            -typedRule.visibilityModifierTypeOrDefault().toVisibility()
            -" set "
            -typedRule.property.nameIdentifier
            -"("
            -(typedRule.parameter?.nameIdentifier ?: -"value")
            -": "
            -(typedRule.property.typeReference
                ?: typedRule.property.resolvedVariable?.name) //TODO: Handle unimported type
            -") "
            -typedRule.bodyBlockExpression
            -"\n"
        }
    )
    handle<KtPropertyAccessor>(
        condition = { typedRule.isGetter },
        priority = 1,
        action = {
            -"function get"
            -typedRule.property.nameIdentifier!!.text.capitalize()
            -"(): "
            -(typedRule.property.typeReference
                ?: typedRule.property.resolvedVariable?.name) //TODO: Handle unimported type
            -" "
            typedRule.bodyExpression?.let {
                -"{ return "
                -it
                -"; }"
            }
            -typedRule.bodyBlockExpression
            -"\n"
        }
    )
    handle<KtPropertyAccessor>(
        condition = { typedRule.isSetter },
        priority = 2,
        action = {
            -"function set"
            -typedRule.property.nameIdentifier!!.text.capitalize()
            -"("
            -(typedRule.parameter?.nameIdentifier ?: -"value")
            -": "
            -(typedRule.property.typeReference
                ?: typedRule.property.resolvedVariable?.name) //TODO: Handle unimported type
            -") "
            -typedRule.bodyBlockExpression
            -"\n"
        }
    )
    handle<KtNameReferenceExpression>(
        condition = { typedRule.text == "field" && typedRule.parentOfType<KtPropertyAccessor>() != null },
        priority = 1000,
        action = {
            val prop = typedRule.parentOfType<KtPropertyAccessor>()!!
            if(prop.property.isMember){
                -"this."
            }
            -"_"
            -typedRule.parentOfType<KtPropertyAccessor>()!!.property.nameIdentifier
        }
    )

    //handle virtual property access
    handle<KtNameReferenceExpression>(
        condition = {
            val resolved = typedRule.resolvedReferenceTarget as? PropertyDescriptor ?: return@handle false
            val containing = resolved.containingDeclaration
            if (containing is ClassDescriptor || containing is SyntheticClassOrObjectDescriptor) return@handle false
            return@handle !resolved.accessors.all { it.isDefault }
        },
        priority = 100,
        action = {
            -"get"
            -typedRule.text.capitalize()
            -"()"
        }
    )
    handle<KtBinaryExpression>(
        condition = {
            val left = typedRule.left as? KtNameReferenceExpression ?: return@handle false
            val resolved = left.resolvedReferenceTarget as? PropertyDescriptor ?: return@handle false
            val containing = resolved.containingDeclaration
            if (containing is ClassDescriptor || containing is SyntheticClassOrObjectDescriptor) return@handle false
            return@handle !resolved.accessors.all { it.isDefault }
        },
        priority = 100,
        action = {
            -"set"
            -typedRule.left!!.text.capitalize()
            -"("
            -typedRule.right
            -")"
        }
    )

    //Prepend 'this'
    handle<KtNameReferenceExpression>(
        condition = {
            if (typedRule.parent is KtDotQualifiedExpression) return@handle false
            val resolvedTarget = typedRule.resolvedReferenceTarget ?: return@handle false
            val containing = resolvedTarget.containingDeclaration ?: return@handle false
            return@handle containing is ClassDescriptor || containing is SyntheticClassOrObjectDescriptor
        },
        priority = 99,
        action = {
            -"this."
            doSuper()
        }
    )
}

inline fun <reified T : PsiElement> PsiElement.parentOfType(): T? = parentOfType(T::class.java)
fun <T : PsiElement> PsiElement.parentOfType(type: Class<T>): T? =
    if (type.isInstance(this.parent)) type.cast(this.parent) else this.parent?.parentOfType(type)