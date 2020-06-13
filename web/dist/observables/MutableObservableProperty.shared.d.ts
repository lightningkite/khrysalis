import { ObservableProperty } from './ObservableProperty.shared';
export declare abstract class MutableObservableProperty<T> extends ObservableProperty<any> {
    protected constructor();
    abstract value: T;
    abstract update(): void;
}
