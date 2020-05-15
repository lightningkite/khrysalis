// Generated by Khrysalis TypeScript converter
// File: rx/DisposeCondition.actual.kt
// Package: com.lightningkite.khrysalis.rx
import { DisposeCondition } from './DisposeCondition.shared'
import { SubscriptionLike } from 'rxjs'

//! Declares com.lightningkite.khrysalis.rx.removed
export function getAndroidViewViewRemoved(this_Removed: View): DisposeCondition {}


//! Declares com.lightningkite.khrysalis.rx.DisposableLambda
export class DisposableLambda implements SubscriptionLike {
    closed: boolean = false;
    lambda: ()=>void;
    constructor(lambda: ()=>void) {
        this.lambda = lambda
    }

    unsubscribe(): void {
        if(this.closed) { return }
        this.closed = true;
        this.lambda();
    }

}


//! Declares com.lightningkite.khrysalis.rx.forever
export function IoReactivexDisposablesDisposableForever<Self extends SubscriptionLike>(this_Forever: Self): Self{
    return this_Forever;
}

//! Declares com.lightningkite.khrysalis.rx.until
export function IoReactivexDisposablesDisposableUntil<Self extends SubscriptionLike>(this_Until: Self, condition: DisposeCondition): Self{
    condition.call(this_Until);
    return this_Until;
}

