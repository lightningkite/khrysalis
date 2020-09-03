import { ObservableProperty } from '../ObservableProperty.shared';
import { StandardObservableProperty } from '../StandardObservableProperty.shared';
export declare function xRecyclerViewWhenScrolledToEnd(this_: HTMLDivElement, action: () => void): void;
export declare function xRecyclerViewReverseDirectionGet(this_: HTMLDivElement): boolean;
export declare function xRecyclerViewReverseDirectionSet(this_: HTMLDivElement, value: boolean): void;
export declare function xRecyclerViewBind<T>(this_: HTMLDivElement, data: ObservableProperty<Array<T>>, defaultValue: T, makeView: (a: ObservableProperty<T>) => HTMLElement): void;
interface RvTypeHandlerHandler<T> {
    type: Array<any>;
    defaultValue: T;
    action: (a: ObservableProperty<T>) => HTMLElement;
    buffer: Array<[StandardObservableProperty<T>, HTMLElement]>;
}
export declare class RVTypeHandler {
    handlers: Array<RvTypeHandlerHandler<any>>;
    handle<T extends any>(T: Array<any>, defaultValue: T, action: (a: ObservableProperty<T>) => HTMLElement): void;
}
export declare function recyclerViewBindMultiType(this_: HTMLDivElement, viewDependency: Window, data: ObservableProperty<Array<any>>, typeHandlerSetup: (a: RVTypeHandler) => void): void;
export declare function xRecyclerViewBindMulti<T>(this_: HTMLDivElement, data: ObservableProperty<Array<T>>, defaultValue: T, determineType: (a: T) => number, makeView: (a: number, b: ObservableProperty<T>) => HTMLElement): void;
export declare function xRecyclerViewBindRefresh(this_: HTMLDivElement, loading: ObservableProperty<boolean>, refresh: () => void): void;
export {};
