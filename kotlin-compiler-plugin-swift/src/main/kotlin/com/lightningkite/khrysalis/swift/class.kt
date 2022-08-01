package com.lightningkite.khrysalis.swift

import com.lightningkite.khrysalis.generic.KotlinTranslator
import com.lightningkite.khrysalis.analysis.*
import com.lightningkite.khrysalis.swift.replacements.SwiftImport
import com.lightningkite.khrysalis.util.forEachBetween
import com.lightningkite.khrysalis.util.fqNameWithoutTypeArgs
import com.lightningkite.khrysalis.util.functionsNamed
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.ir.util.findFirstFunction
import org.jetbrains.kotlin.js.translate.callTranslator.getReturnType
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.*
import org.jetbrains.kotlin.resolve.annotations.argumentValue
import org.jetbrains.kotlin.resolve.calls.util.FakeCallableDescriptorForObject
import org.jetbrains.kotlin.resolve.constants.ArrayValue
import org.jetbrains.kotlin.resolve.constants.StringValue
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperClassNotAny
import org.jetbrains.kotlin.resolve.scopes.receivers.ClassValueReceiver
import org.jetbrains.kotlin.synthetic.hasJavaOriginInHierarchy
import org.jetbrains.kotlin.types.isNullable
import org.jetbrains.kotlin.types.typeUtil.isNullableAny
import java.util.*
import kotlin.collections.ArrayList

fun KtModifierListOwner.swiftVisibility(): Any? = when(this) {
    is KtNamedFunction -> if(this.canBeExtended) "open" else visibilityModifier()
    else -> visibilityModifier()
}

