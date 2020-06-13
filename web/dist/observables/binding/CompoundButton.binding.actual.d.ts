import { MutableObservableProperty } from '../MutableObservableProperty.shared';
export declare function androidWidgetCompoundButtonBindSelect<T>(this_: HTMLElement, value: T, observable: MutableObservableProperty<T>): void;
export declare function androidWidgetCompoundButtonBindSelectNullable<T>(this_: HTMLElement, value: T, observable: MutableObservableProperty<(T | null)>): void;
export declare function androidWidgetCompoundButtonBindSelectInvert<T>(this_: HTMLElement, value: T, observable: MutableObservableProperty<(T | null)>): void;
export declare function androidWidgetCompoundButtonBind(this_: HTMLElement, observable: MutableObservableProperty<boolean>): void;
