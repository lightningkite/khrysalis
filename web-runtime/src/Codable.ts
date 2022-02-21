import {ReifiedType} from "./kotlin/Language";

export interface Codable {
}
export type IsCodable = any;

export type JsonList = Array<any>;
export let JsonList = Array;

export type JsonMap = Map<any, any>;
export let JsonMap = Map;

export namespace JSON2 {
    export function parse<TYPE>(text: string, asType: ReifiedType<TYPE>): TYPE {
        return parseObject(JSON.parse(text), asType)
    }
}

export function parseObject<TYPE>(item: any, asType: ReifiedType<TYPE>): TYPE {
    const parser = asType[0].fromJSON as (item: any, typeArguments: Array<ReifiedType>) => any
    if (typeof parser !== "function") {
        throw Error(`Type ${asType[0]} has no function fromJSON!`)
    }
    return parser(item, asType.slice(1))
}

(String as any).fromJSON = (value: any) => value;
(Number as any).fromJSON = (value: any) => typeof value === "string" ? parseFloat(value) : value;
(Boolean as any).fromJSON = (value: any) => typeof value === "string" ? value === "true" : value;
(Array as any).fromJSON = (value: any, typeArguments: Array<ReifiedType>) => { return (value as Array<any>).map(x => parseObject(x, typeArguments[0])) };
(Map as any).fromJSON = (value: any, typeArguments: Array<ReifiedType>) => {
    let asObj = value as object;
    let map = new Map<any, any>();
    if (typeArguments[0][0] === String) {
        for (const key of Object.keys(asObj)) {
            map.set(key, parseObject((asObj as any)[key], typeArguments[1]));
        }
    } else {
        for (const key of Object.keys(asObj)) {
            map.set(parseObject(key, typeArguments[0]), parseObject((asObj as any)[key], typeArguments[1]));
        }
    }
    return map;
};
(Date as any).fromJSON = (value: any) => new Date(value as string);
(Map as any).toJSON = (value: Map<any, any>) => Object.fromEntries(value);