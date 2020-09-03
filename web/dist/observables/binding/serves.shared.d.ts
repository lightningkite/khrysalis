import { MutableObservableProperty } from '../MutableObservableProperty.shared';
import { DisposeCondition } from '../../rx/DisposeCondition.shared';
export declare function xMutableObservablePropertyServes<T>(this_: MutableObservableProperty<T>, until: DisposeCondition, other: MutableObservableProperty<T>): void;
