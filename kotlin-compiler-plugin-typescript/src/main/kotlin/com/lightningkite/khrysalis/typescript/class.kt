package com.lightningkite.khrysalis.typescript

import com.lightningkite.khrysalis.generic.PartialTranslatorByType
import com.lightningkite.khrysalis.typescript.manifest.declaresPrefix
import com.lightningkite.khrysalis.util.allOverridden
import com.lightningkite.khrysalis.util.forEachBetween
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.js.translate.callTranslator.getReturnType
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.*
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter
import org.jetbrains.kotlin.types.typeUtil.supertypes
import java.util.*
import kotlin.collections.ArrayList


fun TypescriptTranslator.registerClass() {

    fun PartialTranslatorByType<TypescriptFileEmitter, Unit, Any>.ContextByType<*>.writeClassHeader(
        on: KtClassOrObject,
        defaultName: String = "Companion"
    ) {
        val typedRule = on
        -"class "
        -(on.nameIdentifier ?: defaultName)
        -typedRule.typeParameterList
        typedRule.superTypeListEntries.mapNotNull { it as? KtSuperTypeCallEntry }.takeUnless { it.isEmpty() }?.let {
            -" extends "
            it.forEachBetween(
                forItem = { -it.calleeExpression.typeReference },
                between = { -", " }
            )
        }
        typedRule.superTypeListEntries.mapNotNull { it as? KtSuperTypeEntry }
            .filter { it.typeReference?.resolvedType?.getJetTypeFqName(false) !in skippedExtensions }
            .takeUnless { it.isEmpty() }?.let {
                -" implements "
                it.forEachBetween(
                    forItem = { -it.typeReference },
                    between = { -", " }
                )
            }
    }

    fun PartialTranslatorByType<TypescriptFileEmitter, Unit, Any>.ContextByType<*>.writeInterfaceMarkers(on: KtClassOrObject) {
        val typedRule = on
        typedRule.superTypeListEntries
            .mapNotNull { it as? KtSuperTypeEntry }
            .filter { it.typeReference?.resolvedType?.getJetTypeFqName(false) !in skippedExtensions }
            .mapNotNull { it.typeReference?.resolvedType }
            .flatMap { listOf(it) + it.supertypes() }
            .map { it.getJetTypeFqName(false) }
            .filter { it != "kotlin.Any" }
            .distinct()
            .takeUnless { it.isEmpty() }
            ?.forEach {
                -"public static implementsInterface"
                -it.split('.').joinToString("") { it.capitalize() }
                -" = true;\n"
            }
    }

    fun PartialTranslatorByType<TypescriptFileEmitter, Unit, Any>.ContextByType<*>.writeInterfaceDefaultImplementations(
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
                        is PropertyDescriptor -> it.allOverridden().first().containingDeclaration as ClassDescriptor
                        is FunctionDescriptor -> it.allOverridden().first().containingDeclaration as ClassDescriptor
                        else -> return
                    }
                    -BasicType(toOverrideParent.defaultType)
                    -"Defaults."
                    out.addImport(toOverrideParent, toOverrideParent.name.asString() + "Defaults")
                }
                when (it) {
                    is PropertyDescriptor -> {
                        -"public get "
                        -it.name.asString()
                        it.typeParameters.takeUnless { it.isEmpty() }?.let {
                            -'<'
                            it.forEachBetween(
                                forItem = { -it.name.asString() /*TODO: Type Argument Limits*/ },
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
                            -it.name.asString()
                            it.typeParameters.takeUnless { it.isEmpty() }?.let {
                                -'<'
                                it.forEachBetween(
                                    forItem = { -it.name.asString() /*TODO: Type Argument Limits*/ },
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
                        -it.name.asString()
                        it.typeParameters.takeUnless { it.isEmpty() }?.let {
                            -'<'
                            it.forEachBetween(
                                forItem = { -it.name.asString() /*TODO: Type Argument Limits*/ },
                                between = { -", " }
                            )
                            -'>'
                        }
                        -"("
                        it.valueParameters.forEachBetween(
                            forItem = {
                                -it.name.asString()
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
                            -it.name.asString()
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
        -" {\n"
        -typedRule.body
        -"}\n"
        if(typedRule.body?.hasPostActions() == true) {
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
        condition = { !typedRule.isTopLevel() },
        priority = 10000
    ) {
        noReuse = true
        val parent = (typedRule.parent as KtClassBody).parent as KtClassOrObject
        parent.addPostAction {
            -"\n"
            if(!parent.isPrivate()){
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
            if(!parent.isPrivate()){
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


        when (typedRule.modalityModifierType()) {
            KtTokens.ABSTRACT_KEYWORD -> -"abstract "
        }
        writeClassHeader(typedRule)
        -" {\n"
        writeInterfaceMarkers(typedRule)
        typedRule.primaryConstructor?.valueParameters?.filter { it.hasValOrVar() }?.forEach {

            -(it.visibilityModifier() ?: "public")
            -" "
            if ((it.valOrVarKeyword as? LeafPsiElement)?.elementType == KtTokens.VAL_KEYWORD) {
                -"readonly "
            }
            -it.nameIdentifier
            it.typeReference?.let {
                -": "
                -it
            }
            -";\n"
        }
        val parentClassName =
            typedRule.parentOfType<KtClassBody>()?.parentOfType<KtClass>()?.resolvedClass?.name?.asString()
        if (typedRule.isInner()) {
            -"parentThis: "
            -parentClassName
            -";\n"
        }

        if (typedRule.isEnum()) {
            -"private"
        } else if (typedRule.hasModifier(KtTokens.ABSTRACT_KEYWORD)) {
            -"protected"
        } else {
            -(typedRule.primaryConstructor?.visibilityModifier() ?: "public")
        }
        -" constructor("
        typedRule.primaryConstructor?.let { cons ->
            (if (typedRule.isEnum()) {
                listOf("name: string") + cons.valueParameters
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
                -"name: string"
            } else if (typedRule.isInner()) {
                -"parentThis: $parentClassName"
            } else Unit
        }
        -") {\n"
        typedRule.superTypeListEntries.mapNotNull { it as? KtSuperTypeCallEntry }.takeUnless { it.isEmpty() }
            ?.firstOrNull()?.let {
                -"super("
                if (typedRule.isEnum()) {
                    listOf("name: string") + it.valueArguments
                } else {
                    it.valueArguments
                }.forEachBetween(
                    forItem = { -it },
                    between = { -", " }
                )
                -");\n"
            }
        if (typedRule.isEnum()) {
            -"this.name = name;\n"
        } else if (typedRule.isInner()) {
            -"this.parentThis = parentThis;\n"
        }
        //Parameter assignment first
        typedRule.primaryConstructor?.let { cons ->
            cons.valueParameters.asSequence().filter { it.hasValOrVar() }.forEach {
                -"this."
                -it.nameIdentifier
                -" = "
                -it.nameIdentifier
                -";\n"
            }
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

        if (typedRule.superTypeListEntries
                .mapNotNull { it as? KtSuperTypeEntry }
                .any { it.typeReference?.resolvedType?.getJetTypeFqName(false) == "com.lightningkite.khrysalis.Codable" }
        ) {
            //Generate codable constructor
            out.addImport("khrysalis/dist/net/jsonParsing", "parse", "parseJsonTyped")
            -"public static fromJson"
            -typedRule.typeParameterList
            -"(obj: any"
            typedRule.typeParameters.forEach {
                -", "
                -it.name
                -": any"
            }
            -"): "
            -typedRule.nameIdentifier
            -typedRule.typeParameterList
            -" { return new "
            -typedRule.nameIdentifier
            -"(\n"
            //TODO: Use annotation for json
            typedRule.primaryConstructor?.valueParameters?.forEachBetween(
                forItem = {
                    if (it.hasValOrVar()) {
                        val type = it.typeReference?.resolvedType ?: run {
                            -"obj[\""
                            -it.jsonName
                            -"\"]"
                            return@forEachBetween
                        }

                        -"parseJsonTyped("
                        -"obj[\""
                        -it.jsonName
                        -"\"], "
                        (type.constructor.declarationDescriptor as? TypeParameterDescriptor)?.let {
                            -it.name.asString()
                        } ?: -CompleteReflectableType(type)
                        -") as "
                        -type
                    } else {
                        -"undefined"
                    }
                },
                between = {
                    -", \n"
                }
            )
            -"\n) }\n"

            //Generate toJson()
            -"public toJson(): object { return {\n"
            typedRule.primaryConstructor?.valueParameters?.filter { it.hasValOrVar() }?.forEachBetween(
                forItem = {
                    -it.jsonName
                    -": this."
                    -it.jsonName
                },
                between = {
                    -", \n"
                }
            )
            -"\n} }\n"
        }

        if (typedRule.isData()) {
            //Generate hashCode() if not present
            if (typedRule.body?.declarations?.any { it is FunctionDescriptor && (it as KtDeclaration).name == "hashCode" && it.valueParameters.isEmpty() } != true) {
                -"public hashCode(): number {\nlet hash = 17;\n"
                typedRule.primaryConstructor?.valueParameters?.filter { it.hasValOrVar() }?.forEach { param ->
                    val rawType = param.typeReference?.resolvedType
                        ?: throw IllegalArgumentException("No type reference available to generate hashCode() function")
                    val typeName = param.typeReference?.resolvedType?.getJetTypeFqName(false)
                    -"hash = 31 * hash + "
                    replacements.functions[typeName + ".hashCode"]?.firstOrNull()?.let {
                        emitTemplate(
                            requiresWrapping = true,
                            template = it.template,
                            receiver = listOf("this.", param.nameIdentifier)
                        )
                    } ?: run {
                        if (rawType.constructor.declarationDescriptor is TypeParameterDescriptor) {
                            out.addImport("khrysalis/dist/Kotlin", "hashAnything")
                            -"hashAnything(this."
                            -param.nameIdentifier
                            -")"
                        } else {
                            -"this."
                            -param.nameIdentifier
                            -"?.hashCode() ?? 0"
                        }
                    }
                    -";\n"
                }

                -"return hash;\n}\n"
            }

            //Generate equals() if not present
            if (typedRule.body?.declarations?.any { it is FunctionDescriptor && (it as KtDeclaration).name == "equals" && it.valueParameters.size == 1 } != true) {
                -"public equals(other: any): boolean { return other instanceof "
                -typedRule.nameIdentifier
                typedRule.primaryConstructor?.valueParameters?.filter { it.hasValOrVar() }?.forEach { param ->
                    -" && "
                    val rawType = param.typeReference?.resolvedType
                        ?: throw IllegalArgumentException("No type reference available to generate hashCode() function")
                    val typeName = param.typeReference?.resolvedType?.getJetTypeFqName(false)
                    replacements.functions[typeName + ".equals"]?.firstOrNull()?.let {
                        emitTemplate(
                            requiresWrapping = true,
                            template = it.template,
                            receiver = listOf("this.", param.nameIdentifier),
                            allParameters = listOf("other.", param.nameIdentifier),
                            parameter = { listOf("other.", param.nameIdentifier) },
                            parameterByIndex = { listOf("other.", param.nameIdentifier) }
                        )
                    } ?: run {
                        out.addImport("khrysalis/dist/Kotlin", "safeEq")
                        -"safeEq(this."
                        -param.nameIdentifier
                        -", "
                        -"other."
                        -param.nameIdentifier
                        -")"
                    }
                }
                -" }\n"
            }

            //Generate toString() if not present
            if (typedRule.body?.declarations?.any { it is FunctionDescriptor && (it as KtDeclaration).name == "toString" && it.valueParameters.isEmpty() } != true) {
                -"public toString(): string { return "
                -'`'
                -typedRule.nameIdentifier
                -'('
                typedRule.primaryConstructor?.valueParameters?.filter { it.hasValOrVar() }?.forEachBetween(
                    forItem = {
                        -it.nameIdentifier
                        -" = \${this."
                        -it.nameIdentifier
                        -"}"
                    },
                    between = { -", " }
                )
                -')'
                -'`'
                -" }\n"
            }

            //Generate copy(..)
            -"public copy("
            typedRule.primaryConstructor?.valueParameters?.filter { it.hasValOrVar() }?.forEachBetween(
                forItem = {
                    -it.nameIdentifier
                    -": "
                    -it.typeReference
                    -" = this."
                    -it.nameIdentifier
                },
                between = { -", " }
            )
            -") { return new "
            -typedRule.nameIdentifier
            -"("
            typedRule.primaryConstructor?.valueParameters?.filter { it.hasValOrVar() }?.forEachBetween(
                forItem = {
                    -it.nameIdentifier
                },
                between = { -", " }
            )
            -"); }\n"
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

            -"public static valueOf(name: string): "
            -typedRule.nameIdentifier
            -" { return ("
            -typedRule.nameIdentifier
            -" as any)[name]; }\n"

            -"public toString(): string { return this.name }\n"
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
                -it.valueArgumentList
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
        -" {\n"
        writeInterfaceMarkers(typedRule)
        -"public constructor() {\n"
        -"super"
        -'('
        val args = arrayListOf({ ->
            -'"'
            -typedRule.nameIdentifier
            -'"'
            Unit
        })
        (typedRule.initializerList?.initializers?.firstOrNull() as? KtSuperTypeCallEntry)?.valueArguments?.forEach {
            args.add { -it }
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
            -'"'
            Unit
        })
        (typedRule.initializerList?.initializers?.firstOrNull() as? KtSuperTypeCallEntry)?.valueArguments?.forEach {
            args.add { -it }
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
        -typedRule.typeParameterList
        -typedRule.valueParameterList
        -" {\n"
        val parent = typedRule.parentOfType<KtClassBody>()!!.parentOfType<KtClass>()!!.resolvedClass!!
        withReceiverScope(parent, "result") { outName ->
            -"let "
            -outName
            -" = new "
            typedRule.getDelegationCall().let {
                -parent.name.asString()
                -it.typeArgumentList
                -it.valueArgumentList
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

private val KtParameter.jsonName: String
    get() = this.annotations
        .flatMap { it.entries }
        .find { it.typeReference?.text?.substringAfterLast('.')?.substringBefore('(') == "JsonProperty" }
        ?.valueArguments
        ?.firstOrNull()
        ?.getArgumentExpression()
        ?.text
        ?.trim('"')
        ?: this.name ?: "x"
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
        it.type.getJetTypeFqName(false).split('.').joinToString("").filter { it.isLetter() }
    })