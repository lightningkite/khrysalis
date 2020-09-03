import { ObservableProperty } from '../ObservableProperty.shared';
import { MutableObservableProperty } from '../MutableObservableProperty.shared';
export declare function xRatingBarBindMutable(this_: HTMLDivElement, stars: number, observable: MutableObservableProperty<number>): void;
export declare function xRatingBarBind(this_: HTMLDivElement, stars: number, observable: ObservableProperty<number>): void;
export declare function xRatingBarBindFloatMutable(this_: HTMLDivElement, stars: number, observable: MutableObservableProperty<number>): void;
export declare function xRatingBarBindFloat(this_: HTMLDivElement, stars: number, observable: ObservableProperty<number>): void;
