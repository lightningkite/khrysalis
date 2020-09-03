import { ObservableProperty } from './ObservableProperty.shared';
import { Observable } from 'rxjs';
export declare class RxTransformationOnlyObservableProperty<T> extends ObservableProperty<T> {
    readonly basedOn: ObservableProperty<T>;
    readonly operator: ((a: Observable<T>) => Observable<T>);
    constructor(basedOn: ObservableProperty<T>, operator: ((a: Observable<T>) => Observable<T>));
    get value(): T;
    get onChange(): Observable<T>;
}
export declare function xObservablePropertyDistinctUntilChanged<T>(this_: ObservableProperty<T>): ObservableProperty<T>;
export declare function xObservablePropertyPlusRx<T>(this_: ObservableProperty<T>, operator: ((a: Observable<T>) => Observable<T>)): ObservableProperty<T>;
