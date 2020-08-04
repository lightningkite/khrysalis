import { Observable } from 'rxjs';
export declare abstract class ObservableProperty<T> {
    protected constructor();
    abstract readonly value: T;
    abstract readonly onChange: Observable<T>;
}
