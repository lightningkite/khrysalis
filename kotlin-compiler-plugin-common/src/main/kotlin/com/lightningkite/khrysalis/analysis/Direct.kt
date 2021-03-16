package com.lightningkite.khrysalis.analysis

import com.lightningkite.khrysalis.util.simpleFqName
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.model.PartialCallContainer
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.types.DeferredType
import org.jetbrains.kotlin.util.Box

lateinit var bindingContext: BindingContext

val KtNamedDeclaration.simpleFqName: String get() = this.resolvedDeclarationToDescriptor?.simpleFqName ?: ""

val KtAnnotationEntry.resolvedAnnotation: org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor?
    get() = bindingContext[BindingContext.ANNOTATION, this]
val KtExpression.resolvedCompileTimeValue: org.jetbrains.kotlin.resolve.constants.CompileTimeConstant<*>?
    get() = bindingContext[BindingContext.COMPILE_TIME_VALUE, this]
val KtTypeReference.resolvedType: org.jetbrains.kotlin.types.KotlinType?
    get() = bindingContext[BindingContext.TYPE, this]
val KtTypeReference.resolvedAbbreviatedType: org.jetbrains.kotlin.types.KotlinType?
    get() = bindingContext[BindingContext.ABBREVIATED_TYPE, this]
val KtExpression.resolvedExpressionTypeInfo: org.jetbrains.kotlin.types.expressions.KotlinTypeInfo?
    get() = bindingContext[BindingContext.EXPRESSION_TYPE_INFO, this]
val KtExpression.resolvedDataFlowInfoBefore: org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowInfo?
    get() = bindingContext[BindingContext.DATA_FLOW_INFO_BEFORE, this]
val KtExpression.resolvedExpectedExpressionType: org.jetbrains.kotlin.types.KotlinType?
    get() = bindingContext[BindingContext.EXPECTED_EXPRESSION_TYPE, this]
val KtElement.resolvedExpressionEffects: org.jetbrains.kotlin.contracts.model.Computation?
    get() = bindingContext[BindingContext.EXPRESSION_EFFECTS, this]
val KtElement.resolvedContractNotAllowed: Boolean?
    get() = bindingContext[BindingContext.CONTRACT_NOT_ALLOWED, this]
val KtElement.resolvedIsContractDeclarationBlock: Boolean?
    get() = bindingContext[BindingContext.IS_CONTRACT_DECLARATION_BLOCK, this]
val KtFunction.resolvedExpectedReturnType: org.jetbrains.kotlin.types.KotlinType?
    get() = bindingContext[BindingContext.EXPECTED_RETURN_TYPE, this]
val KtExpression.resolvedDataflowInfoAfterCondition: org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowInfo?
    get() = bindingContext[BindingContext.DATAFLOW_INFO_AFTER_CONDITION, this]
val VariableDescriptor.resolvedBoundInitializerValue: org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowValue?
    get() = bindingContext[BindingContext.BOUND_INITIALIZER_VALUE, this]
val KtExpression.resolvedLeakingThis: org.jetbrains.kotlin.cfg.LeakingThisDescriptor?
    get() = bindingContext[BindingContext.LEAKING_THIS, this]
val KtParameter.resolvedUnusedMainParameter: Boolean?
    get() = bindingContext[BindingContext.UNUSED_MAIN_PARAMETER, this]
val VariableDescriptor.resolvedUnusedDelegatedPropertyOperatorParameter: Boolean?
    get() = bindingContext[BindingContext.UNUSED_DELEGATED_PROPERTY_OPERATOR_PARAMETER, this]
val KtExpression.resolvedQualifier: org.jetbrains.kotlin.resolve.scopes.receivers.Qualifier?
    get() = bindingContext[BindingContext.QUALIFIER, this]
val KtExpression.resolvedDoubleColonLhs: org.jetbrains.kotlin.types.expressions.DoubleColonLHS?
    get() = bindingContext[BindingContext.DOUBLE_COLON_LHS, this]
val KtSuperExpression.resolvedThisTypeForSuperExpression: org.jetbrains.kotlin.types.KotlinType?
    get() = bindingContext[BindingContext.THIS_TYPE_FOR_SUPER_EXPRESSION, this]
