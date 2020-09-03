import { MutableObservableProperty } from '../MutableObservableProperty.shared';
export declare function xCompoundButtonBindSelect<T>(this_: HTMLInputElement, value: T, observable: MutableObservableProperty<T>): void;
export declare function xCompoundButtonBindSelectNullable<T>(this_: HTMLInputElement, value: T, observable: MutableObservableProperty<(T | null)>): void;
export declare function xCompoundButtonBindSelectInvert<T>(this_: HTMLInputElement, value: T, observable: MutableObservableProperty<(T | null)>): void;
export declare function xCompoundButtonBind(this_: HTMLInputElement, observable: MutableObservableProperty<boolean>): void;
