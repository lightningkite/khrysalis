import { MutableObservableProperty } from '../observables/MutableObservableProperty.shared';
import { Observable } from 'rxjs';
export declare function xSingleWorking<Element extends any>(this_Working: Observable<Element>, observable: MutableObservableProperty<Boolean>): Observable<Element>;
export declare function xObservableMapNotNull<A, B>(this_: Observable<A>, transform: (a: A) => (B | null)): Observable<B>;
