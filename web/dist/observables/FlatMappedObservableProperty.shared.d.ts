import { ObservableProperty } from './ObservableProperty.shared';
import { Observable } from 'rxjs';
import { MutableObservableProperty } from './MutableObservableProperty.shared';
export declare class FlatMappedObservableProperty<A, B> extends ObservableProperty<B> {
    readonly basedOn: ObservableProperty<A>;
    readonly transformation: ((a: A) => ObservableProperty<B>);
    constructor(basedOn: ObservableProperty<A>, transformation: ((a: A) => ObservableProperty<B>));
    get value(): B;
    get onChange(): Observable<B>;
}
export declare function xObservablePropertySwitchMap<T, B>(this_: ObservableProperty<T>, transformation: ((a: T) => ObservableProperty<B>)): FlatMappedObservableProperty<T, B>;
export declare function xObservablePropertyFlatMap<T, B>(this_: ObservableProperty<T>, transformation: ((a: T) => ObservableProperty<B>)): FlatMappedObservableProperty<T, B>;
export declare function xObservablePropertySwitchMapNotNull<T extends any, B extends any>(this_: ObservableProperty<(T | null)>, transformation: ((a: T) => ObservableProperty<(B | null)>)): FlatMappedObservableProperty<(T | null), (B | null)>;
export declare function xObservablePropertyFlatMapNotNull<T extends any, B extends any>(this_: ObservableProperty<(T | null)>, transformation: ((a: T) => ObservableProperty<(B | null)>)): FlatMappedObservableProperty<(T | null), (B | null)>;
export declare class MutableFlatMappedObservableProperty<A, B> extends MutableObservableProperty<B> {
    readonly basedOn: ObservableProperty<A>;
    readonly transformation: ((a: A) => MutableObservableProperty<B>);
    constructor(basedOn: ObservableProperty<A>, transformation: ((a: A) => MutableObservableProperty<B>));
    get value(): B;
    set value(value: B);
    lastProperty: (MutableObservableProperty<B> | null);
    get onChange(): Observable<B>;
    update(): void;
}
export declare function xObservablePropertySwitchMapMutable<T, B>(this_: ObservableProperty<T>, transformation: ((a: T) => MutableObservableProperty<B>)): MutableFlatMappedObservableProperty<T, B>;
export declare function xObservablePropertyFlatMapMutable<T, B>(this_: ObservableProperty<T>, transformation: ((a: T) => MutableObservableProperty<B>)): MutableFlatMappedObservableProperty<T, B>;
