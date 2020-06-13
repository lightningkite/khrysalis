import { ObservableProperty } from './ObservableProperty.shared';
import { Subject } from 'rxjs';
export declare class ObservableStack<T extends object> extends ObservableProperty<any> {
    constructor();
    static Companion: {
        new (): {
            withFirst<T_1 extends object>(value: T_1): ObservableStack<T_1>;
        };
        INSTANCE: {
            withFirst<T_1 extends object>(value: T_1): ObservableStack<T_1>;
        };
    };
    readonly onChange: Subject<Array<T>>;
    get value(): Array<T>;
    readonly stack: Array<T>;
    push(t: T): void;
    swap(t: T): void;
    pop(): boolean;
    dismiss(): boolean;
    popTo(t: T): void;
    popToPredicate(predicate: (a: T) => boolean): void;
    root(): void;
    reset(t: T): void;
}
