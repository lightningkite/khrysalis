import {Comparable} from "../Kotlin";

//! Declares kotlin.Comparator
export type Comparator<T> = (lhs: T, rhs: T)=>number

export function safeCompare(left: any, right: any): number {
    if(typeof left === "object") {
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