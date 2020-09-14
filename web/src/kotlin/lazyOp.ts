import {
    countdown as rawCountdown,
    countup as rawCountup,
    range as rawRange,
    chunkBy as rawChunkBy,
    chunk as rawChunk,
    concat as rawConcat,
    dropRight as rawDropRight,
    dropUntil as rawDropUntil,
    drop as rawDrop,
    filter as rawFilter,
    flattenBy as rawFlattenBy,
    flattenDeep as rawFlattenDeep,
    flatten as rawFlatten,
    map as rawMap,
    repeat as rawRepeat,
    slice as rawSlice,
    splitBy as rawSplitBy,
    split as rawSplit,
    takeRight as rawTakeRight,
    takeUntil as rawTakeUntil,
    take as rawTake,
    tap as rawTap,
    transform as rawTransform,
    uniqBy as rawUniqBy,
    uniq as rawUniq,
    zip as rawZip,
    consume as rawConsume,
    each as rawEach,
    every as rawEvery,
    find as rawFind,
    first as rawFirst,
    includes as rawIncludes,
    match as rawMatch,
    reduce as rawReduce,
    some as rawSome,
    last as rawLast,
    toArray as rawToArray,
    toSet as rawToSet
} from "iterable-operator"

function repair<T, F extends () => Iterable<T>>(f: F): () => Iterable<T>
function repair<T, A, F extends (a: A) => Iterable<T>>(f: F): (a: A) => Iterable<T>
function repair<T, A, B, F extends (a: A, b: B) => Iterable<T>>(f: F): (a: A, b: B) => Iterable<T>
function repair<T, A, B, C, F extends (a: A, b: B, c: C) => Iterable<T>>(f: F): (a: A, b: B, c: C) => Iterable<T>
function repair<T, A, B, C, D, F extends (a: A, b: B, c: C, d: D) => Iterable<T>>(f: F): (a: A, b: B, c: C, d: D) => Iterable<T>
function repair<T>(f: (...args: any) => Iterable<T>): (...args: any) => Iterable<T> {
    return (...args) => {
        return {
            [Symbol.iterator]: () => f(...args)[Symbol.iterator]()
        }
    }
}

export let chunkBy: <T>(iterable: Iterable<T>, fn: (element: T, index: number) => boolean) => Iterable<T[]> = repair(rawChunkBy)
export let chunk: <T>(iterable: Iterable<T>, size: number) => Iterable<T[]> = repair(rawChunk)
export let concat: <T1, T2>(iterable1: Iterable<T1>, iterable2: Iterable<T2>) => Iterable<T1 | T2> = repair(rawConcat)
export let dropRight: <T>(iterable: Iterable<T>, count: number) => Iterable<T> = repair(rawDropRight)
export let dropUntil: <T>(iterable: Iterable<T>, fn: (element: T, index: number) => boolean) => Iterable<T> = repair(rawDropUntil)
export let drop: <T>(iterable: Iterable<T>, count: number) => Iterable<T> = repair(rawDrop)
export let filter: <T, U extends T = T>(iterable: Iterable<T>, fn: (element: T, index: number) => boolean) => Iterable<U> = repair(rawFilter)
export let flattenBy: (iterable: Iterable<unknown>, fn: (element: unknown, level: number) => boolean) => Iterable<any> = repair(rawFlattenBy)
export let flattenDeep: <T>(iterable: Iterable<unknown>, depth: number) => Iterable<T> = repair(rawFlattenDeep)
export let flatten: <T>(iterable: Iterable<unknown>) => Iterable<T> = repair(rawFlatten)
export let map: <T, U>(iterable: Iterable<T>, fn: (element: T, index: number) => U) => Iterable<U> = repair(rawMap)
export let repeat: <T>(iterable: Iterable<T>, times: number) => Iterable<T> = repair(rawRepeat)
export let slice: <T>(iterable: Iterable<T>, start: number, end: number) => Iterable<T> = repair(rawSlice)
export let splitBy: <T>(iterable: Iterable<T>, fn: (element: T, index: number) => boolean) => Iterable<T[]> = repair(rawSplitBy)
export let split: <T>(iterable: Iterable<T>, separator: T) => Iterable<T[]> = repair(rawSplit)
export let takeRight: <T>(iterable: Iterable<T>, count: number) => Iterable<T> = repair(rawTakeRight)
export let takeUntil: <T>(iterable: Iterable<T>, fn: (element: T, index: number) => boolean) => Iterable<T> = repair(rawTakeUntil)
export let take: <T>(iterable: Iterable<T>, count: number) => Iterable<T> = repair(rawTake)
export let tap: <T>(iterable: Iterable<T>, fn: (element: T, index: number) => unknown) => Iterable<T> = repair(rawTap)
export let transform: <T, U>(iterable: Iterable<T>, transformer: (iterable: Iterable<T>) => Iterable<U>) => Iterable<U> = repair(rawTransform)
export let uniqBy: <T, U>(iterable: Iterable<T>, fn: (element: T, index: number) => U) => Iterable<T> = repair(rawUniqBy)
export let uniq: <T>(iterable: Iterable<T>) => Iterable<T> = repair(rawUniq)
export let zip: <T1, T2>(iterable1: Iterable<T1>, iterable2: Iterable<T2>) => Iterable<Array<T1 | T2>> = repair(rawZip)

export let countdown = rawCountdown
export let countup = rawCountup
export let range = rawRange
export let consume = rawConsume
export let each = rawEach
export let every = rawEvery
export function find<T>(iter: Iterable<T>, predicate: (item: T)=>boolean): T | null {
    for(const item of iter){
        if(predicate(item)){
            return item;
        }
    }
    return null;
}
export let first = rawFirst
export let includes = rawIncludes
export let match = rawMatch
export let reduce = rawReduce
export let some = rawSome
export let last = rawLast
export let toArray = rawToArray
export let toSet = rawToSet
