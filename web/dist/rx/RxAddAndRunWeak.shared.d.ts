import { Observable, SubscriptionLike } from 'rxjs';
export declare function ioReactivexObservableAdd<Element extends any>(this_: Observable<Element>, listener: (a: Element) => Boolean): SubscriptionLike;
