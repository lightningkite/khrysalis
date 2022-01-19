
export interface Codable {
}
export type IsCodable = any;

export type JsonList = Array<any>;
export let JsonList = Array;

export type JsonMap = Map<any, any>;
export let JsonMap = Map;

export function parse<TYPE>(item: any, asType: Array<any>): TYPE {
    const parser = asType[0].fromJSON as (item: any, typeArguments: Array<any>) => any
    if(typeof parser !== "function"){
        console.log(asType[0])
        throw Error(`Type ${asType[0]} has no function fromJSON!`)
    }
    return parser(item, asType.slice(1))
}

export function parseUntyped(json: string): any {
    return JSON.parse(json, function(key, value) {
        if(typeof value === 'object' && value !== null){
            return new Map(Object.entries(value));
        } else {
            return value;
        }
    })
}

(String as any).fromJSON = (value: any) => value;
(Number as any).fromJSON = (value: any) => typeof value === "string" ? parseFloat(value) : value;
(Boolean as any).fromJSON = (value: any) => typeof value === "string" ? value === "true" : value;
(Array as any).fromJSON = (value: any, typeArguments: Array<any>) => { return (value as Array<any>).map(x => parse(x, typeArguments[0])) };
(Map as any).fromJSON = (value: any, typeArguments: Array<any>) => {
    let asObj = value as object;
    let map = new Map<any, any>();
    if (typeArguments[0] === String) {
        for (const key of Object.keys(asObj)) {
            map.set(key, parse((asObj as any)[key], typeArguments[1]));
        }
    } else {
        for (const key of Object.keys(asObj)) {
            map.set(parse(key, typeArguments[0]), parse((asObj as any)[key], typeArguments[1]));
        }
    }
    return map;
};
(Date as any).fromJSON = (value: any) => new Date(value as string);
(Map as any).toJSON = (value: Map<any, any>) => Object.fromEntries(value);
