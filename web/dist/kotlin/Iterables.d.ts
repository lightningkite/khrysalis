export declare function xIterableFirstOrNull<T>(iter: Iterable<T>): T | null;
export declare function xIterableLastOrNull<T>(iterable: Iterable<T>): T | null;
export declare function xIterableFirst<T>(iter: Iterable<T>): T;
export declare function xIterableLast<T>(iterable: Iterable<T>): T | null;
export declare function xIterableSingle<T>(iter: Iterable<T>): T | null;
export declare function xIterableSingleOrNull<T>(iter: Iterable<T>): T;
export declare function xIterableJoinToString<T>(iter: Iterable<T>, separator?: string, prefix?: string, postfix?: string, limit?: number, truncated?: string, transform?: (t: T) => string): string;
export declare function xIterableDistinctBy<T, D>(iter: Iterable<T>, selector: (t: T) => D): Array<T>;
export declare function xSequenceDistinctBy<T, D>(iter: Iterable<T>, selector: (t: T) => D): Iterable<T>;
