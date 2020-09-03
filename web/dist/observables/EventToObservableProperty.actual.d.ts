import { ObservableProperty } from './ObservableProperty.shared';
import { Observable } from 'rxjs';
export declare function xObservableAsObservablePropertyUnboxed<T>(this_: Observable<T>, defaultValue: T): ObservableProperty<T>;