val KtReferenceExpression.resolvedReferenceTarget: DeclarationDescriptor?
    get() = bindingContext[BindingContext.REFERENCE_TARGET, this]
val KtReferenceExpression.resolvedShortReferenceToCompanionObject: ClassifierDescriptorWithTypeParameters?
    get() = bindingContext[BindingContext.SHORT_REFERENCE_TO_COMPANION_OBJECT, this]
val Call.resolvedResolvedCall: ResolvedCall<*>?
    get() = bindingContext[BindingContext.RESOLVED_CALL, this]
val Call.resolvedOnlyResolvedCall: PartialCallContainer?
    get() = bindingContext[BindingContext.ONLY_RESOLVED_CALL, this]
val Call.resolvedPartialCallResolutionContext: org.jetbrains.kotlin.resolve.calls.context.BasicCallResolutionContext?
    get() = bindingContext[BindingContext.PARTIAL_CALL_RESOLUTION_CONTEXT, this]
val KtExpression.resolvedDelegateExpressionToProvideDelegateCall: Call?
    get() = bindingContext[BindingContext.DELEGATE_EXPRESSION_TO_PROVIDE_DELEGATE_CALL, this]
val Call.resolvedTailRecursionCall: org.jetbrains.kotlin.cfg.TailRecursionKind?
    get() = bindingContext[BindingContext.TAIL_RECURSION_CALL, this]
val KtElement.resolvedConstraintSystemCompleter: org.jetbrains.kotlin.resolve.calls.inference.ConstraintSystemCompleter?
    get() = bindingContext[BindingContext.CONSTRAINT_SYSTEM_COMPLETER, this]
val KtElement.resolvedCall get() = this.getResolvedCall(bindingContext)
val KtExpression.resolvedAmbiguousReferenceTarget: Collection<DeclarationDescriptor>?
    get() = bindingContext[BindingContext.AMBIGUOUS_REFERENCE_TARGET, this]
val KtExpression.resolvedLoopRangeIteratorResolvedCall: ResolvedCall<FunctionDescriptor>?
    get() = bindingContext[BindingContext.LOOP_RANGE_ITERATOR_RESOLVED_CALL, this]
val KtExpression.resolvedLoopRangeHasNextResolvedCall: ResolvedCall<FunctionDescriptor>?
    get() = bindingContext[BindingContext.LOOP_RANGE_HAS_NEXT_RESOLVED_CALL, this]
val KtExpression.resolvedLoopRangeNextResolvedCall: ResolvedCall<FunctionDescriptor>?
    get() = bindingContext[BindingContext.LOOP_RANGE_NEXT_RESOLVED_CALL, this]
val Call.resolvedEnclosingSuspendFunctionForSuspendFunctionCall: FunctionDescriptor?
    get() = bindingContext[BindingContext.ENCLOSING_SUSPEND_FUNCTION_FOR_SUSPEND_FUNCTION_CALL, this]
val VariableAccessorDescriptor.resolvedDelegatedPropertyResolvedCall: ResolvedCall<FunctionDescriptor>?
    get() = bindingContext[BindingContext.DELEGATED_PROPERTY_RESOLVED_CALL, this]
val VariableAccessorDescriptor.resolvedDelegatedPropertyCall: Call?
    get() = bindingContext[BindingContext.DELEGATED_PROPERTY_CALL, this]
val VariableDescriptorWithAccessors.resolvedProvideDelegateResolvedCall: ResolvedCall<FunctionDescriptor>?
    get() = bindingContext[BindingContext.PROVIDE_DELEGATE_RESOLVED_CALL, this]
val VariableDescriptorWithAccessors.resolvedProvideDelegateCall: Call?
    get() = bindingContext[BindingContext.PROVIDE_DELEGATE_CALL, this]
val KtDestructuringDeclarationEntry.resolvedComponentResolvedCall: ResolvedCall<FunctionDescriptor>?
    get() = bindingContext[BindingContext.COMPONENT_RESOLVED_CALL, this]
val KtExpression.resolvedIndexedLvalueGet: ResolvedCall<FunctionDescriptor>?
    get() = bindingContext[BindingContext.INDEXED_LVALUE_GET, this]
val KtExpression.resolvedIndexedLvalueSet: ResolvedCall<FunctionDescriptor>?
    get() = bindingContext[BindingContext.INDEXED_LVALUE_SET, this]
