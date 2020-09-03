import { ObservableProperty } from '../ObservableProperty.shared';
export declare function xAutoCompleteTextViewBind<T>(this_: HTMLInputElement, options: ObservableProperty<Array<T>>, toString: (a: T) => string, onItemSelected: (a: T) => void): void;
export declare function xAutoCompleteTextViewBindList<T>(this_: HTMLInputElement, options: ObservableProperty<Array<T>>, toString: (a: T) => string, onItemSelected: (a: T) => void): void;
