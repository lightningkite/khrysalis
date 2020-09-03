import { ObservableProperty } from './ObservableProperty.shared';
import { Observable } from 'rxjs';
export declare class SharingObservableProperty<T> extends ObservableProperty<T> {
    readonly basedOn: ObservableProperty<T>;
    readonly startAsListening: boolean;
    constructor(basedOn: ObservableProperty<T>, startAsListening?: boolean);
    cachedValue: T;
    isListening: boolean;
    get value(): T;
    readonly onChange: Observable<T>;
}
export declare function xObservablePropertyShare<T>(this_: ObservableProperty<T>, startAsListening?: boolean): SharingObservableProperty<T>;
