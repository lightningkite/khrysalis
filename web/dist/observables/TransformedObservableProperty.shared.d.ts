import { ObservableProperty } from './ObservableProperty.shared';
import { Observable } from 'rxjs';
export declare class TransformedObservableProperty<A, B> extends ObservableProperty<B> {
    readonly basedOn: ObservableProperty<A>;
    readonly read: ((a: A) => B);
    constructor(basedOn: ObservableProperty<A>, read: ((a: A) => B));
    get value(): B;
    get onChange(): Observable<B>;
}
export declare function xObservablePropertyTransformed<T, B>(this_: ObservableProperty<T>, read: ((a: T) => B)): ObservableProperty<B>;
export declare function xObservablePropertyMap<T, B>(this_: ObservableProperty<T>, read: ((a: T) => B)): ObservableProperty<B>;
