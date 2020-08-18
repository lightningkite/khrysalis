import { Comparable } from './Kotlin';
export declare function kotlinCollectionsListBinarySearch<T>(self: Array<T>, fromIndex: number | undefined, toIndex: number | undefined, comparison: (a: T) => number): number;
export declare function kotlinCollectionsListBinarySearchBy<T, K extends Comparable<K>>(self: Array<T>, key: K | null, fromIndex: number | undefined, toIndex: number | undefined, selector: (a: T) => (K | null)): number;
export declare function kotlinCollectionsListWithoutIndex<T>(this_WithoutIndex: Array<T>, index: number): Array<T>;
export declare function kotlinCollectionsIterableSumByLong<T>(this_SumByLong: Iterable<T>, selector: (a: T) => number): number;
export declare function kotlinCollectionsMutableListBinaryInsertBy<T, K extends Comparable<K>>(this_BinaryInsertBy: Array<T>, item: T, selector: (a: T) => (K | null)): void;
export declare function kotlinCollectionsMutableListBinaryInsertByDistinct<T, K extends Comparable<K>>(this_BinaryInsertByDistinct: Array<T>, item: T, selector: (a: T) => (K | null)): Boolean;
export declare function kotlinCollectionsListBinaryFind<T, K extends Comparable<K>>(this_BinaryFind: Array<T>, key: K, selector: (a: T) => (K | null)): (T | null);
export declare function kotlinCollectionsListBinaryForEach<T, K extends Comparable<K>>(this_BinaryForEach: Array<T>, lower: K, upper: K, selector: (a: T) => (K | null), action: (a: T) => void): void;
