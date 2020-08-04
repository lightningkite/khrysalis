import { MutableObservableProperty } from '../observables/MutableObservableProperty.shared';
import { Observable } from 'rxjs';
export declare function ioReactivexSingleWorking<Element extends any>(this_Working: Observable<Element>, observable: MutableObservableProperty<Boolean>): Observable<Element>;
export declare function ioReactivexObservableMapNotNull<A, B>(this_: Observable<A>, transform: (a: A) => (B | null)): Observable<B>;
