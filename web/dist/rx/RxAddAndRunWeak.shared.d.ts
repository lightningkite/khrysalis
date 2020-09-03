import { Observable, SubscriptionLike } from 'rxjs';
export declare function xObservableAdd<Element extends any>(this_: Observable<Element>, listener: (a: Element) => Boolean): SubscriptionLike;