fun SwiftTranslator.registerClass() {

    fun KotlinTranslator<SwiftFileEmitter>.ContextByType<*>.writeProtocolAssociatedtypeImpl(
        on: KtClassOrObject
    ) {
        on.superTypeListEntries
            .mapNotNull {
                it.typeReference?.resolvedType
            }
            .filter {
                (it.constructor.declarationDescriptor as? ClassDescriptor)?.kind == ClassKind.INTERFACE
            }
            .forEach {
                (it.constructor.declarationDescriptor as? ClassDescriptor)?.declaredTypeParameters?.forEach { p ->
                    -"public typealias "
                    -p.name.asString()
                    -" = "
                    -it.arguments[p.index].type
                    -"\n"
                }
            }
    }

    fun KotlinTranslator<SwiftFileEmitter>.ContextByType<*>.writeClassHeader(
        on: KtClassOrObject
    ) {
        val typedRule = on
        if(on.isOpen) {
            // might be open
        } else {
            -"final "
        }
        -"class "
        -swiftTopLevelNameElement(on)
        -typedRule.typeParameterList
        typedRule.superTypeListEntries
            .mapNotNull { it as? KtSuperTypeCallEntry }
            .map {
                it.calleeExpression.typeReference
            }
            .plus(
                typedRule.superTypeListEntries
                    .mapNotNull { it as? KtSuperTypeEntry }
                    .map {
                        listOf(it.typeReference)
                    }
            )
            .let {
                if (on is KtClass && on.isData()) {
                    out.addImport(SwiftImport("KhrysalisRuntime"))
                    val result = it + "CustomStringConvertible"
//                    println("${on.name} - ${on.resolvedClass?.unsubstitutedMemberScope?.functionsNamed("hashCode")?.filter { it.valueParameters.size == 0 }?.firstOrNull()}")
                    if(on.resolvedClass?.unsubstitutedMemberScope?.functionsNamed("hashCode")?.filter { it.valueParameters.size == 0 }?.firstOrNull()?.useOverrideInSwift() == true)
                        result
                    else
                        result + "Hashable"
                } else it
            }
            .let {
                if (on is KtClass && on.isData() && on.hasDatabaseModelAnnotation) {
                    out.addImport(SwiftImport("KhrysalisRuntime"))
                    it + listOf("PropertyIterable")
                } else it
            }
            .let {
                if (on is KtClass && on.isEnum()) {
                    out.addImport(SwiftImport("KhrysalisRuntime"))
                    it + listOf("StringEnum")
                } else it
            }
            .let {
                if (on.body?.functions?.find { it.name == "equals" && it.valueParameters.size == 1 }?.resolvedFunction?.useOverrideInSwift() == false) {
                    out.addImport(SwiftImport("KhrysalisRuntime"))
                    it + listOf("KEquatable")
                } else it
            }
            .let {
                if (on.body?.functions?.find { it.name == "hashCode" && it.valueParameters.size == 0 }?.resolvedFunction?.useOverrideInSwift() == false) {
                    out.addImport(SwiftImport("KhrysalisRuntime"))
                    it + listOf("KHashable")
                } else it
            }
            .let {
                if (on.body?.functions?.find { it.name == "toString" && it.valueParameters.size == 0 }?.resolvedFunction?.useOverrideInSwift() == false) {
                    out.addImport(SwiftImport("KhrysalisRuntime"))
                    it + listOf("KStringable")
                } else it
            }
            .let {
                if (on.hasCodableAnnotation && !on.implementsKotlinCodable) {
                    it + listOf("Codable")
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

    handle<KtParameter>(
        condition = {
            typedRule.parentOfType<KtParameterList>()?.parentOfType<KtFunction>()?.resolvedFunction?.let {
                it.name.asString() == "equals" && it.valueParameters.size == 1 && it.valueParameters[0].type.isNullableAny()
            } == true
        },
        priority = 10000
    ) {
        -typedRule.name
        -": Any"
    }

    handle<KtClass>(
        condition = { typedRule.resolvedClass?.swiftTopLevelMessedUp == true },
        priority = 1000,
        action = {
            this.noReuse = true
            typedRule.containingKtFile.after.add {
                doSuper()
                -"\n"
            }
        }
    )
    handle<KtObjectDeclaration>(
        condition = { typedRule.resolvedClass?.swiftTopLevelMessedUp == true },
        priority = 1000,
        action = {
            this.noReuse = true
            typedRule.containingKtFile.after.add {
                doSuper()
                -"\n"
            }
        }
    )

    handle<KtClass>(
        condition = { typedRule.isInterface() },
        priority = 100
    ) {
        -(typedRule.swiftVisibility() ?: "public")
        -" protocol "
        -swiftTopLevelNameElement(typedRule)
        -": "
        val x = typedRule.resolvedClass?.annotations
            ?.findAnnotation(FqName("com.lightningkite.khrysalis.SwiftProtocolExtends"))
            ?.argumentValue("names")
            ?.let { it as? ArrayValue }
            ?.let { it.value.map { (it as? StringValue)?.value } }
        listOf("AnyObject")
            .plus(typedRule.superTypeListEntries.map { it.typeReference })
            .plus(x ?: listOf<String>())
            .forEachBetween(
                forItem = { -it },
                between = { -", " }
            )
        -" {\n"
        typedRule.typeParameters.forEach {
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
            -"associatedtype "
            -it
            -'\n'
        }
        -typedRule.body
        -"}\n"
        if (typedRule.body?.hasPostActions() == true) {
            -(typedRule.swiftVisibility() ?: "public")
            -" extension "
            -swiftTopLevelNameElement(typedRule)
            -" {"
            typedRule.body?.runPostActions()
            -"\n}"
        }
        typedRule.runPostActions()
    }

    handle<KtClass> {
        -(typedRule.swiftVisibility() ?: "public")
        -' '
        writeClassHeader(typedRule)
        -" {\n"
        writeProtocolAssociatedtypeImpl(typedRule)
        typedRule.primaryConstructor?.valueParameters?.filter { it.hasValOrVar() }?.forEach {
            if (it.resolvedPrimaryConstructorParameter?.hasSwiftOverride == true) {
                -"private "
                if (it.annotationEntries.any {
                        it.resolvedAnnotation?.fqName?.asString()
                            ?.equals("com.lightningkite.khrysalis.Unowned", true) == true
                    }) {
                    -"unowned "
                }
                -"var _"
                -it.nameIdentifier
                it.typeReference?.let {
                    -": "
                    -it
                }
                -"\n"
                -"override "
                -(it.swiftVisibility() ?: "public")
                -" var "
                -it.nameIdentifier
                it.typeReference?.let {
                    -": "
                    -it
                }
                -" { get { return self._"
                -it.nameIdentifier
                -" } set(value) { self._"
                -it.nameIdentifier
                -" = value } }"
            } else {
                -(it.swiftVisibility() ?: "public")
                if (it.annotationEntries.any {
                        it.resolvedAnnotation?.fqName?.asString()
                            ?.equals("com.lightningkite.khrysalis.Unowned", true) == true
                    }) {
                    -" unowned"
                }
                -" var "
                -it.nameIdentifier
                it.typeReference?.let {
                    -": "
                    -it
                }
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
            val sc = s.unsubstitutedPrimaryConstructor ?: return@run
            val cc = c.unsubstitutedPrimaryConstructor ?: return@run
            if (sc.valueParameters.size != cc.valueParameters.size) return@run
            if (sc.valueParameters.zip(cc.valueParameters).all {
                    it.first.name.asString() == it.second.name.asString()
                            && it.first.type.fqNameWithoutTypeArgs == it.second.type.fqNameWithoutTypeArgs
                }) {
                -"override "
            }
        }
        handleConstructor(this, parentClassName, this@registerClass)

        if (typedRule.shouldGenerateCodable) {
            -"convenience required public init(from decoder: Decoder) throws {\n"
            -"let values = try decoder.container(keyedBy: CodingKeys.self)\n"
            -"self.init(\n"
            typedRule.primaryConstructor?.valueParameters?.filter { it.hasValOrVar() }?.forEachBetween(forItem = {
                if ((it.resolvedValueParameter as? ValueParameterDescriptor)?.useName != false) {
                    -it.nameIdentifier
                    -": "
                }
                it.defaultValue?.let { default ->
                    -"values.contains(."
                    -it.nameIdentifier
                    -") ? try values.decode("
                    -it.typeReference
                    -".self, forKey: ."
                    -it.nameIdentifier
                    -") : "
                    -default
                } ?: run {
                    -"try values.decode("
                    -it.typeReference
                    -".self, forKey: ."
                    -it.nameIdentifier
                    -")"
                }
            }, between = { -",\n" })
            -"\n)\n}\n\n"
            -"enum CodingKeys: String, CodingKey {\n"
            typedRule.primaryConstructor?.valueParameters?.filter { it.hasValOrVar() }?.forEach {
                val jsonName = it.jsonName()
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
                val resolveFunction = typedRule.resolvedClass!!.unsubstitutedMemberScope.findFirstFunction("hashCode") { it.valueParameters.size == 0 }
                if(resolveFunction.useOverrideInSwift()) {
                    -"override ${if(typedRule.isOpen) "open" else "public"} func hashCode() -> Int {\n"
                    -"var hasher = Hasher()\n"
                    typedRule.primaryConstructor?.valueParameters?.filter { it.hasValOrVar() }?.forEach { param ->
                        -"hasher.combine("
                        -param.nameIdentifier
                        -")\n"
                    }
                    -"return hasher.finalize()\n}\n"
                } else {
                    -"public func hash(into hasher: inout Hasher) {\n"
                    typedRule.primaryConstructor?.valueParameters?.filter { it.hasValOrVar() }?.forEach { param ->
                        -"hasher.combine("
                        -param.nameIdentifier
                        -")\n"
                    }
                    -"\n}\n"
                }
            }

            //Generate equals() if not present
            if (typedRule.body?.declarations?.any { it is FunctionDescriptor && (it as KtDeclaration).name == "equals" && it.valueParameters.size == 1 } != true) {
                val resolveFunction = typedRule.resolvedClass!!.unsubstitutedMemberScope.findFirstFunction("equals") { it.valueParameters.size == 1 }
                fun emitCompare(lhs: String, rhs: String) {
                    typedRule.primaryConstructor?.valueParameters?.filter { it.hasValOrVar() }?.forEachBetween(
                        forItem = { param ->
                            val typeName = param.typeReference?.resolvedType?.fqNameWithoutTypeArgs
                            replacements.functions[typeName + ".equals"]?.firstOrNull()?.let {
                                emitTemplate(
                                    requiresWrapping = true,
                                    template = it.template,
                                    receiver = listOf("$lhs.", param.nameIdentifier),
                                    allParameters = listOf("$rhs.", param.nameIdentifier),
                                    parameter = { listOf("$rhs.", param.nameIdentifier) },
                                    parameterByIndex = { listOf("$rhs.", param.nameIdentifier) }
                                )
                            } ?: run {
                                -"$lhs."
                                -param.nameIdentifier
                                -" == "
                                -"$rhs."
                                -param.nameIdentifier
                            }
                        },
                        between = {
                            -" && "
                        }
                    )
                }
                if(resolveFunction.useOverrideInSwift()) {
                    // If this stems from a manual implementation somewhere, we'll use KEquatable instead
                    -"override ${if(typedRule.isOpen) "open" else "public"} func equals(other: Any) -> Bool {\n"
                    -"guard let other = other as? "
                    -swiftTopLevelNameElement(typedRule)
                    -" else { return false }\n"
                    -"return "
                    emitCompare("self", "other")
                    -"\n}\n"
                } else {
                    -"public static func == (lhs: "
                    -swiftTopLevelNameElement(typedRule)
                    -", rhs: "
                    -swiftTopLevelNameElement(typedRule)
                    -") -> Bool { return "
                    emitCompare("lhs", "rhs")
                    -" }\n"
                }
            }

            //Generate toString() if not present
            if (typedRule.body?.declarations?.any { it is FunctionDescriptor && (it as KtDeclaration).name == "toString" && it.valueParameters.isEmpty() } != true) {
                -"public var description: String { return "
                -'"'
                -swiftTopLevelNameElement(typedRule)
                -'('
                typedRule.primaryConstructor?.valueParameters?.filter { it.hasValOrVar() }?.forEachBetween(
                    forItem = {
                        -it.nameIdentifier
                        -"=\\(String(kotlin: self."
                        -it.nameIdentifier
                        -"))"
                    },
                    between = { -", " }
                )
                -')'
                -'"'
                -" }\n"
            }

            //Generate property list
            if(typedRule.hasDatabaseModelAnnotation) {
                if(typedRule.typeParameters.isNotEmpty()) {
                    typedRule.primaryConstructor?.valueParameters?.forEach {
                        -"public static var "
                        -it.nameIdentifier
                        -"Prop: PropertyIterableProperty<"
                        -typedRule.nameIdentifier
                        -", "
                        -it.typeReference
                        -"> { return PropertyIterableProperty(name: \""
                        -it.nameIdentifier
                        -"\", path: \\."
                        -it.nameIdentifier
                        -", setCopy: { (this, value) in this.copy("
                        -it.nameIdentifier
                        -": value) })"
                        -"}\n"
                    }
                    -"public static var properties: Array<PartialPropertyIterableProperty<"
                    -swiftTopLevelNameElement(typedRule)
                    -">> { return ["
                    typedRule.primaryConstructor?.valueParameters?.filter { it.hasValOrVar() }?.forEachBetween(
                        forItem = {
                            -it.nameIdentifier
                            -"Prop"
                        },
                        between = { -", " }
                    )
                    -"] }\n"
                } else {
                    typedRule.primaryConstructor?.valueParameters?.forEach {
                        -"public static let "
                        -it.nameIdentifier
                        -"Prop: PropertyIterableProperty<"
                        -typedRule.nameIdentifier
                        -", "
                        -it.typeReference
                        -"> = PropertyIterableProperty(name: \""
                        -it.nameIdentifier
                        -"\", path: \\."
                        -it.nameIdentifier
                        -", setCopy: { (this, value) in this.copy("
                        -it.nameIdentifier
                        -": value) })"
                        -"\n"
                    }
                    -"public static let properties: Array<PartialPropertyIterableProperty<"
                    -swiftTopLevelNameElement(typedRule)
                    -">> = ["
                    typedRule.primaryConstructor?.valueParameters?.filter { it.hasValOrVar() }?.forEachBetween(
                        forItem = {
                            -it.nameIdentifier
                            -"Prop"
                        },
                        between = { -", " }
                    )
                    -"]\n"
                }
                -"public static var anyProperties: Array<AnyPropertyIterableProperty> { return properties.map { \$0 } }\n"
            }

            //Generate copy(..)
            -"public func copy("
            typedRule.primaryConstructor?.valueParameters?.filter { it.hasValOrVar() }?.forEachBetween(
                forItem = {
                    if ((it.resolvedValueParameter as? ValueParameterDescriptor)?.useName == false) {
                        -"_ "
                    }
                    -it.nameIdentifier
                    -": "
                    -it.typeReference
                    -"? = "
                    if (it.typeReference?.resolvedType?.isMarkedNullable == true && it.typeReference?.resolvedType !is TypeParameterDescriptor) {
                        -".some(nil)"
                    } else {
                        -"nil"
                    }
                },
                between = { -", " }
            )
            -") -> "
            -swiftTopLevelNameElement(typedRule)
            typedRule.typeParameterList?.let {
                -'<'
                it.parameters.forEachBetween(
                    forItem = { -it.name },
                    between = { -", " }
                )
                -'>'
            }
            -" { return "
            -swiftTopLevelNameElement(typedRule)
            -"("
            typedRule.primaryConstructor?.valueParameters?.filter { it.hasValOrVar() }?.forEachBetween(
                forItem = {
                    if ((it.resolvedValueParameter as? ValueParameterDescriptor)?.useName != false) {
                        -it.nameIdentifier
                        -": "
                    }
                    if (it.typeReference?.resolvedType?.isMarkedNullable == true && it.typeReference?.resolvedType !is TypeParameterDescriptor) {
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

        -"}"
        typedRule.runPostActions()
    }

    handle<KtSuperExpression> {
        -"super"
    }

    handle<KtObjectDeclaration> {
        -(typedRule.swiftVisibility() ?: "public")
        -' '
        writeClassHeader(typedRule)
        -" {\n"
        writeProtocolAssociatedtypeImpl(typedRule)
        run {
            val c = typedRule.resolvedClass ?: return@run
            val s = c.getSuperClassNotAny() ?: return@run
            val sc = s.unsubstitutedPrimaryConstructor ?: return@run
            val cc = c.unsubstitutedPrimaryConstructor ?: return@run
            if (sc.valueParameters.size != cc.valueParameters.size) return@run
            if (sc.valueParameters.zip(cc.valueParameters).all {
                    it.first.name.asString() == it.second.name.asString()
                            && it.first.type.fqNameWithoutTypeArgs == it.second.type.fqNameWithoutTypeArgs
                }) {
                -"override "
            }
        }
        handleConstructor(this, null, this@registerClass)
        -"public static let INSTANCE = "
        -swiftTopLevelNameElement(typedRule)
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
        throw IllegalStateException("Object literals not supported in Swift!")
    }

    //Enums
    handle<KtClass>(
        condition = { typedRule.isEnum() },
        priority = 10,
        action = {
            -(typedRule.swiftVisibility() ?: "public")
            -" enum "
            -swiftTopLevelNameElement(typedRule)
            -": KotlinEnum, Codable, Hashable, Comparable {\n"
            for (entry in typedRule.body?.enumEntries ?: listOf()) {
                -"case "
                -entry.nameIdentifier
                -"\n"
            }
            -"\npublic static let caseNames = ["
            (typedRule.body?.enumEntries ?: listOf()).forEachBetween(
                forItem =  {
                    -'"'
                    -it.nameIdentifier
                    -'"'
                },
                between = { -", " }
            )
            -"]\n"
            //Constructor properties
            typedRule.primaryConstructorParameters
                .filter { it.hasValOrVar() }
                .forEach { prop ->
                    -"private static let "
                    -prop.nameIdentifier
                    -"Values: Dictionary<"
                    -swiftTopLevelNameElement(typedRule)
                    -", "
                    -prop.typeReference
                    -"> = [\n"
                    typedRule.body?.enumEntries?.forEachBetween(
                        forItem = { entry ->
                            -"."
                            -entry.nameIdentifier
                            -": "
                            -entry.initializerList?.initializers
                                ?.find { it is KtSuperTypeCallEntry }
                                ?.resolvedCall?.valueArguments?.entries
                                ?.find { it.key.name.asString() == prop.name }
                                ?.let {
                                    it.value.arguments.firstOrNull()?.let {
                                        -it.getArgumentExpression()
                                    } ?: -prop.defaultValue
                                } ?: -"TODO()"
                        },
                        between = { -",\n" }
                    )
                    -"\n]\n"
                    -(prop.swiftVisibility() ?: "public")
                    -" var "
                    -prop.nameIdentifier
                    -": "
                    -prop.typeReference
                    -" {\n"
                    -"return "
                    -swiftTopLevelNameElement(typedRule)
                    -"."
                    -prop.nameIdentifier
                    -"Values[self] ?? "
                    -swiftTopLevelNameElement(typedRule)
                    -"."
                    -prop.nameIdentifier
                    -"Values[."
                    -typedRule.body?.enumEntries?.firstOrNull()?.name
                    -"]!\n"
                    -"}\n"
                }
            //Functions
            typedRule.body
                ?.functions
                ?.forEach { func ->
                    -VirtualFunction(
                        name = func.name!!,
                        resolvedFunction = func.resolvedFunction,
                        typeParameters = func.typeParameters,
                        valueParameters = func.valueParameters,
                        returnType = func.typeReference ?: func.resolvedFunction?.returnType ?: "Void",
                        body = if (func.hasModifier(KtTokens.OPEN_KEYWORD) || func.hasModifier(KtTokens.ABSTRACT_KEYWORD)) {
                            { ->
                                -"{ \nswitch self {\n"
                                typedRule.body?.enumEntries?.mapNotNull { entry ->
                                    val matching =
                                        entry.body?.functions?.find { it.name == func.name } ?: return@mapNotNull null
                                    return@mapNotNull entry to matching
                                }?.forEach { (entry, matching) ->
                                    -"case ."
                                    -entry.nameIdentifier
                                    -":\n"
                                    when (val body = matching.bodyExpression) {
                                        is KtBlockExpression -> {
                                            -body.allChildren.drop(1).toList().dropLast(1)
                                        }
                                        else -> {
                                            -"return "
                                            -body
                                        }
                                    }
                                    -"\n"
                                }
                                -"default:\n"
                                when (val body = func.bodyExpression) {
                                    is KtBlockExpression -> {
                                        -body.allChildren.drop(1).toList().dropLast(1)
                                    }
                                    null -> -"TODO()"
                                    else -> {
                                        -"return "
                                        -body
                                    }
                                }
                                -"\n}\n}\n"
                            }
                        } else func.bodyExpression as Any
                    )
                    -"\n"
                }
            //Open properties
            //Abstract properties
            //Closed properties

            typedRule.body?.children?.filter { it is KtObjectDeclaration }?.forEach {
                -it
            }

            -"}\n"
        }
    )

    handle<KtSecondaryConstructor> {
        -(typedRule.swiftVisibility() ?: "public")
        -" convenience init("
        typedRule.valueParameters.forEachBetween(
            forItem = { -it },
            between = { -", " }
        )
        -") {\n"
        -"self.init("
        val dg = typedRule.getDelegationCall()
        val resSuper = dg.resolvedCall
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
                        if (it.key.useName) {
                            -it.key.name.asString()
                            -": "
                        }
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

    handle<KtNameReferenceExpression>(
        condition = {
            val call = typedRule.resolvedCall ?: return@handle false
            if (call.resultingDescriptor !is FakeCallableDescriptorForObject) return@handle false
            (call.getReturnType().constructor.declarationDescriptor as? ClassDescriptor)?.kind.let { it != ClassKind.ENUM_CLASS && it != ClassKind.ENUM_ENTRY }
        },
        priority = 1000,
        action = {
            doSuper()
            -".INSTANCE"
        }
    )
    handle<KtDotQualifiedExpression>(
        condition = {
            val p = (typedRule.parent as? KtDotQualifiedExpression) ?: return@handle false
            if (p.receiverExpression != typedRule) return@handle false
            val rec = p.resolvedCall?.let { it.dispatchReceiver ?: it.extensionReceiver } as? ClassValueReceiver
                ?: return@handle false
            (rec.type.constructor.declarationDescriptor as? ClassDescriptor)?.kind.let { it != ClassKind.ENUM_CLASS && it != ClassKind.ENUM_ENTRY }
        },
        priority = 1_000,
        action = {
            val p = typedRule.parent as KtDotQualifiedExpression
            val r = p.resolvedCall!!.let { it.dispatchReceiver ?: it.extensionReceiver } as ClassValueReceiver
            val actual = r.type.constructor.declarationDescriptor
            val written = r.classQualifier.descriptor
            doSuper()
            if (actual?.swiftTopLevelMessedUp != true && actual != written) {
                -"."
                -actual?.name?.asString()
            }
            -".INSTANCE"
        }
    )
    handle<KtNameReferenceExpression>(
        condition = {
            val p = (typedRule.parent as? KtDotQualifiedExpression) ?: return@handle false
            if (p.receiverExpression != typedRule) return@handle false
            val rec = p.resolvedCall?.let { it.dispatchReceiver ?: it.extensionReceiver } as? ClassValueReceiver
                ?: return@handle false
            (rec.type.constructor.declarationDescriptor as? ClassDescriptor)?.kind.let { it != ClassKind.ENUM_CLASS && it != ClassKind.ENUM_ENTRY }
        },
        priority = 1_000,
        action = {
            val p = typedRule.parent as KtDotQualifiedExpression
            val r = p.resolvedCall!!.let { it.dispatchReceiver ?: it.extensionReceiver } as ClassValueReceiver
            val actual = r.type.constructor.declarationDescriptor
            val written = r.classQualifier.descriptor
            doSuper()
            if (actual?.swiftTopLevelMessedUp != true && actual != written) {
                -"."
                -actual?.name?.asString()
            }
            -".INSTANCE"
        }
    )

    handle<KtNameReferenceExpression>(
        condition = {
            val descriptor =
                typedRule.resolvedReferenceTarget as? ClassDescriptor
                    ?: return@handle false
            descriptor.swiftTopLevelMessedUp
        },
        priority = 900,
        action = {
            val nameRef = typedRule
            val descriptor = nameRef.resolvedReferenceTarget as ClassDescriptor
            -descriptor.swiftTopLevelName
        }
    )
    handle<KtDotQualifiedExpression>(
        condition = {
            val descriptor =
                (typedRule.selectorExpression as? KtNameReferenceExpression)?.resolvedReferenceTarget as? ClassDescriptor
                    ?: return@handle false
            descriptor.swiftTopLevelMessedUp
        },
        priority = 900,
        action = {
            val nameRef = typedRule.selectorExpression as KtNameReferenceExpression
            val descriptor = nameRef.resolvedReferenceTarget as ClassDescriptor
            -descriptor.swiftTopLevelName

            run instanceBlock@{
                val call = typedRule.resolvedCall ?: return@instanceBlock
                if (call.resultingDescriptor !is FakeCallableDescriptorForObject) return@instanceBlock
                if ((call.getReturnType().constructor.declarationDescriptor as? ClassDescriptor)?.kind != ClassKind.ENUM_CLASS) {
                    -".INSTANCE"
                }
            }
        }
    )
    handle<KtDotQualifiedExpression>(
        condition = {
            val descriptor =
                (((typedRule.selectorExpression as? KtCallExpression)?.calleeExpression as? KtNameReferenceExpression)?.resolvedReferenceTarget as? ConstructorDescriptor)?.constructedClass
                    ?: return@handle false
            descriptor.swiftTopLevelMessedUp
        },
        priority = 900,
        action = {
            val callExp = typedRule.selectorExpression as KtCallExpression
            val constructor =
                (callExp.calleeExpression as KtNameReferenceExpression).resolvedReferenceTarget as ConstructorDescriptor
            val descriptor = constructor.constructedClass
            -descriptor.swiftTopLevelName
            -ArgumentsList(
                on = constructor,
                resolvedCall = callExp.resolvedCall!!
            )
        }
    )

}

private fun <T : KtClassOrObject> handleConstructor(
    contextByType: KotlinTranslator<SwiftFileEmitter>.ContextByType<T>,
    parentClassName: Any?,
    swiftTranslator: SwiftTranslator
) = with(swiftTranslator) {
    with(contextByType) {
        val isInner = (typedRule as? KtClass)?.isInner() == true
        val isEnum = (typedRule as? KtClass)?.isEnum() == true
        -(contextByType.typedRule.primaryConstructor?.swiftVisibility() ?: "public")
        -" init("
        contextByType.typedRule.primaryConstructor?.let { cons ->
            (if (isInner) {
                listOf(listOf("parentThis: ", parentClassName)) + cons.valueParameters
            } else {
                cons.valueParameters
            }).forEachBetween(
                forItem = { -it },
                between = { -", " }
            )
        } ?: contextByType.run {
            if (isInner) {
                -"parentThis: "
                -parentClassName
            } else Unit
        }
        -") {\n"
        if (isInner) {
            -"self.parentThis = parentThis;\n"
        }

        //Parameter assignment first
        contextByType.typedRule.primaryConstructor?.let { cons ->
            cons.valueParameters.asSequence().filter { it.hasValOrVar() }.forEach {
                -"self."
                if (it.resolvedPrimaryConstructorParameter?.hasSwiftOverride == true) {
                    -'_'
                    -it.nameIdentifier
                } else {
                    -it.nameIdentifier
                }
                -" = "
                -it.nameIdentifier
                -"\n"
            }
        }

        //If used by future initializers directly, create inbetween.
        val usedInInits = listOfNotNull(
            contextByType.typedRule.primaryConstructor?.valueParameters?.map { it.defaultValue },
            contextByType.typedRule.body?.children?.mapNotNull { (it as? KtProperty)?.initializer }
        )
            .asSequence()
            .flatMap { it.asSequence() }
            .filterNotNull()
            .flatMap {
                val matches = HashSet<PropertyDescriptor>()
                fun check(it: PsiElement) {
                    if (it is KtNameReferenceExpression) {
                        val prop = it.resolvedReferenceTarget as? PropertyDescriptor ?: return
                        if (prop.containingDeclaration == contextByType.typedRule.resolvedClass) {
                            matches.add(prop)
                        }
                    }
                    if (it !is KtFunctionLiteral) {
                        it.allChildren.forEach { check(it) }
                    }
                }
                check(it)
                matches.asSequence()
            }
            .toSet()

        //If uses self capture, delay and mark with !
        val postSuper = ArrayList<() -> Unit>()

        //Then, in order, variable initializers
        suppressReceiverAddition = true
        contextByType.typedRule.body?.children?.forEach {
            when (it) {
                is KtProperty -> {
                    it.initializer?.let { init ->
                        if (swiftTranslator.capturesSelf(init, contextByType.typedRule.resolvedClass)) {
                            postSuper += {
                                -"self."
                                if (it.resolvedProperty?.hasSwiftBacking == true) {
                                    -'_'
                                }
                                -it.nameIdentifier
                                -" = "
                                -init
                                -"\n"
                            }
                        } else if (it.resolvedProperty in usedInInits) {
                            -"let "
                            -it.nameIdentifier
                            -": "
                            -(it.typeReference ?: it.resolvedProperty?.type ?: it.resolvedVariable?.type)
                            -" = "
                            -init
                            -"\n"
                            -"self."
                            if (it.resolvedProperty?.hasSwiftBacking == true) {
                                -'_'
                            }
                            -it.nameIdentifier
                            -" = "
                            -it.nameIdentifier
                            -"\n"
                        } else {
                            -"self."
                            if (it.resolvedProperty?.hasSwiftBacking == true) {
                                -'_'
                            }
                            -it.nameIdentifier
                            -" = "
                            -init
                            -"\n"
                        }
                    }
                }
            }
        }
        suppressReceiverAddition = false
        //Then super
        val superEntries =
            (typedRule as? KtEnumEntry)?.initializerList?.initializers?.mapNotNull { it as? KtSuperTypeCallEntry }
                ?: contextByType.typedRule.superTypeListEntries.mapNotNull { it as? KtSuperTypeCallEntry }
        superEntries
            .takeUnless { it.isEmpty() }
            ?.firstOrNull()?.let {
                -"super.init("
                val resolvedCall = it.resolvedCall
                it.resolvedCall?.valueArguments?.entries
                    ?.sortedBy { it.key.index }
                    ?.filter { it.value.arguments.isNotEmpty() }
                    ?.forEachBetween(
                        forItem = {
                            val args = it.value.arguments
                            when (args.size) {
                                0 -> {
                                }
                                1 -> {
                                    if ((resolvedCall?.candidateDescriptor as? ConstructorDescriptor)?.hasJavaOriginInHierarchy() != true) {
                                        if (it.key.useName) {
                                            -it.key.name.asString().safeSwiftIdentifier()
                                            -": "
                                        }
                                    }
                                    -args[0].getArgumentExpression()
                                }
                                else -> {
                                    -it.key.name.asString()
                                    -": ["
                                    args.forEachBetween(
                                        forItem = { -it.getArgumentExpression() },
                                        between = { -", " }
                                    )
                                    -"]"
                                }
                            }
                        },
                        between = { -", " }
                    )
                -")\n"
            }
        -"//Necessary properties should be initialized now\n"
        postSuper.forEach { it() }
        //Then, in order, anon initializers
        contextByType.typedRule.body?.children?.forEach {
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
    }
}

private fun KtParameter.jsonName(): String {
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


fun KtClass.isSimpleEnum() = isEnum() && body?.enumEntries?.all { !it.hasInitializer() && it.body == null } == true

val KtClassOrObject.isOpen: Boolean get() = hasModifier(KtTokens.OPEN_KEYWORD) || hasModifier(KtTokens.ABSTRACT_KEYWORD) || hasModifier(KtTokens.SEALED_KEYWORD)
val KtClassOrObject.shouldGenerateCodable: Boolean get() = implementsKotlinCodable || hasCodableAnnotation
val KtClassOrObject.implementsKotlinCodable: Boolean get() = superTypeListEntries
    .mapNotNull { it as? KtSuperTypeEntry }
    .any { it.typeReference?.resolvedType?.fqNameWithoutTypeArgs == "com.lightningkite.khrysalis.Codable" }
val KtClassOrObject.hasCodableAnnotation: Boolean get() = annotationEntries.any { it.resolvedAnnotation?.type?.fqNameWithoutTypeArgs == "kotlinx.serialization.Serializable" && it.valueArguments.isEmpty()}
val KtClassOrObject.hasDatabaseModelAnnotation: Boolean get() = annotationEntries.any {
    it.resolvedAnnotation?.type?.fqNameWithoutTypeArgs?.endsWith("DatabaseModel") == true
}
