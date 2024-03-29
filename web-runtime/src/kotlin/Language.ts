export type ReifiedType<T = unknown> = Array<any>;
export function typeOf<T>(basis: any, ...args: ReifiedType): ReifiedType<T> { return [basis, ...args] }

export class Exception extends Error {
    cause: any;

    constructor(message?: string, cause?: any) {
        super(message);
        this.cause = cause ?? null;
    }
}

export function printStackTrace(something: any){
    if(something instanceof Error) {
        console.error(`${something.name}: ${something.message}\n${something.stack}`);
    } else {
        console.error(`Raw error: ${something}`)
    }
}

export class IllegalArgumentException extends Exception {
}

export class IllegalStateException extends Exception {
}

export class NoSuchElementException extends Exception {
}

export class IndexOutOfBoundsException extends Exception {
}

export function hashString(s: string | null): number {
    if(s === null) return 0
    var h = 0, l = s.length, i = 0;
    if ( l > 0 )
        while (i < l)
            h = (h << 5) - h + s.charCodeAt(i++) | 0;
    return h;
}

export function hashAnything(item: any): number {
    if (item === null || item === undefined) return 0;
    switch (typeof item) {
        case "object":
            if(item.hashCode){
                return item.hashCode();
            } else {
                let hash = 17;
                for(const prop in item) {
                    hash = 31 * hash + item[prop]
                }
                return hash;
            }
        case "number":
            return Math.floor(item);
        case "string":
            return hashString(item);
        case "boolean":
            return item ? 1 : 0;
        default:
            return 0;
    }
}

export function safeEq(left: any, right: any): boolean {
    if (left !== null && (typeof left) === "object" && left.equals) {
        return left.equals(right)
    } else {
        return left === right
    }
}
export function equalBy<A>(key: ((a: A) => any) | keyof A): (lhs: A, rhs: A) => boolean {
    if(typeof key === "function")
        return (l, r) => safeEq(key(l), key(r))
    else
        return (l, r) => safeEq(l[key], r[key])
}

export function checkReified<T>(item: any, fullType: Array<any>): item is T {
    const type = fullType[0];
    switch (type) {
        case String:
            return typeof item === "string";
        case Number:
            return typeof item === "number";
        case Boolean:
            return typeof item === "boolean";
        case undefined:
            return typeof item === "undefined";
        case null:
            return !item;
        default:
            return item instanceof type;
    }
}

export function checkIsInterface<T>(item: any, key: string): item is T {
    return (item.constructor as any)["implements" + key]
}

export function tryCastInterface<T>(item: any, key: string): T | null {
    if ((item.constructor as any)["implements" + key]) {
        return item as T;
    } else {
        return null;
    }
}

export function tryCastPrimitive<T>(item: any, key: string): T | null {
    if (typeof item === key) {
        return item as T;
    } else {
        return null;
    }
}

export function tryCastClass<T>(item: any, erasedType: any): T | null {
    if (item instanceof erasedType) {
        return item as T;
    } else {
        return null;
    }
}

export function runOrNull<T, R>(on: T | null | undefined, action: (t: T) => R): R | null {
    if (on !== null && on !== undefined) {
        return action(on);
    } else {
        return null;
    }
}

export function also<T>(item: T, action: (a: T) => void): T {
    action(item);
    return item;
}

export function takeIf<T>(item: T, action: (a: T) => boolean): T | null {
    if (action(item)) return item;
    else return null;
}

export function takeUnless<T>(item: T, action: (a: T) => boolean): T | null {
    if (!action(item)) return item;
    else return null;
}

export function parseIntOrNull(s: string): number | null {
    const r = parseInt(s);
    if (isNaN(r)) return null;
    return r;
}

export function parseFloatOrNull(s: string): number | null {
    const r = parseFloat(s);
    if (isNaN(r)) return null;
    return r;
}

interface ConstructorSizeFor<T> {
    new (size: number): T
}
interface TypedArray {
    byteLength: number
    set(array: this, offset: number): any
}

export function concatTypedArray<T extends TypedArray>(T: ConstructorSizeFor<T>, ...arrays: Array<T>): T {
    let totalLength = 0;
    for (const arr of arrays) {
        totalLength += arr.byteLength;
    }
    const result = new T(totalLength);
    let offset = 0;
    for (const arr of arrays) {
        result.set(arr, offset);
        offset += arr.byteLength;
    }
    return result;
}

// interface Collection<E> extends Iterable<E> {
//     readonly size: number; //Why?  Because that's what Array uses, and we conform to Array.
//     contains(e: E): boolean
//     containsAll(e: E): boolean
// }
// interface MutableCollection<E> extends Collection<E> {
//     add(element: E): boolean
//     remove(element: E): boolean
//     addAll(elements: Collection<E>): boolean
//     removeAll(elements: Collection<E>): boolean
//     retainAll(elements: Collection<E>): boolean
//     clear(): boolean
// }