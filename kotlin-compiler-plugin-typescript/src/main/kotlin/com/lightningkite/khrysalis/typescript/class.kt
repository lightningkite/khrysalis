package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.analysis.*
import com.lightningkite.khrysalis.generic.KotlinTranslator
import com.lightningkite.khrysalis.typescript.manifest.declaresPrefix
import com.lightningkite.khrysalis.util.*
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.js.translate.callTranslator.getReturnType
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.*
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter
import org.jetbrains.kotlin.types.isNullable
import org.jetbrains.kotlin.types.typeUtil.supertypes
import java.util.*
import kotlin.collections.ArrayList


fun TypescriptTranslator.registerClass() {

    fun KotlinTranslator<TypescriptFileEmitter>.ContextByType<*>.writeClassHeader(
        on: KtClassOrObject,
        defaultName: String = "Companion"
    ) {
        val typedRule = on
        -"class "
        -(on.nameIdentifier ?: defaultName)
        -typedRule.typeParameterList
        val typeToExtend = typedRule.superTypeListEntries.asSequence()
            .mapNotNull { (it as? KtSuperTypeCallEntry)?.calleeExpression?.typeReference }
            .firstOrNull()
        if (typeToExtend != null) {
            -" extends "
            -typeToExtend
        }
        typedRule.superTypeListEntries.mapNotNull { it as? KtSuperTypeEntry }
            .filter { it.typeReference?.resolvedType?.fqNameWithoutTypeArgs !in skippedExtensions }
            .takeUnless { it.isEmpty() }?.let {
                -" implements "
                it.forEachBetween(
                    forItem = { -it.typeReference },
                    between = { -", " }
                )
            }
    }

    fun KotlinTranslator<TypescriptFileEmitter>.ContextByType<*>.writeInterfaceMarkers(on: KtClassOrObject) {
        val typedRule = on
        typedRule.superTypeListEntries
            .mapNotNull { it as? KtSuperTypeEntry }
            .filter { it.typeReference?.resolvedType?.fqNameWithoutTypeArgs !in skippedExtensions }
            .mapNotNull { it.typeReference?.resolvedType }
            .flatMap { listOf(it) + it.supertypes() }
            .map { it.fqNameWithoutTypeArgs }
            .filter { it != "kotlin.Any" }
            .distinct()
            .takeUnless { it.isEmpty() }
            ?.forEach {
                -"public static implements"
                -it.substringAfterLast('.')
                -" = true;\n"
            }
    }

    fun KotlinTranslator<TypescriptFileEmitter>.ContextByType<*>.writeInterfaceDefaultImplementations(
        on: KtClassOrObject
    ) {
        val resolvedType = when (on) {
            is KtClass -> on.resolvedClass
            is KtObjectDeclaration -> on.resolvedDeclarationToDescriptor as ClassDescriptor
            else -> throw IllegalArgumentException()
        }
        resolvedType
            ?.unsubstitutedMemberScope?.getContributedDescriptors(DescriptorKindFilter.CALLABLES) { true }?.asSequence()
            ?.mapNotNull { it as? MemberDescriptor }
            ?.filter { it.source == SourceElement.NO_SOURCE }
            ?.filter {
                when (it) {
                    is PropertyDescriptor -> it.allOverridden()
                        .all { (it.containingDeclaration as? ClassDescriptor)?.kind == ClassKind.INTERFACE }
                    is FunctionDescriptor -> it.allOverridden()
                        .all { (it.containingDeclaration as? ClassDescriptor)?.kind == ClassKind.INTERFACE }
                    else -> false
                }
            }
            ?.forEach {
                fun writeDefaultObj() {
                    val toOverrideParent = when (it) {
                        is PropertyDescriptor -> it.allOverridden()
                            .filter { (it.containingDeclaration as? ClassDescriptor)?.kind == ClassKind.INTERFACE }
                            .filter { it.overriddenDescriptors.isEmpty() }
                            .first().containingDeclaration as ClassDescriptor
                        is FunctionDescriptor -> it.allOverridden()
                            .filter { (it.containingDeclaration as? ClassDescriptor)?.kind == ClassKind.INTERFACE }
                            .filter { it.overriddenDescriptors.isEmpty() }
                            .first().containingDeclaration as ClassDescriptor
                        else -> return
                    }
                    -out.addImportGetName(toOverrideParent, toOverrideParent.name.asString() + "Defaults")
                    -'.'
                }
                when (it) {
                    is PropertyDescriptor -> {
                        -"public get "
                        -it.name.asString().safeJsIdentifier()
                        it.typeParameters.takeUnless { it.isEmpty() }?.let {
                            -'<'
                            it.forEachBetween(
                                forItem = { -it.name.asString().safeJsIdentifier() /*TODO: Type Argument Limits*/ },
                                between = { -", " }
                            )
                            -'>'
                        }
                        -"(): "
                        -it.type
                        -" { return "
                        writeDefaultObj()
                        -it.allOverridden().first().tsFunctionGetDefaultName
                        -"(this); }\n"

                        if (it.isVar) {
                            -"public set "
                            -it.name.asString().safeJsIdentifier()
                            it.typeParameters.takeUnless { it.isEmpty() }?.let {
                                -'<'
                                it.forEachBetween(
                                    forItem = { -it.name.asString().safeJsIdentifier() /*TODO: Type Argument Limits*/ },
                                    between = { -", " }
                                )
                                -'>'
                            }
                            -"(value: "
                            -it.type
                            -") { "
                            writeDefaultObj()
                            -it.allOverridden().first().tsFunctionGetDefaultName
                            -"(this, value); }\n"
                        }
                    }
                    is FunctionDescriptor -> {
                        -"public "
                        -it.name.asString().safeJsIdentifier()
                        it.typeParameters.takeUnless { it.isEmpty() }?.let {
                            -'<'
                            it.forEachBetween(
                                forItem = { -it.name.asString().safeJsIdentifier() /*TODO: Type Argument Limits*/ },
                                between = { -", " }
                            )
                            -'>'
                        }
                        -"("
                        it.valueParameters.forEachBetween(
                            forItem = {
                                -it.name.asString().safeJsIdentifier()
                                -": "
                                -it.type
                            },
                            between = { -", " }
                        )
                        -"): "
                        -(it.returnType ?: "void")
                        -" { return "
                        writeDefaultObj()
                        -it.allOverridden().first().tsDefaultName
                        -"(this"
                        it.valueParameters.forEach {
                            -", "
                            -it.name.asString().safeJsIdentifier()
                        }
                        -"); }\n"
                    }
                    else -> -"// Insert default for $it\n"
                }
            }
    }

    handle<KtClass>(
        condition = { typedRule.isInterface() },
        priority = 100
    ) {
        -"$declaresPrefix${typedRule.simpleFqName}\n"
        if (!typedRule.isPrivate()) -"export "
        -"interface "
        -typedRule.nameIdentifier
        -typedRule.typeParameterList
        typedRule.superTypeListEntries.mapNotNull { it as? KtSuperTypeEntry }
            .filter { it.typeReference?.resolvedType?.fqNameWithoutTypeArgs !in skippedExtensions }
            .takeUnless { it.isEmpty() }?.let {
                -" extends "
                it.forEachBetween(
                    forItem = { -it.typeReference },
                    between = { -", " }
                )
            }
        -" {\n"
        -typedRule.body
        -"}\n"
        if (typedRule.body?.hasPostActions() == true) {
            if (!typedRule.isPrivate()) -"export "
            -"namespace "
            -typedRule.nameIdentifier
            -"Defaults {"
            typedRule.body?.runPostActions()
            -"\n}"
        }
        typedRule.runPostActions()
    }

    handle<KtClass>(
        condition = { !typedRule.isTopLevel() && typedRule !is KtEnumEntry },
        priority = 10000
    ) {
        noReuse = true
        val parent = (typedRule.parent as KtClassBody).parent as KtClassOrObject
        parent.addPostAction {
            -"\n"
            if (!parent.isPrivate()) {
                -"export "
            }
            -"namespace "
            -parent.nameIdentifier
            -" {\n"
            doSuper()
            -"\n}"
        }
    }

    handle<KtObjectDeclaration>(
        condition = { !typedRule.isTopLevel() },
        priority = 10000
    ) {
        noReuse = true
        val parent = (typedRule.parent as KtClassBody).parent as KtClassOrObject
        parent.addPostAction {
            -"\n"
            if (!parent.isPrivate()) {
                -"export "
            }
            -"namespace "
            -parent.nameIdentifier
            -" {\n"
            doSuper()
            -"\n}"
        }
    }

    handle<KtClass>(
        condition = { typedRule.isInner() },
        priority = 1001
    ) {
        withReceiverScope(
            typedRule.parentOfType<KtClassBody>()!!.parentOfType<KtClass>()!!.resolvedClass!!,
            "parentThis"
        ) {
            doSuper()
        }
    }

    handle<KtClass> {
        -"$declaresPrefix${typedRule.fqName?.asString()}\n"
        if (!typedRule.isPrivate()) -"export "

        if(typedRule.mustBeExtended) -"abstract "
        writeClassHeader(typedRule)
        -" {\n"
        writeInterfaceMarkers(typedRule)
        val parentClassName =
            typedRule.parentOfType<KtClassBody>()?.parentOfType<KtClass>()?.resolvedClass?.name?.asString()
        if (typedRule.isInner()) {
            -"parentThis: "
            -parentClassName
            -";\n"
        }

        if (typedRule.isEnum()) {
            -"private"
        } else if (typedRule.mustBeExtended) {
            -"protected"
        } else {
            -(typedRule.primaryConstructor?.visibilityModifier() ?: "public")
        }
        -" constructor("
        typedRule.primaryConstructor?.let { cons ->
            (if (typedRule.isEnum()) {
                listOf("name: string, jsonName: string") + cons.valueParameters
            } else if (typedRule.isInner()) {
                listOf("parentThis: $parentClassName") + cons.valueParameters
            } else {
                cons.valueParameters
            }).forEachBetween(
                forItem = { -it },
                between = { -", " }
            )
        } ?: run {
            if (typedRule.isEnum()) {
                -"name: string, jsonName: string"
            } else if (typedRule.isInner()) {
                -"parentThis: $parentClassName"
            } else Unit
        }
        -") {\n"
        typedRule.superTypeListEntries.mapNotNull { it as? KtSuperTypeCallEntry }.takeUnless { it.isEmpty() }
            ?.firstOrNull()?.let {
                -"super"
                val c = it.resolvedCall!!
                -ArgumentsList(c.candidateDescriptor as FunctionDescriptor, c, if (typedRule.isEnum()) listOf("name", "jsonName") else listOf(), suppressTypeArgs = true)
                -";\n"
            }
        if (typedRule.isEnum()) {
            -"this.name = name;\n"
            -"this.jsonName = jsonName;\n"
        } else if (typedRule.isInner()) {
            -"this.parentThis = parentThis;\n"
        }
        //Then, in order, variable initializers and anon initializers
        typedRule.body?.children?.forEach {
            when (it) {
                is KtProperty -> {
                    it.initializer?.let { init ->
                        -"this."
                        -it.nameIdentifier
                        -" = "
                        -init
                        -";\n"
                    }
                }
                is KtAnonymousInitializer -> {
                    val b = it.body
                    if (b is KtBlockExpression) {
                        b.statements.forEach {
                            -it
                            -";\n"
                        }
                    } else {
                        -b
                        -";\n"
                    }
                }
            }
        }
        -"}\n"

        if ((typedRule.superTypeListEntries
                .mapNotNull { it as? KtSuperTypeEntry }
                .any { it.typeReference?.resolvedType?.fqNameWithoutTypeArgs == "com.lightningkite.khrysalis.Codable" } ||
            typedRule.annotationEntries.any { it.resolvedAnnotation?.type?.fqNameWithoutTypeArgs == "kotlinx.serialization.Serializable" && it.valueArguments.isEmpty() }
        ) && typedRule.resolvedClass?.isData == false) {
            //Generate codable constructor
            out.addImport("@lightningkite/khrysalis-runtime", "parseObject")

            if (!typedRule.isEnum()) {
                -"public static fromJSON"
                -typedRule.typeParameterList
                -"(obj: any"
                typedRule.typeParameters.forEach {
                    -", "
                    -it.name
                    -": any"
                }
                -"): "
                -typedRule.nameIdentifier
                typedRule.typeParameterList?.parameters?.let {
                    -'<'
                    it.forEachBetween(forItem = { -it.name }, between = { -", " })
                    -'>'
                }
                -" { return new "
                -typedRule.nameIdentifier
                -"(\n"
                typedRule.primaryConstructor?.valueParameters?.forEachBetween(
                    forItem = {
                        if (it.hasValOrVar()) {
                            val type = it.typeReference?.resolvedType ?: run {
                                -"obj[\""
                                -it.jsonName(this@registerClass)
                                -"\"]"
                                return@forEachBetween
                            }

                            if (type.isPrimitive()) {
                                -"obj[\""
                                -it.jsonName(this@registerClass)
                                -"\"]"
                            } else {
                                -"parseObject("
                                -"obj[\""
                                -it.jsonName(this@registerClass)
                                -"\"], "
                                (type.constructor.declarationDescriptor as? TypeParameterDescriptor)?.let {
                                    -it.name.asString()
                                } ?: -CompleteReflectableType(type)
                                -") as "
                                -type
                            }
                        } else {
                            -"undefined"
                        }
                    },
                    between = {
                        -", \n"
                    }
                )
                -"\n) }\n"

                //Generate toJSON()
                -"public toJSON(): object { return {\n"
                typedRule.primaryConstructor?.valueParameters?.filter { it.hasValOrVar() }?.forEachBetween(
                    forItem = {
                        -it.jsonName(this@registerClass)
                        -": this."
                        -it.nameIdentifier
                    },
                    between = {
                        -", \n"
                    }
                )
                -"\n} }\n"
            }
        }

        if (typedRule.isData()) {
            -"public static properties = ["
            typedRule.primaryConstructor?.valueParameters?.filter { it.hasValOrVar() }?.forEachBetween(
                forItem = {
                    -'"'
                    -it.nameIdentifier
                    -'"'
                },
                between = {
                    -", "
                }
            )
            -"]\n"
            typedRule.primaryConstructor?.valueParameters
                ?.filter { it.hasValOrVar() }
                ?.map { it.name?.safeJsIdentifier() to it.jsonName(this@registerClass) }
                ?.filter { it.first != it.second }
                ?.takeUnless { it.isEmpty() }
                ?.let { overrides ->
                    -"public static propertiesJsonOverride = {"
                    overrides.forEachBetween(
                        forItem = {
                            -it.first
                            -": \""
                            -it.second
                            -'"'
                        },
                        between = {
                            -", "
                        }
                    )
                    -"}\n"
                }
            -"public static propertyTypes("
            out.addImport("@lightningkite/khrysalis-runtime", "ReifiedType")
            typedRule.typeParameterList?.parameters?.forEachBetween(
                forItem = {
                    -it.name
                    -": ReifiedType"
                },
                between = { -", " }
            )
            -") { return {"
            typedRule.primaryConstructor?.valueParameters?.filter { it.hasValOrVar() }?.forEachBetween(
                forItem = {
                    -it.nameIdentifier
                    -": "
                    -CompleteReflectableType(it.resolvedPrimaryConstructorParameter!!.type)
                },
                between = {
                    -", "
                }
            )
            -"} }\n"
            -"copy: (values: Partial<"
            -typedRule.nameIdentifier
            typedRule.typeParameterList?.parameters?.let {
                -'<'
                it.forEachBetween(forItem = { -it.name }, between = { -", " })
                -'>'
            }
            -">) => this;\n"
            -"equals: (other: any) => boolean;\n"
            -"hashCode: () => number;\n"
            typedRule.addPostAction {
                out.addImport("@lightningkite/khrysalis-runtime", "setUpDataClass")
                -"\nsetUpDataClass(${typedRule.name})"
            }
        }

        -typedRule.body

        writeInterfaceDefaultImplementations(typedRule)
        if (typedRule.isEnum()) {
            -"private static _values: Array<"
            -typedRule.nameIdentifier
            -"> = ["
            typedRule.body?.enumEntries?.forEachBetween(
                forItem = {
                    -typedRule.nameIdentifier
                    -'.'
                    -it.nameIdentifier
                },
                between = { -", " }
            )
            -"];\n"

            -"public static values(): Array<"
            -typedRule.nameIdentifier
            -"> { return "
            -typedRule.nameIdentifier
            -"._values; }\n"
            -"public readonly name: string;\n"
            -"public readonly jsonName: string;\n"

            -"public static valueOf(name: string): "
            -typedRule.nameIdentifier
            -" { return ("
            -typedRule.nameIdentifier
            -" as any)[name]; }\n"

            -"public toString(): string { return this.name }\n"

            //Generate toJSON()
            -"public toJSON(): string { return this.jsonName }\n"
            -"public static fromJSON(key: string): "
            -typedRule.nameIdentifier
            -" { return "
            -typedRule.nameIdentifier
            -"._values.find(x => x.jsonName.toLowerCase() === key.toLowerCase())! }\n"
        }

        -"}"
        typedRule.runPostActions()
    }

    handle<KtSuperExpression> {
        -"super"
    }

    handle<KtObjectDeclaration> {
        if (!typedRule.isPrivate()) {
            -"$declaresPrefix${typedRule.simpleFqName}\n"
            -"export "
        }
        writeClassHeader(typedRule)
        -" {\n"
        writeInterfaceMarkers(typedRule)
        -"private constructor() {\n"
        typedRule.superTypeListEntries.mapNotNull { it as? KtSuperTypeCallEntry }.takeUnless { it.isEmpty() }
            ?.firstOrNull()?.let {
                -"super"
                val c = it.resolvedCall!!
                -ArgumentsList(c.candidateDescriptor as FunctionDescriptor, c, suppressTypeArgs = true)
                -";\n"
            }
        //Then, in order, variable initializers and anon initializers
        typedRule.body?.children?.forEach {
            when (it) {
                is KtProperty -> {
                    it.initializer?.let { init ->
                        -"this."
                        -it.nameIdentifier
                        -" = "
                        -init
                        -";\n"
                    }
                }
                is KtAnonymousInitializer -> {
                    val b = it.body
                    if (b is KtBlockExpression) {
                        b.statements.forEach {
                            -it
                            -";\n"
                        }
                    } else {
                        -b
                        -";\n"
                    }
                }
            }
        }
        -"}\n"
        -"public static INSTANCE = new "
        -(typedRule.nameIdentifier ?: "Companion")
        -"();\n"
        -typedRule.body
        writeInterfaceDefaultImplementations(typedRule)
        -"}"
        typedRule.runPostActions()
    }

    handle<KtClassBody> {
        typedRule.allChildren.toList().drop(1).dropLast(1).forEach {
            -it
        }
    }

    handle<KtClassInitializer> { /*skip*/ }

    handle<KtObjectLiteralExpression> {
        -"new "
        writeClassHeader(typedRule.objectDeclaration, "Anon")
        -" {\n"
        writeInterfaceMarkers(typedRule.objectDeclaration)
        -"public constructor() {\n"
        typedRule.objectDeclaration.superTypeListEntries.mapNotNull { it as? KtSuperTypeCallEntry }
            .takeUnless { it.isEmpty() }
            ?.firstOrNull()?.let {
                -"super"
                -it.valueArgumentList
                -";\n"
            }
        //Then, in order, variable initializers and anon initializers
        typedRule.objectDeclaration.body?.children?.forEach {
            when (it) {
                is KtProperty -> {
                    it.initializer?.let { init ->
                        -"this."
                        -it.nameIdentifier
                        -" = "
                        -init
                        -";\n"
                    }
                }
                is KtAnonymousInitializer -> {
                    val b = it.body
                    if (b is KtBlockExpression) {
                        b.statements.forEach {
                            -it
                            -";\n"
                        }
                    } else {
                        -b
                        -";\n"
                    }
                }
            }
        }
        -"}\n"
        -typedRule.objectDeclaration.body
        writeInterfaceDefaultImplementations(typedRule.objectDeclaration)
        -"}()"
    }

    handle<KtEnumEntry> {
        -"public static "
        -typedRule.nameIdentifier
        -" = new "
        writeClassHeader(typedRule)
        -typedRule.parentOfType<KtClassBody>()?.parentOfType<KtClass>()?.nameIdentifier
        -" {\n"
        writeInterfaceMarkers(typedRule)
        -"public constructor() {\n"
        -"super"
        -'('
        val args = arrayListOf({ ->
            -'"'
            -typedRule.nameIdentifier
            -"\", \""
            -typedRule.jsonName(this@registerClass)
            -'"'
            Unit
        })
        (typedRule.initializerList?.initializers?.firstOrNull() as? KtSuperTypeCallEntry)?.resolvedCall?.valueArguments?.forEach {
            args.add { -(it.value.arguments.firstOrNull()?.getArgumentExpression() ?: "undefined") }
        }
            ?: (typedRule.initializerList?.initializers?.firstOrNull() as? KtSuperTypeCallEntry)?.valueArguments?.forEach {
                args.add { -it.getArgumentExpression() }
            }
        args.forEachBetween(
            forItem = { it() },
            between = { -", " }
        )
        -");\n"
        //Then, in order, variable initializers and anon initializers
        typedRule.body?.children?.forEach {
            when (it) {
                is KtProperty -> {
                    it.initializer?.let { init ->
                        -"this."
                        -it.nameIdentifier
                        -" = "
                        -init
                        -";\n"
                    }
                }
                is KtAnonymousInitializer -> {
                    val b = it.body
                    if (b is KtBlockExpression) {
                        b.statements.forEach {
                            -it
                            -";\n"
                        }
                    } else {
                        -b
                        -";\n"
                    }
                }
            }
        }
        -"}\n"
        -typedRule.body
        writeInterfaceDefaultImplementations(typedRule)
        -"}();\n"
    }

    handle<KtEnumEntry>(
        condition = { typedRule.body == null || typedRule.body?.declarations?.isEmpty() == true },
        priority = 100
    ) {
        -"public static "
        -typedRule.nameIdentifier
        -" = new "
        -typedRule.parentOfType<KtClassBody>()?.parentOfType<KtClass>()?.nameIdentifier
        -'('
        val args = arrayListOf({ ->
            -'"'
            -typedRule.nameIdentifier
            -"\", \""
            -typedRule.jsonName(this@registerClass)
            -'"'
            Unit
        })
        (typedRule.initializerList?.initializers?.firstOrNull() as? KtSuperTypeCallEntry)?.resolvedCall?.valueArguments?.entries?.sortedBy { it.key.index }
            ?.forEach {
                args.add {
                    -(it.value.arguments.firstOrNull()?.getArgumentExpression() ?: "undefined")
                }
            }
            ?: (typedRule.initializerList?.initializers?.firstOrNull() as? KtSuperTypeCallEntry)?.valueArguments?.forEach {
                args.add { -it.getArgumentExpression() }
            }
        args.forEachBetween(
            forItem = { it() },
            between = { -", " }
        )
        -");\n"
    }

    handle<KtSecondaryConstructor> {
        val resolved = typedRule.resolvedConstructor ?: return@handle
        -(typedRule.visibilityModifier() ?: "public")
        -" static "
        -resolved.tsConstructorName
        -typedRule.containingClass()!!.typeParameterList
        -typedRule.valueParameterList
        -" {\n"
        val parent = typedRule.parentOfType<KtClassBody>()!!.parentOfType<KtClass>()!!.resolvedClass!!
        withReceiverScope(parent, "result") { outName ->
            -"let "
            -outName
            -" = new "
            typedRule.getDelegationCall().let {
                -parent.name.asString()
                -ArgumentsList(
                    on = it.resolvedCall!!.candidateDescriptor as FunctionDescriptor,
                    resolvedCall = it.resolvedCall!!,
                    prependArguments = listOf()
                )
                -";\n"
            }
            val b = typedRule.bodyExpression
            if (b is KtBlockExpression) {
                -b.allChildren.toList().drop(1).dropLast(1)
            } else {
                -b
            }
            -"\nreturn "
            -outName
            -";\n"
        }
        -"}"
    }
}

