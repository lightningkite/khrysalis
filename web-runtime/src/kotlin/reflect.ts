
export type TProperty1<T, V> = keyof {
    [P in keyof T as T[P] extends V? P: never]: any
}

export function reflectiveGet<T, V>(root: T, prop: TProperty1<T, V>): V {
    return root[prop] as unknown as V
}
export function reflectiveSet<T, V>(root: T, prop: TProperty1<T, V>, value: V) {
    root[prop] = value as unknown as T[typeof prop]
}

interface Sample {
    x: number
    y: Sample | null
}
function testbed() {
    const s: Sample = { x: 1, y: { x: 2, y: { x: 3, y: null } } }
    reflectiveGet(s, "x")
}