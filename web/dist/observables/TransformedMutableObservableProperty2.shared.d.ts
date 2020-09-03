import { Observable } from 'rxjs';
import { MutableObservableProperty } from './MutableObservableProperty.shared';
export declare class TransformedMutableObservableProperty2<A, B> extends MutableObservableProperty<B> {
    readonly basedOn: MutableObservableProperty<A>;
    readonly read: ((a: A) => B);
    readonly write: ((a: A, b: B) => A);
    constructor(basedOn: MutableObservableProperty<A>, read: ((a: A) => B), write: ((a: A, b: B) => A));
    update(): void;
    get value(): B;
    set value(value: B);
    get onChange(): Observable<B>;
}
export declare function xMutableObservablePropertyMapWithExisting<T, B>(this_: MutableObservableProperty<T>, read: ((a: T) => B), write: ((a: T, b: B) => T)): MutableObservableProperty<B>;
