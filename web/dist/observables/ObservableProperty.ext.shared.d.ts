import { Observable, SubscriptionLike } from 'rxjs';
import { ObservableProperty } from './ObservableProperty.shared';
import { MutableObservableProperty } from './MutableObservableProperty.shared';
export declare function xObservablePropertyObservableGet<T>(this_: ObservableProperty<T>): Observable<T>;
export declare function xObservablePropertyObservableNNGet<T>(this_: ObservableProperty<T>): Observable<T>;
export declare function xObservablePropertyOnChangeNNGet<T>(this_: ObservableProperty<T>): Observable<T>;
export declare function xObservablePropertySubscribeBy<T>(this_: ObservableProperty<T>, onError?: ((a: any) => void), onComplete?: (() => void), onNext?: ((a: T) => void)): SubscriptionLike;
export declare function includes<E>(collection: MutableObservableProperty<Set<E>>, element: E): MutableObservableProperty<boolean>;
export declare function xObservablePropertyWhileActive(this_: ObservableProperty<boolean>, action: (() => SubscriptionLike)): SubscriptionLike;
