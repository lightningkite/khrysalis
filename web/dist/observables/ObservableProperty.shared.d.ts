import { Observable } from 'rxjs';
export declare abstract class ObservableProperty<T> {
    abstract readonly value: T;
    abstract readonly onChange: Observable<T>;
}
