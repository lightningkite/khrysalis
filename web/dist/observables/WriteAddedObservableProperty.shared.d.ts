import { ObservableProperty } from './ObservableProperty.shared';
import { Observable } from 'rxjs';
import { MutableObservableProperty } from './MutableObservableProperty.shared';
export declare class WriteAddedObservableProperty<A> extends MutableObservableProperty<A> {
    readonly basedOn: ObservableProperty<A>;
    readonly onWrite: ((a: A) => void);
    constructor(basedOn: ObservableProperty<A>, onWrite: ((a: A) => void));
    get value(): A;
    set value(value: A);
    get onChange(): Observable<A>;
    update(): void;
}
export declare function xObservablePropertyWithWrite<T>(this_: ObservableProperty<T>, onWrite: ((a: T) => void)): MutableObservableProperty<T>;
