import {Comparable} from "../Kotlin";

//! Declares kotlin.Comparator
export type Comparator<T> = (lhs: T, rhs: T)=>number

export function safeCompare(left: any, right: any): number {
    if(left === null) {
        if(right === null) { return 0 }
        return -1
    }
    if(right === null) { return 1 }
    if(typeof left === "object" && !(left instanceof Date)) {
        return (left as Comparable<any>).compareTo(right)
    } else {
        if(left < right)
            return -1
        else if(left == right)
            return 0
        else
            return 1
    }
}

export function cMin<T>(a: T, b: T): T {
    if(safeCompare(a, b) < 0) {
        return a;
    } else {
        return b;
    }
}
export function cMax<T>(a: T, b: T): T {
    if(safeCompare(a, b) > 0) {
        return a;
    } else {
        return b;
    }
}
export function cCoerce<T>(value: T, low: T, high: T): T {
    return cMin(high, cMax(value, low));
}