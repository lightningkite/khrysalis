// Generated by Khrysalis TypeScript converter - this file will be overwritten.
// File: views/Form.shared.kt
// Package: com.lightningkite.khrysalis.views
// FQImport: com.lightningkite.khrysalis.views.UntypedFormField.validation TS validation
// FQImport: com.lightningkite.khrysalis.views.Form.fields TS fields
// FQImport: com.lightningkite.khrysalis.views.Form.field.field TS field
// FQImport: com.lightningkite.khrysalis.views.notNull.T TS T
// FQImport: com.lightningkite.khrysalis.views.Form.Companion.xMustMatchY TS xMustMatchY
// FQImport: com.lightningkite.khrysalis.views.Form.fieldFromProperty.field TS field
// FQImport: com.lightningkite.khrysalis.views.StringResource TS StringResource
// FQImport: com.lightningkite.khrysalis.views.Form.field TS field
// FQImport: com.lightningkite.khrysalis.views.Form.Companion.xIsRequired TS xIsRequired
// FQImport: com.lightningkite.khrysalis.views.ViewStringRaw TS ViewStringRaw
// FQImport: com.lightningkite.khrysalis.views.matches.T TS T
// FQImport: com.lightningkite.khrysalis.views.FormValidationError SKIPPED due to same file
// FQImport: com.lightningkite.khrysalis.views.Form.runOrDialog.action TS action
// FQImport: com.lightningkite.khrysalis.views.FormValidationError TS FormValidationError
// FQImport: com.lightningkite.khrysalis.views.FormField.observable TS observable
// FQImport: com.lightningkite.khrysalis.views.Form.check TS check
// FQImport: com.lightningkite.khrysalis.views.Form.checkField TS checkField
// FQImport: com.lightningkite.khrysalis.views.Form.checkField.result TS result
// FQImport: com.lightningkite.khrysalis.views.Form.field.<anonymous>.untypedField TS untypedField
// FQImport: com.lightningkite.khrysalis.views.Form.fieldFromProperty.name TS name
// FQImport: com.lightningkite.khrysalis.observables.StandardObservableProperty TS StandardObservableProperty
// FQImport: com.lightningkite.khrysalis.views.Form.runOrDialog.<anonymous>.it TS it
// FQImport: com.lightningkite.khrysalis.observables.MutableObservableProperty TS MutableObservableProperty
// FQImport: com.lightningkite.khrysalis.views.Form.fieldFromProperty.<anonymous>.untypedField TS untypedField
// FQImport: kotlin.Boolean TS Boolean
// FQImport: com.lightningkite.khrysalis.views.FormField.T TS T
// FQImport: com.lightningkite.khrysalis.observables.MutableObservableProperty.value TS value
// FQImport: com.lightningkite.khrysalis.views.Form.field.obs TS obs
// FQImport: com.lightningkite.khrysalis.views.Form.checkField.field TS field
// FQImport: com.lightningkite.khrysalis.views.UntypedFormField SKIPPED due to same file
// FQImport: com.lightningkite.khrysalis.views.UntypedFormField TS UntypedFormField
// FQImport: com.lightningkite.khrysalis.views.ViewStringResource TS ViewStringResource
// FQImport: com.lightningkite.khrysalis.views.Form.check.<anonymous>.it TS it
// FQImport: com.lightningkite.khrysalis.views.FormValidationError.string TS string
// FQImport: com.lightningkite.khrysalis.views.FormField TS FormField
// FQImport: com.lightningkite.khrysalis.views.joinToViewString TS kotlinCollectionsListJoinToViewString
// FQImport: com.lightningkite.khrysalis.views.FormField.<set-value>.value TS value
// FQImport: com.lightningkite.khrysalis.views.Form.field.defaultValue TS defaultValue
// FQImport: com.lightningkite.khrysalis.views.Form.fieldFromProperty.T TS T
// FQImport: com.lightningkite.khrysalis.views.Form.field.name TS name
// FQImport: com.lightningkite.khrysalis.views.Form.runOrDialog.errors TS errors
// FQImport: com.lightningkite.khrysalis.views.UntypedFormField.error TS error
// FQImport: com.lightningkite.khrysalis.views.showDialog TS showDialog
// FQImport: com.lightningkite.khrysalis.views.Form.field.T TS T
// FQImport: kotlin.collections.isNotEmpty TS kotlinCollectionsCollectionIsNotEmpty
// FQImport: com.lightningkite.khrysalis.views.Form.fieldFromProperty.validation TS validation
// FQImport: com.lightningkite.khrysalis.views.ViewStringTemplate TS ViewStringTemplate
// FQImport: kotlin.text.isBlank TS kotlinCharSequenceIsBlank
// FQImport: com.lightningkite.khrysalis.views.Form.check.<anonymous>.result TS result
// FQImport: com.lightningkite.khrysalis.views.ViewString TS ViewString
// FQImport: com.lightningkite.khrysalis.views.Form.fieldFromProperty.property TS property
// FQImport: com.lightningkite.khrysalis.views.FormField.name TS name
// FQImport: com.lightningkite.khrysalis.views.matches.other TS other
// FQImport: com.lightningkite.khrysalis.views.FormField SKIPPED due to same file
// FQImport: com.lightningkite.khrysalis.views.Form.fieldFromProperty TS fieldFromProperty
// FQImport: com.lightningkite.khrysalis.observables.StandardObservableProperty.value TS value
// FQImport: com.lightningkite.khrysalis.views.unless.condition TS condition
// FQImport: com.lightningkite.khrysalis.views.Form.field.validation TS validation
import { StandardObservableProperty } from './../observables/StandardObservableProperty.shared'
import { ViewString, ViewStringRaw, ViewStringResource, ViewStringTemplate, kotlinCollectionsListJoinToViewString } from './Strings.shared'
import { MutableObservableProperty } from './../observables/MutableObservableProperty.shared'
import { StringResource } from './ResourceTypes.actual'
import { kotlinCharSequenceIsBlank } from './../kotlin/kotlin.text'
import { showDialog } from './showDialog.shared'
import { listFilterNotNull } from 'khrysalis/dist/Kotlin'

