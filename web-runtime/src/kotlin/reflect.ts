
export type TProperty1<T, V> = keyof { [P in keyof T as T[P] extends V ? P : never]: P } & keyof T & string;

export function reflectiveGet<T, V>(root: T, prop: TProperty1<T, V>): V {
    return root[prop] as unknown as V
}
export function reflectiveSet<T, V>(root: T, prop: TProperty1<T, V>, value: V) {
    root[prop] = value as unknown as T[typeof prop]
}