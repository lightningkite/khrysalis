import { ObservableProperty } from '../ObservableProperty.shared';
export declare function xTextViewBindString(this_: HTMLElement, observable: ObservableProperty<string>): void;
export declare function xTextViewBindStringRes(this_: HTMLElement, observable: ObservableProperty<(string | null)>): void;
export declare function xTextViewBindText<T>(this_: HTMLElement, observable: ObservableProperty<T>, transform: (a: T) => string): void;
