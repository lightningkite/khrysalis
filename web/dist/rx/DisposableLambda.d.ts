import { SubscriptionLike } from 'rxjs';
export declare class DisposableLambda implements SubscriptionLike {
    closed: boolean;
    lambda: () => void;
    constructor(lambda: () => void);
    unsubscribe(): void;
}