//! Declares com.lightningkite.khrysalis.views.FormValidationError
export class FormValidationError {
    public readonly field: UntypedFormField;
    public readonly _string: ViewString;
    public constructor(field: UntypedFormField, _string: ViewString) {
        this.field = field;
        this._string = _string;
    }
}

//! Declares com.lightningkite.khrysalis.views.UntypedFormField
export interface UntypedFormField {
    
    readonly name: ViewString;
    
    readonly untypedObservable: any;
    
    readonly validation: (a: UntypedFormField) => (ViewString | null);
    
    readonly error: StandardObservableProperty<(ViewString | null)>;
    
}
export class UntypedFormFieldDefaults {
}

//! Declares com.lightningkite.khrysalis.views.FormField
export class FormField<T> implements UntypedFormField {
    public static implementsInterfaceComLightningkiteKhrysalisViewsUntypedFormField = true;
    public readonly name: ViewString;
    public readonly observable: MutableObservableProperty<T>;
    public readonly validation:  (a: UntypedFormField) => (ViewString | null);
    public constructor(name: ViewString, observable: MutableObservableProperty<T>, validation:  (a: UntypedFormField) => (ViewString | null)) {
        this.name = name;
        this.observable = observable;
        this.validation = validation;
        this.error = new StandardObservableProperty(null, undefined);
    }
    
    public readonly error: StandardObservableProperty<(ViewString | null)>;
    
    //! Declares com.lightningkite.khrysalis.views.FormField.value
    public get value(): T { return this.observable.value; }
    public set value(value: T) {
        this.observable.value = value;
    }
    
    //! Declares com.lightningkite.khrysalis.views.FormField.untypedObservable
    public get untypedObservable(): any { return this.observable; }
    
}

//! Declares com.lightningkite.khrysalis.views.Form
export class Form {
    
    
    public static Companion = class Companion {
        private constructor() {
            this.xIsRequired = new ViewStringRaw(`%1\$s is required.`);
            this.xMustMatchY = new ViewStringRaw(`%1\$s must match %2\$s.`);
        }
        public static INSTANCE = new Companion();
        
        public xIsRequired: ViewString;
        
        public xMustMatchY: ViewString;
        
    }
    
