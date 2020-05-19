// Generated by Khrysalis TypeScript converter - this file will be overwritten.
// File: rx/DisposeCondition.shared.kt
// Package: com.lightningkite.khrysalis.rx
// FQImport: com.lightningkite.khrysalis.rx.andAllDisposeConditions SKIPPED due to same file
// FQImport: com.lightningkite.khrysalis.rx.DisposableLambda TS DisposableLambda
// FQImport: com.lightningkite.khrysalis.rx.andAllDisposeConditions.list TS list
// FQImport: com.lightningkite.khrysalis.rx.and.other TS other
// FQImport: com.lightningkite.khrysalis.rx.DisposeCondition SKIPPED due to same file
// FQImport: com.lightningkite.khrysalis.rx.DisposeCondition TS DisposeCondition
// FQImport: com.lightningkite.khrysalis.rx.andAllDisposeConditions TS andAllDisposeConditions
// FQImport: com.lightningkite.khrysalis.rx.andAllDisposeConditions.<anonymous>.it TS it
// FQImport: com.lightningkite.khrysalis.rx.or.other TS other
// FQImport: com.lightningkite.khrysalis.rx.DisposeCondition.call TS call
// FQImport: com.lightningkite.khrysalis.rx.andAllDisposeConditions.<anonymous>.disposalsLeft TS disposalsLeft
// FQImport: com.lightningkite.khrysalis.rx.or.<anonymous>.it TS it
// FQImport: com.lightningkite.khrysalis.rx.andAllDisposeConditions.<anonymous>.item TS item
import { DisposableLambda } from './DisposeCondition.actual'
import { SubscriptionLike } from 'rxjs'

//! Declares com.lightningkite.khrysalis.rx.DisposeCondition
export class DisposeCondition {
    public readonly call:  (a: SubscriptionLike) => void;
    public constructor(call:  (a: SubscriptionLike) => void) {
        this.call = call;
    }
}

//! Declares com.lightningkite.khrysalis.rx.and
export function comLightningkiteKhrysalisRxDisposeConditionAnd(this_: DisposeCondition, other: DisposeCondition): DisposeCondition{ return andAllDisposeConditions([this_, other]); }

//! Declares com.lightningkite.khrysalis.rx.andAllDisposeConditions
export function andAllDisposeConditions(list: Array<DisposeCondition>): DisposeCondition{ return new DisposeCondition((it) => {
            let disposalsLeft = list.length;
            
            for (const item of list) {
                item.call(new DisposableLambda(() => {
                            disposalsLeft = disposalsLeft - 1;
                            if (disposalsLeft === 0) it.unsubscribe()
                }));
            }
}); }

//! Declares com.lightningkite.khrysalis.rx.or
export function comLightningkiteKhrysalisRxDisposeConditionOr(this_: DisposeCondition, other: DisposeCondition): DisposeCondition{ return new DisposeCondition((it) => {
            this_.call(it);; other.call(it);
}); }
