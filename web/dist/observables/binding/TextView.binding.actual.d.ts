import { ObservableProperty } from '../ObservableProperty.shared';
export declare function androidWidgetTextViewBindString(this_: HTMLElement, observable: ObservableProperty<string>): void;
export declare function androidWidgetTextViewBindStringRes(this_: HTMLElement, observable: ObservableProperty<(string | null)>): void;
export declare function androidWidgetTextViewBindText<T>(this_: HTMLElement, observable: ObservableProperty<T>, transform: (a: T) => string): void;
