import { ObservableProperty } from './ObservableProperty.shared';
export declare abstract class MutableObservableProperty<T> extends ObservableProperty<T> {
    protected constructor();
    abstract value: T;
    abstract update(): void;
}
