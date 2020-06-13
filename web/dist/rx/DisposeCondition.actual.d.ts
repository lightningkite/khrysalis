import { DisposeCondition } from './DisposeCondition.shared';
import { SubscriptionLike } from 'rxjs';
export declare function getAndroidViewViewRemoved(this_Removed: Node): DisposeCondition;
export declare class DisposableLambda implements SubscriptionLike {
    closed: boolean;
    lambda: () => void;
    constructor(lambda: () => void);
    unsubscribe(): void;
}
export declare function ioReactivexDisposablesDisposableForever<Self extends SubscriptionLike>(this_Forever: Self): Self;
export declare function ioReactivexDisposablesDisposableUntil<Self extends SubscriptionLike>(this_Until: Self, condition: DisposeCondition): Self;
