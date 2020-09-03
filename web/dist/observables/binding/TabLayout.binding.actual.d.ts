import { MutableObservableProperty } from '../MutableObservableProperty.shared';
export declare function xTabLayoutBind<T>(this_: HTMLDivElement, tabs: Array<T>, selected: MutableObservableProperty<T>, allowReselect: boolean | undefined, toString: (a: T) => string): void;
export declare function xTabLayoutBindIndex(this_: HTMLDivElement, tabs: Array<string>, selected: MutableObservableProperty<number>, allowReselect?: boolean): void;
