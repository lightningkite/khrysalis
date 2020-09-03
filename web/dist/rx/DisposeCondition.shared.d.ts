import { SubscriptionLike } from 'rxjs';
export declare class DisposeCondition {
    readonly call: ((a: SubscriptionLike) => void);
    constructor(call: ((a: SubscriptionLike) => void));
}
export declare function xDisposeConditionAnd(this_: DisposeCondition, other: DisposeCondition): DisposeCondition;
export declare function andAllDisposeConditions(list: Array<DisposeCondition>): DisposeCondition;
export declare function xDisposeConditionOr(this_: DisposeCondition, other: DisposeCondition): DisposeCondition;
