import { MonoTypeOperatorFunction, NextObserver, ObservableInput, SubscriptionLike } from "rxjs/internal/types";
import { Observable } from "rxjs";
export declare class SingleObserver<T> implements NextObserver<T> {
    readonly action: (result: T | null, error: any | null) => void;
    constructor(action: (result: T | null, error: any | null) => void);
    error(err: any): void;
    next(value: T): void;
}
export declare function doOnSubscribe<T>(action: (observer: SubscriptionLike) => void): MonoTypeOperatorFunction<T>;
export declare function doOnDispose<T>(action: (observer: SubscriptionLike) => void): MonoTypeOperatorFunction<T>;
export declare function rxRetryWhenAlt<T>(action: (observable: Observable<any>) => ObservableInput<any>): MonoTypeOperatorFunction<T>;
