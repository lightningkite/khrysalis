import { MutableObservableProperty } from '../MutableObservableProperty.shared';
export declare function comGoogleAndroidMaterialTabsTabLayoutBind<T>(this_: HTMLDivElement, tabs: Array<T>, selected: MutableObservableProperty<T>, allowReselect: boolean, toString: (a: T) => string): void;
export declare function tabLayoutBindIndex(this_: HTMLDivElement, tabs: Array<string>, selected: MutableObservableProperty<number>, allowReselect?: boolean): void;
