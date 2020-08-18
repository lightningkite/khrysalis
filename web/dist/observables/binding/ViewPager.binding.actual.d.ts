import { ObservableProperty } from '../ObservableProperty.shared';
import { MutableObservableProperty } from '../MutableObservableProperty.shared';
export declare function androidxViewpagerWidgetViewPagerBindStatic<T>(this_: HTMLDivElement, items: Array<T>, showIndex: MutableObservableProperty<number> | undefined, makeView: (a: T) => HTMLElement): void;
export declare function androidxViewpagerWidgetViewPagerBind<T>(this_: HTMLDivElement, items: ObservableProperty<Array<T>>, _default: T, showIndex: MutableObservableProperty<number> | undefined, makeView: (a: ObservableProperty<T>) => HTMLElement): void;
