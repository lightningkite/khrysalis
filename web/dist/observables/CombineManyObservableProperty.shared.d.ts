import { ObservableProperty } from './ObservableProperty.shared';
import { Observable } from 'rxjs';
export declare class CombineManyObservableProperty<IN> extends ObservableProperty<Array<IN>> {
    readonly observables: Array<ObservableProperty<IN>>;
    constructor(observables: Array<ObservableProperty<IN>>);
    get value(): Array<IN>;
    get onChange(): Observable<Array<IN>>;
}
export declare function combinedAndMap<IN, OUT>(this_: Array<ObservableProperty<IN>>, combiner: ((a: Array<IN>) => OUT)): ObservableProperty<OUT>;
export declare function xListCombined<T>(this_: Array<ObservableProperty<T>>): ObservableProperty<Array<T>>;
