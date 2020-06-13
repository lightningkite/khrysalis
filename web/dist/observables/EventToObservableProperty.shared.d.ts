import { ObservableProperty } from './ObservableProperty.shared';
import { Observable } from 'rxjs';
export declare class EventToObservableProperty<T> extends ObservableProperty<any> {
    value: T;
    readonly wrapped: Observable<T>;
    constructor(value: T, wrapped: Observable<T>);
    get onChange(): Observable<T>;
}
export declare function ioReactivexObservableAsObservableProperty<Element>(this_: Observable<Element>, defaultValue: Element): ObservableProperty<Element>;
export declare function ioReactivexObservableAsObservablePropertyDefaultNull<Element>(this_: Observable<Element>): ObservableProperty<(Element | null)>;
