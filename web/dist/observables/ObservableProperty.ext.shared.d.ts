import { ObservableProperty } from './ObservableProperty.shared';
import { Observable, SubscriptionLike } from 'rxjs';
import { MutableObservableProperty } from './MutableObservableProperty.shared';
export declare function getComLightningkiteKhrysalisObservablesObservablePropertyObservable<T>(this_: ObservableProperty<T>): Observable<T>;
export declare function getComLightningkiteKhrysalisObservablesObservablePropertyObservableNN<T>(this_: ObservableProperty<T>): Observable<T>;
export declare function getComLightningkiteKhrysalisObservablesObservablePropertyOnChangeNN<T>(this_: ObservableProperty<T>): Observable<T>;
export declare function comLightningkiteKhrysalisObservablesObservablePropertySubscribeBy<T>(this_: ObservableProperty<T>, onError?: ((a: any) => void), onComplete?: (() => void), onNext?: ((a: T) => void)): SubscriptionLike;
export declare function includes<E>(collection: MutableObservableProperty<Set<E>>, element: E): MutableObservableProperty<boolean>;