    public readonly fields: Array<UntypedFormField>;
    
    
    public field<T>(name: ViewString, defaultValue: T, validation:  (a: FormField<T>) => (ViewString | null)): FormField<T>{
        const obs = new StandardObservableProperty(defaultValue, undefined);
        
        const field = new FormField(name, obs, (untypedField) => validation(untypedField as FormField<T>));
        
        this.fields.push(field);
        return field;
    }
    
    public field<T>(name: StringResource, defaultValue: T, validation:  (a: FormField<T>) => (ViewString | null)): FormField<T>{ return this.field(new ViewStringResource(name), defaultValue, validation); }
    
    public fieldFromProperty<T>(name: ViewString, property: MutableObservableProperty<T>, validation:  (a: FormField<T>) => (ViewString | null)): FormField<T>{
        const field = new FormField(name, property, (untypedField) => validation(untypedField as FormField<T>));
        
        this.fields.push(field);
        return field;
    }
    
    public fieldFromProperty<T>(name: StringResource, property: MutableObservableProperty<T>, validation:  (a: FormField<T>) => (ViewString | null)): FormField<T>{ return this.fieldFromProperty(new ViewStringResource(name), property, validation); }
    
    public check(): Array<FormValidationError>{
        return listFilterNotNull(this.fields.map((it) => {
                    const result = this.checkField(it);
                    
                    if (!(result.equals(null))) {
                        return new FormValidationError(it, result);
                    } else {
                        return null;
                    }
        }));
    }
    
    public runOrDialog(action: () => void): void{
        const errors = this.check();
        
        if (kotlinCollectionsCollectionIsNotEmpty(errors)) {
            showDialog(kotlinCollectionsListJoinToViewString(errors.map((it) => it._string), undefined));
        } else {
            action();
        }
    }
    
    public checkField(field: UntypedFormField): (ViewString | null){
        const result = field.validation(field);
        
        field.error.value = result;
        return result;
    }
}

//! Declares com.lightningkite.khrysalis.views.required
export function comLightningkiteKhrysalisViewsFormFieldRequired(this_: FormField<string>): (ViewString | null){
    if (kotlinCharSequenceIsBlank(this_.observable.value)) {
        return new ViewStringTemplate(Form.Companion.INSTANCE.xIsRequired, [this_.name]);
    } else {
        return null;
    }
}

//! Declares com.lightningkite.khrysalis.views.notNull
export function comLightningkiteKhrysalisViewsFormFieldNotNull<T>(this_: FormField<T>): (ViewString | null){
    if (this_.observable.value.equals(null)) {
        return new ViewStringTemplate(Form.Companion.INSTANCE.xIsRequired, [this_.name]);
    } else {
        return null;
    }
}

//! Declares com.lightningkite.khrysalis.views.notFalse
export function comLightningkiteKhrysalisViewsFormFieldNotFalse(this_: FormField<Boolean>): (ViewString | null){
    if (this_.observable.value.not()) {
        return new ViewStringTemplate(Form.Companion.INSTANCE.xIsRequired, [this_.name]);
    } else {
        return null;
    }
}

//! Declares com.lightningkite.khrysalis.views.unless
export function comLightningkiteKhrysalisViewsViewStringUnless(this_: ViewString, condition: Boolean): (ViewString | null){
    if (condition) {
        return null;
    } else {
        return this_;
    }
}


//! Declares com.lightningkite.khrysalis.views.matches
export function comLightningkiteKhrysalisViewsFormFieldMatches<T extends object>(this_: FormField<T>, other: FormField<T>): (ViewString | null){
    if (!(this_.observable.value.equals(other.observable.value))) {
        return new ViewStringTemplate(Form.Companion.INSTANCE.xMustMatchY, [this_.name, other.name]);
    } else {
        return null;
    }
}

//object test {
    //    val form = Form()
    //
    //    val username = Field(ViewStringRaw("Username"), "") { it.required() ?: it.isEmail() ?: it.matches(otherField) }
    //    val password = Field(ViewStringRaw("Password"), "") { it.required() }
    //    val verifyPassword = Field(ViewStringRaw("Verify Password"), "") { it.required() ?: it.matches(password) }
//}

