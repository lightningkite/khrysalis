import { MutableObservableProperty } from './MutableObservableProperty.shared';
import { Subject } from 'rxjs';
export declare class StandardObservableProperty<T> extends MutableObservableProperty<T> {
    underlyingValue: T;
    readonly onChange: Subject<T>;
    constructor(underlyingValue: T, onChange?: Subject<T>);
    get value(): T;
    set value(value: T);
    update(): void;
}
