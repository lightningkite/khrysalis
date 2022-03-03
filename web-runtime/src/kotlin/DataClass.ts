import {hashAnything, ReifiedType, safeEq} from "./Language";
import {parseObject} from "../Codable";

interface DataClassInterface {
    //static readonly properties: Array<string>
    //static readonly propertiesJsonOverride: Record<string, string>
    //static readonly propertyTypes: (...args: Array<ReifiedType>) => Record<string, ReifiedType>>
}

function hc(this: DataClassInterface): number {
    let hash = 17;
    for (const prop of (this.constructor as any).properties as Array<string>) {
        hash = (Math.imul(31, hash) + hashAnything((this as any)[prop])) | 0;
    }
    return hash;
}

function eq(this: DataClassInterface, other: any): boolean {
    if (other === undefined) return false
    if (typeof other !== 'object') return false
    if (this.constructor !== other.constructor) return false
    for (const prop of (this.constructor as any).properties as Array<string>) {
        if (!safeEq((this as any)[prop], (other as any)[prop])) {
            return false
        }
    }
    return true
}

function ts(this: DataClassInterface): string {
    let out = this.constructor.name
    out += "("
    let isStart = true
    for (const prop of (this.constructor as any).properties as Array<string>) {
        if (isStart) isStart = false
        else out += ", "
        out += `${prop}=${(this as any)[prop]}`
    }
    out += ")"
    return out
}

function tj(this: DataClassInterface): Record<string, any> {
    const result: Record<string, any> = {}
    const propertiesJsonOverride: Record<string, string> | undefined = (this.constructor as any).propertiesJsonOverride
    if(propertiesJsonOverride) {
        for (const prop of (this.constructor as any).properties as Array<string>) {
            result[propertiesJsonOverride[prop] ?? prop] = (this as any)[prop]
        }
    } else {
        for (const prop of (this.constructor as any).properties as Array<string>) {
            result[prop] = (this as any)[prop]
        }
    }
    return result
}

function fj(type: any): (record: Record<string, any>, ...typeArguments: Array<ReifiedType>) => any {
    const propertiesJsonOverride: Record<string, string> | undefined = type.propertiesJsonOverride
    if(propertiesJsonOverride) {
        return (record, args) => {
            const properties: Record<string, ReifiedType> = (type as any).propertyTypes(...args)
            const orderedProperties: Array<string> = type.properties
            return new type(...orderedProperties.map(prop => parseObject(record[propertiesJsonOverride[prop] ?? prop], properties[prop])))
        }
    } else {
        return (record, args) => {
            const properties: Record<string, ReifiedType> = (type as any).propertyTypes(...args)
            const orderedProperties: Array<string> = type.properties
            return new type(...orderedProperties.map(prop => parseObject(record[prop], properties[prop])))
        }
    }
}

function cp(this: DataClassInterface, partial: Record<string, any>): any {
    const type: any = this.constructor
    return new type(...(type.properties as Array<string>).map(x =>
        partial[x] === undefined ? (this as any)[x] : partial[x]
    ))
}

type Constructor = new (...args: any[]) => any;

export interface DataClass {
    hashCode(): number
    equals(other: any): boolean
    toString(): string
    copy(values: Partial<this>): this
    toJSON(): Record<string, any>
    //static fromJSON(): this
}

export function setUpDataClass(type: any) {
    const myImplementations = Object.getOwnPropertyNames(type.prototype)
    if (myImplementations.indexOf("hashCode") === -1) type.prototype.hashCode = hc
    if (myImplementations.indexOf("equals") === -1) type.prototype.equals = eq
    if (myImplementations.indexOf("toString") === -1) type.prototype.toString = ts
    if (myImplementations.indexOf("copy") === -1) type.prototype.copy = cp
    if (myImplementations.indexOf("toJSON") === -1) type.prototype.toJSON = tj
    if (myImplementations.indexOf("fromJSON") === -1) type.fromJSON = fj(type)
}