val KtCollectionLiteralExpression.resolvedCollectionLiteralCall: ResolvedCall<FunctionDescriptor>?
    get() = bindingContext[BindingContext.COLLECTION_LITERAL_CALL, this]
val KtExpression.resolvedSmartcast: org.jetbrains.kotlin.resolve.calls.smartcasts.ExplicitSmartCasts?
    get() = bindingContext[BindingContext.SMARTCAST, this]
val KtExpression.resolvedSmartcastNull: Boolean?
    get() = bindingContext[BindingContext.SMARTCAST_NULL, this]
val KtExpression.resolvedImplicitReceiverSmartcast: org.jetbrains.kotlin.resolve.calls.smartcasts.ImplicitSmartCasts?
    get() = bindingContext[BindingContext.IMPLICIT_RECEIVER_SMARTCAST, this]
val KtWhenExpression.resolvedExhaustiveWhen: Boolean?
    get() = bindingContext[BindingContext.EXHAUSTIVE_WHEN, this]
val KtWhenExpression.resolvedImplicitExhaustiveWhen: Boolean?
    get() = bindingContext[BindingContext.IMPLICIT_EXHAUSTIVE_WHEN, this]
val KtElement.resolvedLexicalScope: org.jetbrains.kotlin.resolve.scopes.LexicalScope?
    get() = bindingContext[BindingContext.LEXICAL_SCOPE, this]
val ScriptDescriptor.resolvedScriptScope: org.jetbrains.kotlin.resolve.scopes.LexicalScope?
    get() = bindingContext[BindingContext.SCRIPT_SCOPE, this]
val KtExpression.resolvedVariableReassignment: Boolean?
    get() = bindingContext[BindingContext.VARIABLE_REASSIGNMENT, this]
val ValueParameterDescriptor.resolvedAutoCreatedIt: Boolean?
    get() = bindingContext[BindingContext.AUTO_CREATED_IT, this]
val KtExpression.resolvedProcessed: Boolean?
    get() = bindingContext[BindingContext.PROCESSED, this]
val KtElement.resolvedUsedAsExpression: Boolean?
    get() = bindingContext[BindingContext.USED_AS_EXPRESSION, this]
val KtElement.resolvedUsedAsResultOfLambda: Boolean?
    get() = bindingContext[BindingContext.USED_AS_RESULT_OF_LAMBDA, this]
val VariableDescriptor.resolvedCapturedInClosure: org.jetbrains.kotlin.types.expressions.CaptureKind?
    get() = bindingContext[BindingContext.CAPTURED_IN_CLOSURE, this]
val KtDeclaration.resolvedPreliminaryVisitor: org.jetbrains.kotlin.types.expressions.PreliminaryDeclarationVisitor?
    get() = bindingContext[BindingContext.PRELIMINARY_VISITOR, this]
val Box<DeferredType>.resolvedDeferredType: Boolean?
    get() = bindingContext[BindingContext.DEFERRED_TYPE, this]
val PropertyDescriptor.resolvedBackingFieldRequired: Boolean?
    get() = bindingContext[BindingContext.BACKING_FIELD_REQUIRED, this]
val PropertyDescriptor.resolvedIsUninitialized: Boolean?
    get() = bindingContext[BindingContext.IS_UNINITIALIZED, this]
val PropertyDescriptor.resolvedMustBeLateinit: Boolean?
    get() = bindingContext[BindingContext.MUST_BE_LATEINIT, this]
val KtLambdaExpression.resolvedBlock: Boolean?
    get() = bindingContext[BindingContext.BLOCK, this]
val KtClassOrObject.resolvedClass: ClassDescriptor?
    get() = bindingContext[BindingContext.CLASS, this]
val KtScript.resolvedScript: ScriptDescriptor?
    get() = bindingContext[BindingContext.SCRIPT, this]
val KtTypeParameter.resolvedTypeParameter: TypeParameterDescriptor?
    get() = bindingContext[BindingContext.TYPE_PARAMETER, this]
val KtFunction.resolvedFunction: SimpleFunctionDescriptor?
    get() = bindingContext[BindingContext.FUNCTION, this]
val KtConstructor<*>.resolvedConstructor: ConstructorDescriptor?
    get() = bindingContext[BindingContext.CONSTRUCTOR, this]
