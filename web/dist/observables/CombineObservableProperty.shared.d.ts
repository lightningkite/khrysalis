import { ObservableProperty } from './ObservableProperty.shared';
import { Observable } from 'rxjs';
export declare class CombineObservableProperty<T, A, B> extends ObservableProperty<T> {
    readonly observableA: ObservableProperty<A>;
    readonly observableB: ObservableProperty<B>;
    readonly combiner: ((a: A, b: B) => T);
    constructor(observableA: ObservableProperty<A>, observableB: ObservableProperty<B>, combiner: ((a: A, b: B) => T));
    get value(): T;
    get onChange(): Observable<T>;
}
export declare function xObservablePropertyCombine<T, B, C>(this_: ObservableProperty<T>, other: ObservableProperty<B>, combiner: ((a: T, b: B) => C)): ObservableProperty<C>;
