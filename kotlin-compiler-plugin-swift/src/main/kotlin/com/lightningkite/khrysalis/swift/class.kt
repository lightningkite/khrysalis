package com.lightningkite.khrysalis.swift

import com.lightningkite.khrysalis.generic.PartialTranslatorByType
import com.lightningkite.khrysalis.util.allOverridden
import com.lightningkite.khrysalis.util.forEachBetween
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.js.translate.callTranslator.getReturnType
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.*
import org.jetbrains.kotlin.psi2ir.findFirstFunction
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperClassNotAny
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter
import org.jetbrains.kotlin.types.FlexibleType
import org.jetbrains.kotlin.types.SimpleType
import org.jetbrains.kotlin.types.WrappedType
import org.jetbrains.kotlin.types.isNullable
import org.jetbrains.kotlin.types.typeUtil.nullability
import org.jetbrains.kotlin.types.typeUtil.supertypes
import java.util.*
import kotlin.collections.ArrayList
import kotlin.reflect.jvm.internal.impl.types.KotlinType


fun SwiftTranslator.registerClass() {

    fun PartialTranslatorByType<SwiftFileEmitter, Unit, Any>.ContextByType<*>.writeClassHeader(
        on: KtClassOrObject,
        defaultName: String = "Companion"
    ) {
        val typedRule = on
        -"class "
        -(on.nameIdentifier ?: defaultName)
        -typedRule.typeParameterList
        typedRule.superTypeListEntries
            .mapNotNull { it as? KtSuperTypeCallEntry }
            .map { it.resolvedCall?.getReturnType() }
            .plus(
                typedRule.superTypeListEntries
                    .mapNotNull { it as? KtSuperTypeEntry }
                    .map { it.typeReference }
            )
            .let {
                if (on is KtClass && on.isData()) {
                    it + listOf("KDataClass")
                } else it
            }
            .let {
                if (on is KtClass && on.isEnum()) {
                    it + listOf("KEnum")
                } else it
            }
            .let {
                val over = on.resolvedClass?.findFirstFunction("equals") { it.valueParameters.size == 1 && it.valueParameters[0].type.getJetTypeFqName(false) == "kotlin.Any" }
                val immediate = over?.containingDeclaration == on.resolvedClass
                val anyOtherExists = over?.overriddenDescriptors?.any { it.containingDeclaration.fqNameOrNull()?.asString() != "kotlin.Any" } == true
                if (immediate && !anyOtherExists) {
                    it + listOf("KEquatable")
                } else it
            }
            .let {
                val over = on.resolvedClass?.findFirstFunction("hashCode") { it.valueParameters.size == 0 }
                val immediate = over?.containingDeclaration == on.resolvedClass
                val anyOtherExists = over?.overriddenDescriptors?.any { it.containingDeclaration.fqNameOrNull()?.asString() != "kotlin.Any" } == true
                if (immediate && !anyOtherExists) {
                    it + listOf("KHashable")
                } else it
            }
            .let {
                val over = on.resolvedClass?.findFirstFunction("toString") { it.valueParameters.size == 0 }
                val immediate = over?.containingDeclaration == on.resolvedClass
                val anyOtherExists = over?.overriddenDescriptors?.any { it.containingDeclaration.fqNameOrNull()?.asString() != "kotlin.Any" } == true
                if (immediate && !anyOtherExists) {
                    it + listOf("KStringable")
                } else it
            }
            .takeUnless { it.isEmpty() }?.let {
                -" : "
                it.forEachBetween(
                    forItem = { -it },
                    between = { -", " }
                )
            }
        typedRule.typeConstraintList?.let {
            -" where "
            -it
        }
    }

    handle<KtClass>(
        condition = { typedRule.isInterface() },
        priority = 100
    ) {
        -(typedRule.visibilityModifier() ?: "public")
        -" protocol "
        -typedRule.nameIdentifier
        typedRule.typeParameterList?.let {
            //TODO
            //This is possible using 4 entries per interface:
            //struct XBox<T>: X - Implements X from a contained 'boxed' property of AnyX.
            //protocol AnyX - has copy of all the properties with untyped_ prepended
            //protocol X: AnyX - has associated type
            //extension X - implements AnyX, also provides .box() function
            //Then:
            //- Use XBox<T> in all property or argument positions
            //- Every time something is used in a property or argument position and is generic, use `.box()`
            //- Every time something using XBox<T> is typecast, `use x.boxed`
            throw IllegalStateException("Converting a generic interface is not possible at the moment due to Swift crap. See code for potential solution, yet unimplemented due to complexity.")
        }
        -" {\n"
        -typedRule.body
        -"}\n"
        if (typedRule.body?.hasPostActions() == true) {
            -(typedRule.visibilityModifier() ?: "public")
            -" extension "
            -typedRule.nameIdentifier
            -" {"
            typedRule.body?.runPostActions()
            -"\n}"
        }
        typedRule.runPostActions()
    }

    handle<KtClass> {
        -(typedRule.visibilityModifier() ?: "public")
        -' '
        writeClassHeader(typedRule)
        -" {\n"
        typedRule.primaryConstructor?.valueParameters?.filter { it.hasValOrVar() }?.forEach {

            -(it.visibilityModifier() ?: "public")
            -" var "
            -it.nameIdentifier
            it.typeReference?.let {
                -": "
                -it
            }
            -"\n"
        }
        val parentClassName =
            typedRule.parentOfType<KtClassBody>()?.parentOfType<KtClass>()?.resolvedClass?.name?.asString()
        if (typedRule.isInner()) {
            -"parentThis: "
            -parentClassName
            -"\n"
        }

        run {
            val c = typedRule.resolvedClass ?: return@run
            val s = c.getSuperClassNotAny() ?: return@run
            val sc = c.unsubstitutedPrimaryConstructor ?: return@run
            val cc = c.unsubstitutedPrimaryConstructor ?: return@run
            if (sc.valueParameters.size != cc.valueParameters.size) return@run
            if (sc.valueParameters.zip(cc.valueParameters).all {
                    it.first.name.asString() == it.second.name.asString()
                            && it.first.type.getJetTypeFqName(false) == it.second.type.getJetTypeFqName(false)
                }) {
                -"override "
            }
        }
        if (typedRule.isEnum()) {
            -"private"
        } else if (typedRule.hasModifier(KtTokens.ABSTRACT_KEYWORD)) {
            -"protected"
        } else {
            -(typedRule.primaryConstructor?.visibilityModifier() ?: "public")
        }
        -" init("
        typedRule.primaryConstructor?.let { cons ->
            (if (typedRule.isEnum()) {
                listOf("name: String") + cons.valueParameters
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
                -"name: String"
            } else if (typedRule.isInner()) {
                -"parentThis: $parentClassName"
            } else Unit
        }
        -") {\n"
        if (typedRule.isEnum()) {
            -"self.name = name;\n"
        } else if (typedRule.isInner()) {
            -"self.parentThis = parentThis;\n"
        }
        //Parameter assignment first
        typedRule.primaryConstructor?.let { cons ->
            cons.valueParameters.asSequence().filter { it.hasValOrVar() }.forEach {
                -"self."
                -it.nameIdentifier
                -" = "
                -it.nameIdentifier
                -"\n"
            }
        }
        //Then, in order, variable initializers
        typedRule.body?.children?.forEach {
            when (it) {
                is KtProperty -> {
                    it.initializer?.let { init ->
                        -"self."
                        -it.nameIdentifier
                        -" = "
                        -init
                        -"\n"
                    }
                }
            }
        }
        //Then super
        typedRule.superTypeListEntries.mapNotNull { it as? KtSuperTypeCallEntry }.takeUnless { it.isEmpty() }
            ?.firstOrNull()?.let {
                -"super.init("
                var first = true
                if (typedRule.isEnum()) {
                    first = false
                    -"name: String"
                }
                it.resolvedCall?.valueArguments?.entries?.sortedBy { it.key.index }?.forEach {
                    val args = it.value.arguments
                    when (args.size) {
                        0 -> {
                        }
                        1 -> {
                            if (first) {
                                first = false
                            } else {
                                -", "
                            }
                            -it.key.name.asString()
                            -": "
                            -args[0].getArgumentExpression()
                        }
                        else -> {
                            if (first) {
                                first = false
                            } else {
                                -", "
                            }
                            -it.key.name.asString()
                            -": ["
                            args.forEachBetween(
                                forItem = { -it.getArgumentExpression() },
                                between = { -", " }
                            )
                            -"]"
                        }
                    }
                }
                -")\n"
            }
        //Then, in order, anon initializers
        typedRule.body?.children?.forEach {
            when (it) {
                is KtAnonymousInitializer -> {
                    val b = it.body
                    if (b is KtBlockExpression) {
                        b.statements.forEach {
                            -it
                            -"\n"
                        }
                    } else {
                        -b
                        -"\n"
                    }
                }
            }
        }
        -"}\n"

        if (typedRule.superTypeListEntries
                .mapNotNull { it as? KtSuperTypeEntry }
                .any { it.typeReference?.resolvedType?.getJetTypeFqName(false) == "com.lightningkite.khrysalis.Codable" }
        ) {
            -"required public init(from decoder: Decoder) throws {\n"
            -"let values = try decoder.container(keyedBy: CodingKeys.self)\n"
            typedRule.primaryConstructor?.valueParameters?.filter { it.hasValOrVar() }?.forEach {
                if (it.typeReference?.resolvedType?.getJetTypeFqName(false) == "kotlin.Double") {
                    it.defaultValue?.let { default ->
                        -it.nameIdentifier
                        -" = try values.decodeDoubleIfPresent(forKey: ."
                        -it.nameIdentifier
                        -") ?? "
                        -default
                    } ?: run {
                        -it.nameIdentifier
                        -" = try values.decodeDouble(forKey: ."
                        -it.nameIdentifier
                        -")"
                    }
                } else if (it.typeReference?.resolvedType?.getJetTypeFqName(false) == "kotlin.Double?") {
                    it.defaultValue?.let { default ->
                        -it.nameIdentifier
                        -" = try values.decodeDoubleIfPresent(forKey: ."
                        -it.nameIdentifier
                        -") ?? "
                        -default
                    } ?: run {
                        -it.nameIdentifier
                        -" = try values.decodeDoubleIfPresent(forKey: ."
                        -it.nameIdentifier
                        -")"
                    }
                } else {
                    it.defaultValue?.let { default ->
                        -it.nameIdentifier
                        -" = try values.decodeIfPresent("
                        -it.typeReference
                        -".self, forKey: ."
                        -it.nameIdentifier
                        -") ?? "
                        -default
                    } ?: run {
                        -it.nameIdentifier
                        -" = try values.decode("
                        -it.typeReference
                        -".self, forKey: ."
                        -it.nameIdentifier
                        -")"
                    }
                }
                -"\n"
            }

            -"}\n"
            -'\n'
            -"enum CodingKeys: String, CodingKey {\n"
            typedRule.primaryConstructor?.valueParameters?.filter { it.hasValOrVar() }?.forEach {
                val jsonName = it.jsonName
                -"case "
                -it.nameIdentifier
                -" = \"${jsonName}\"\n"
            }

            -"}\n"
            -'\n'
            -"public func encode(to encoder: Encoder) throws {\n"
            -"var container = encoder.container(keyedBy: CodingKeys.self)\n"
            typedRule.primaryConstructor?.valueParameters?.filter { it.hasValOrVar() }?.forEach {
                if (it.typeReference?.resolvedType?.isNullable() == false) {
                    -"try container.encode(self."
                    -it.nameIdentifier
                    -", forKey: ."
                    -it.nameIdentifier
                    -")"
                } else {
                    -"try container.encodeIfPresent(self."
                    -it.nameIdentifier
                    -", forKey: ."
                    -it.nameIdentifier
                    -")"
                }
                -"\n"
            }

            -"}\n"
            -'\n'
        }

        if (typedRule.isData()) {
            //Generate hashCode() if not present
            if (typedRule.body?.declarations?.any { it is FunctionDescriptor && (it as KtDeclaration).name == "hashCode" && it.valueParameters.isEmpty() } != true) {
                -"public func hash(into hasher: inout Hasher) {\n"
                typedRule.primaryConstructor?.valueParameters?.filter { it.hasValOrVar() }?.forEach { param ->
                    -"hasher.combine("
                    -param.nameIdentifier
                    -")\n"
                }
                -"}\n"
            }

            //Generate equals() if not present
            if (typedRule.body?.declarations?.any { it is FunctionDescriptor && (it as KtDeclaration).name == "equals" && it.valueParameters.size == 1 } != true) {
                -"public static func == (lhs: "
                -typedRule.nameIdentifier
                -", rhs: "
                -typedRule.nameIdentifier
                -") -> Bool { return "
                typedRule.primaryConstructor?.valueParameters?.filter { it.hasValOrVar() }?.forEachBetween(
                    forItem = { param ->
                        val typeName = param.typeReference?.resolvedType?.getJetTypeFqName(false)
                        replacements.functions[typeName + ".equals"]?.firstOrNull()?.let {
                            emitTemplate(
                                requiresWrapping = true,
                                template = it.template,
                                receiver = listOf("lhs.", param.nameIdentifier),
                                allParameters = listOf("rhs.", param.nameIdentifier),
                                parameter = { listOf("rhs.", param.nameIdentifier) },
                                parameterByIndex = { listOf("rhs.", param.nameIdentifier) }
                            )
                        } ?: run {
                            -"lhs."
                            -param.nameIdentifier
                            -" == "
                            -"rhs."
                            -param.nameIdentifier
                        }
                    },
                    between = {
                        -" && "
                    }
                )
                -" }\n"
            }

            //Generate toString() if not present
            if (typedRule.body?.declarations?.any { it is FunctionDescriptor && (it as KtDeclaration).name == "toString" && it.valueParameters.isEmpty() } != true) {
                -"public var description: String { return "
                -'"'
                -typedRule.nameIdentifier
                -'('
                typedRule.primaryConstructor?.valueParameters?.filter { it.hasValOrVar() }?.forEachBetween(
                    forItem = {
                        -it.nameIdentifier
                        -" = \\(self."
                        -it.nameIdentifier
                        -")"
                    },
                    between = { -", " }
                )
                -')'
                -'"'
                -" }\n"
            }

            //Generate copy(..)
            -"public func copy("
            typedRule.primaryConstructor?.valueParameters?.filter { it.hasValOrVar() }?.forEachBetween(
                forItem = {
                    -it.nameIdentifier
                    -": "
                    -it.typeReference
                    -"? = "
                    if (it.typeReference?.resolvedType?.isNullable() == true) {
                        -".some(nil)"
                    } else {
                        -"nil"
                    }
                },
                between = { -", " }
            )
            -") -> "
            -typedRule.nameIdentifier
            -typedRule.typeParameterList
            -" { return "
            -typedRule.nameIdentifier
            -"("
            typedRule.primaryConstructor?.valueParameters?.filter { it.hasValOrVar() }?.forEachBetween(
                forItem = {
                    -it.nameIdentifier
                    -": "
                    if (it.typeReference?.resolvedType?.isNullable() == true) {
                        -"invertOptional("
                        -it.nameIdentifier
                        -") ?? self."
                        -it.nameIdentifier
                    } else {
                        -it.nameIdentifier
                        -" ?? self."
                        -it.nameIdentifier
                    }
                },
                between = { -", " }
            )
            -") }\n"
        }

        -typedRule.body

        if (typedRule.isEnum()) {
            -"private static let _values: Array<"
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
            -"]\n"
            -"public static func values() -> Array<"
            -typedRule.nameIdentifier
            -"> { return _values }\n"
            -"public let name: String;\n"

            -"public func toString() -> String { return self.name }\n"
        }

        -"}"
        typedRule.runPostActions()
    }

    handle<KtSuperExpression> {
        -"super"
    }

    handle<KtObjectDeclaration> {
        -(typedRule.visibilityModifier() ?: "public")
        -' '
        writeClassHeader(typedRule)
        -" {\n"
        -"private init() {\n"
        //Then, in order, variable initializers and anon initializers
        typedRule.body?.children?.forEach {
            when (it) {
                is KtProperty -> {
                    it.initializer?.let { init ->
                        -"self."
                        -it.nameIdentifier
                        -" = "
                        -init
                        -"\n"
                    }
                }
                is KtAnonymousInitializer -> {
                    val b = it.body
                    if (b is KtBlockExpression) {
                        b.statements.forEach {
                            -it
                            -"\n"
                        }
                    } else {
                        -b
                        -"\n"
                    }
                }
            }
        }
        typedRule.superTypeListEntries.mapNotNull { it as? KtSuperTypeCallEntry }.takeUnless { it.isEmpty() }
            ?.firstOrNull()?.let {
                -"super"
                -it.valueArgumentList
                -"\n"
            }
        -"}\n"
        -"public static let INSTANCE = "
        -(typedRule.nameIdentifier ?: "Companion")
        -"()\n"
        -typedRule.body
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
        //TODO
    }

    //Simple enum
    handle<KtClass>(
        condition = { typedRule.isSimpleEnum() },
        priority = 10,
        action = {
            -(typedRule.visibilityModifier() ?: "public")
            -" enum "
            -typedRule.nameIdentifier
            -": CaseIterable {\n"
            for (entry in typedRule.body?.enumEntries ?: listOf()) {
                -"case "
                -entry.nameIdentifier
                -"\n"
            }
            -typedRule.body
            -"}"
        }
    )
    handle<KtEnumEntry>(
        condition = {
            typedRule.containingClass()?.isSimpleEnum() == true
        },
        priority = 1,
        action = {}
    )
    handle<KtEnumEntry> {
        -"public class "
        -typedRule.nameIdentifier
        -"Type: "
        -typedRule
            .parentOfType<KtClassBody>()
            ?.parentOfType<KtClass>()
            ?.nameIdentifier
        -"{\n"
        -"public init() {\n"
        //Then variable initializers
        typedRule.body?.children?.forEach {
            when (it) {
                is KtProperty -> {
                    it.initializer?.let { init ->
                        -"self."
                        -it.nameIdentifier
                        -" = "
                        -init
                        -";\n"
                    }
                }
            }
        }
        -"super.init"
        -'('
        -"name: \""
        -typedRule.nameIdentifier
        -"\""
        typedRule.initializerList?.initializers?.firstOrNull()?.resolvedCall?.valueArguments?.entries?.sortedBy { it.key.index }?.forEach {
            val args = it.value.arguments
            when (args.size) {
                0 -> {
                }
                1 -> {
                    -", "
                    -it.key.name.asString()
                    -": "
                    -args[0].getArgumentExpression()
                }
                else -> {
                    -", "
                    -it.key.name.asString()
                    -": ["
                    args.forEachBetween(
                        forItem = { -it.getArgumentExpression() },
                        between = { -", " }
                    )
                    -"]"
                }
            }
        }
        -");\n"
        //Then anon initializers
        typedRule.body?.children?.forEach {
            when (it) {
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
        -"}\npublic static let "
        -typedRule.nameIdentifier
        -" = "
        -typedRule.nameIdentifier
        -"Type()"
    }

    handle<KtSecondaryConstructor> {
        -(typedRule.visibilityModifier() ?: "public")
        -" convenience init("
        typedRule.valueParameters.forEachBetween(
            forItem = { -it },
            between = { -", " }
        )
        -") {\n"
        -"self.init("
        val dg = typedRule.getDelegationCallOrNull()
        val resSuper = dg?.resolvedCall
        if (resSuper != null) {
            var first = true
            resSuper.valueArguments.entries.sortedBy { it.key.index }.forEach {
                val args = it.value.arguments
                when (args.size) {
                    0 -> {
                    }
                    1 -> {
                        if (first) {
                            first = false
                        } else {
                            -", "
                        }
                        -it.key.name.asString()
                        -": "
                        -args[0].getArgumentExpression()
                    }
                    else -> {
                        if (first) {
                            first = false
                        } else {
                            -", "
                        }
                        -it.key.name.asString()
                        -": ["
                        args.forEachBetween(
                            forItem = { -it.getArgumentExpression() },
                            between = { -", " }
                        )
                        -"]"
                    }
                }
            }
        }
        -")\n"
        -typedRule.bodyExpression?.allChildren?.toList()?.drop(1)?.dropLast(1)
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


fun KtClass.isSimpleEnum() = isEnum() && body?.enumEntries?.all { !it.hasInitializer() && it.body == null } == true