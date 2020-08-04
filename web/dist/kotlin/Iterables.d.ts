export declare function kotlinCollectionsIterableFirstOrNull<T>(iter: Iterable<T>): T | null;
export declare function kotlinCollectionsIterableLastOrNull<T>(iterable: Iterable<T>): T | null;
export declare function kotlinCollectionsIterableFirst<T>(iter: Iterable<T>): T;
export declare function kotlinCollectionsIterableLast<T>(iterable: Iterable<T>): T | null;
export declare function kotlinCollectionsIterableSingle<T>(iter: Iterable<T>): T | null;
export declare function kotlinCollectionsIterableSingleOrNull<T>(iter: Iterable<T>): T;
export declare function kotlinCollectionsIterableJoinToString<T>(iter: Iterable<T>, separator?: string, prefix?: string, postfix?: string, limit?: number, truncated?: string, transform?: (t: T) => string): string;
export declare function kotlinCollectionsIterableDistinctBy<T, D>(iter: Iterable<T>, selector: (t: T) => D): Array<T>;
export declare function kotlinSequencesSequenceDistinctBy<T, D>(iter: Iterable<T>, selector: (t: T) => D): Iterable<T>;
