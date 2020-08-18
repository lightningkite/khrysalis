import { ObservableProperty } from './ObservableProperty.shared';
import { Subject } from 'rxjs';
export declare class ObservableStack<T extends object> extends ObservableProperty<Array<T>> {
    constructor();
    readonly onChange: Subject<Array<T>>;
    get value(): Array<T>;
    readonly stack: Array<T>;
    push(t: T): void;
    swap(t: T): void;
    pop(): boolean;
    dismiss(): boolean;
    backPressPop(): boolean;
    backPressDismiss(): boolean;
    popTo(t: T): void;
    popToPredicate(predicate: ((a: T) => boolean)): void;
    root(): void;
    reset(t: T): void;
}
export declare namespace ObservableStack {
    class Companion {
        private constructor();
        static INSTANCE: Companion;
        withFirst<T extends object>(value: T): ObservableStack<T>;
    }
}
