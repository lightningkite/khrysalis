import { NextObserver } from "rxjs/internal/types";
export declare class SingleObserver<T> implements NextObserver<T> {
    readonly action: (result: T | null, error: any | null) => void;
    constructor(action: (result: T | null, error: any | null) => void);
    error(err: any): void;
    next(value: T): void;
}
