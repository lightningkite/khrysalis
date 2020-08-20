export declare type Comparator<T> = (lhs: T, rhs: T) => number;
export declare function safeCompare(left: any, right: any): number;
export declare function cMin<T>(a: T, b: T): T;
export declare function cMax<T>(a: T, b: T): T;
export declare function cCoerce<T>(value: T, low: T, high: T): T;