fun KtParameter.jsonName(translator: TypescriptTranslator): String {
    return annotationEntries
        .mapNotNull { it.resolvedAnnotation }
        .find { it.fqName?.asString()?.let { it.endsWith("JsonProperty") || it.endsWith("SerialName") } == true }
        ?.allValueArguments?.get(Name.identifier("value"))
        ?.value as? String
        ?: name ?: "x"
}

fun KtEnumEntry.jsonName(translator: TypescriptTranslator): String {
    return annotationEntries
        .mapNotNull { it.resolvedAnnotation }
        .find { it.fqName?.asString()?.let { it.endsWith("JsonProperty") || it.endsWith("SerialName") } == true }
        ?.allValueArguments?.get(Name.identifier("value"))
        ?.value as? String
        ?: name ?: "x"
}

private val weakKtClassPostActions = WeakHashMap<KtElement, ArrayList<() -> Unit>>()
fun KtElement.runPostActions() {
    weakKtClassPostActions.remove(this)?.forEach { it() }
}

fun KtElement.hasPostActions(): Boolean {
    return weakKtClassPostActions.get(this)?.isNotEmpty() == true
}

fun KtElement.addPostAction(action: () -> Unit) {
    weakKtClassPostActions.getOrPut(this) { ArrayList() }.add(action)
}

val ConstructorDescriptor.tsConstructorName: String?
    get() = this.annotations
        .find { it.fqName?.asString()?.substringAfterLast('.') == "JsName" }
        ?.allValueArguments
        ?.entries
        ?.firstOrNull()
        ?.value
        ?.value
        ?.toString() ?: ("constructor" + this.valueParameters.joinToString("") {
        it.type.fqNameWithTypeArgs.split('.', ',', '<', '>').map { it.filter { it.isLetterOrDigit() } }
            .filter { it.firstOrNull()?.isLowerCase() == false }.joinToString("")
    })