val ConstructorDescriptor.resolvedConstructorResolvedDelegationCall: ResolvedCall<ConstructorDescriptor>?
    get() = bindingContext[BindingContext.CONSTRUCTOR_RESOLVED_DELEGATION_CALL, this]
val KtProperty.resolvedVariable: VariableDescriptor?
    get() = bindingContext[BindingContext.VARIABLE, this]
val KtProperty.resolvedProperty: PropertyDescriptor?
    get() = bindingContext[BindingContext.VARIABLE, this] as? PropertyDescriptor
val KtParameter.resolvedValueParameter: VariableDescriptor?
    get() = bindingContext[BindingContext.VALUE_PARAMETER, this]
val KtPropertyAccessor.resolvedPropertyAccessor: PropertyAccessorDescriptor?
    get() = bindingContext[BindingContext.PROPERTY_ACCESSOR, this]
val KtParameter.resolvedPrimaryConstructorParameter: PropertyDescriptor?
    get() = bindingContext[BindingContext.PRIMARY_CONSTRUCTOR_PARAMETER, this]
val KtTypeAlias.resolvedTypeAlias: TypeAliasDescriptor?
    get() = bindingContext[BindingContext.TYPE_ALIAS, this]
val PsiElement.resolvedDeprecatedShortNameAccess: Boolean?
    get() = bindingContext[BindingContext.DEPRECATED_SHORT_NAME_ACCESS, this]
val KtReferenceExpression.resolvedLabelTarget: org.jetbrains.kotlin.com.intellij.psi.PsiElement?
    get() = bindingContext[BindingContext.LABEL_TARGET, this]
val KtReferenceExpression.resolvedAmbiguousLabelTarget: Collection<org.jetbrains.kotlin.com.intellij.psi.PsiElement>?
    get() = bindingContext[BindingContext.AMBIGUOUS_LABEL_TARGET, this]
val ValueParameterDescriptor.resolvedValueParameterAsProperty: PropertyDescriptor?
    get() = bindingContext[BindingContext.VALUE_PARAMETER_AS_PROPERTY, this]
val ValueParameterDescriptor.resolvedDataClassComponentFunction: FunctionDescriptor?
    get() = bindingContext[BindingContext.DATA_CLASS_COMPONENT_FUNCTION, this]
val ClassDescriptor.resolvedDataClassCopyFunction: FunctionDescriptor?
    get() = bindingContext[BindingContext.DATA_CLASS_COPY_FUNCTION, this]
val org.jetbrains.kotlin.name.FqNameUnsafe.resolvedFqnameToClassDescriptor: ClassDescriptor?
    get() = bindingContext[BindingContext.FQNAME_TO_CLASS_DESCRIPTOR, this]
val org.jetbrains.kotlin.name.FqName.resolvedPackageToFiles: Collection<KtFile>?
    get() = bindingContext[BindingContext.PACKAGE_TO_FILES, this]
val KtBinaryExpressionWithTypeRHS.resolvedCastTypeUsedAsExpectedType: Boolean?
    get() = bindingContext[BindingContext.CAST_TYPE_USED_AS_EXPECTED_TYPE, this]
val KtFunction.resolvedNewInferenceLambdaInfo: org.jetbrains.kotlin.resolve.calls.tower.KotlinResolutionCallbacksImpl.LambdaInfo?
    get() = bindingContext[BindingContext.NEW_INFERENCE_LAMBDA_INFO, this]
val KtExpression.resolvedPrimitiveNumericComparisonInfo: org.jetbrains.kotlin.resolve.checkers.PrimitiveNumericComparisonInfo?
    get() = bindingContext[BindingContext.PRIMITIVE_NUMERIC_COMPARISON_INFO, this]
val KtExpression.resolvedNewInferenceCatchExceptionParameter: org.jetbrains.kotlin.com.intellij.openapi.util.Ref<VariableDescriptor>?
    get() = bindingContext[BindingContext.NEW_INFERENCE_CATCH_EXCEPTION_PARAMETER, this]
val PsiElement.resolvedDeclarationToDescriptor: DeclarationDescriptor?
    get() = bindingContext[BindingContext.DECLARATION_TO_DESCRIPTOR, this]