import { ViewString } from './Strings.shared';
import { MutableObservableProperty } from '../observables/MutableObservableProperty.shared';
import { StandardObservableProperty } from '../observables/StandardObservableProperty.shared';
export declare class FormValidationError {
    readonly field: UntypedFormField;
    readonly _string: ViewString;
    constructor(field: UntypedFormField, _string: ViewString);
}
export interface UntypedFormField {
    readonly name: ViewString;
    readonly untypedObservable: any;
    readonly validation: ((a: UntypedFormField) => (ViewString | null));
    readonly error: StandardObservableProperty<(ViewString | null)>;
}
export declare class FormField<T> implements UntypedFormField {
    static implementsInterfaceComLightningkiteKhrysalisViewsUntypedFormField: boolean;
    readonly name: ViewString;
    readonly observable: MutableObservableProperty<T>;
    readonly validation: ((a: UntypedFormField) => (ViewString | null));
    constructor(name: ViewString, observable: MutableObservableProperty<T>, validation: ((a: UntypedFormField) => (ViewString | null)));
    readonly error: StandardObservableProperty<(ViewString | null)>;
    get value(): T;
    set value(value: T);
    get untypedObservable(): any;
}
export declare class Form {
    constructor();
    readonly fields: Array<UntypedFormField>;
    field<T>(name: ViewString, defaultValue: T, validation: ((a: FormField<T>) => (ViewString | null))): FormField<T>;
    fieldRes<T>(name: string, defaultValue: T, validation: ((a: FormField<T>) => (ViewString | null))): FormField<T>;
    fieldFromProperty<T>(name: ViewString, property: MutableObservableProperty<T>, validation: ((a: FormField<T>) => (ViewString | null))): FormField<T>;
    fieldFromPropertyRes<T>(name: string, property: MutableObservableProperty<T>, validation: ((a: FormField<T>) => (ViewString | null))): FormField<T>;
    check(): Array<FormValidationError>;
    runOrDialog(action: (() => void)): void;
    checkField(field: UntypedFormField): (ViewString | null);
}
export declare namespace Form {
    class Companion {
        private constructor();
        static INSTANCE: Companion;
        xIsRequired: ViewString;
        xMustMatchY: ViewString;
    }
}
export declare function xFormFieldRequired(this_: FormField<string>): (ViewString | null);
export declare function xFormFieldNotNull<T>(this_: FormField<T>): (ViewString | null);
export declare function xFormFieldNotFalse(this_: FormField<boolean>): (ViewString | null);
export declare function xViewStringUnless(this_: ViewString, condition: boolean): (ViewString | null);
export declare function xFormFieldMatches<T extends any>(this_: FormField<T>, other: FormField<T>): (ViewString | null);
