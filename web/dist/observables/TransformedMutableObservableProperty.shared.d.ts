import { Observable } from 'rxjs';
import { MutableObservableProperty } from './MutableObservableProperty.shared';
export declare class TransformedMutableObservableProperty<A, B> extends MutableObservableProperty<B> {
    readonly basedOn: MutableObservableProperty<A>;
    readonly read: ((a: A) => B);
    readonly write: ((a: B) => A);
    constructor(basedOn: MutableObservableProperty<A>, read: ((a: A) => B), write: ((a: B) => A));
    update(): void;
    get value(): B;
    set value(value: B);
    get onChange(): Observable<B>;
}
export declare function xMutableObservablePropertyTransformed<T, B>(this_: MutableObservableProperty<T>, read: ((a: T) => B), write: ((a: B) => T)): MutableObservableProperty<B>;
export declare function xMutableObservablePropertyMap<T, B>(this_: MutableObservableProperty<T>, read: ((a: T) => B), write: ((a: B) => T)): MutableObservableProperty<B>;
