import { ObservableProperty } from '../ObservableProperty.shared';
import { MutableObservableProperty } from '../MutableObservableProperty.shared';
export declare function xPageIndicatorViewBind(this_: HTMLDivElement, count: number | undefined, selected: MutableObservableProperty<number>): void;
export declare function xPageIndicatorViewBindDynamic(this_: HTMLDivElement, count: ObservableProperty<number>, selected: MutableObservableProperty<number>): void;
