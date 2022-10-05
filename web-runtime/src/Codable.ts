import {ReifiedType} from "./kotlin/Language";
import {Instant, LocalDate, LocalTime, OffsetDateTime, ZonedDateTime} from "@js-joda/core";

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
    if(item === undefined) return undefined as unknown as TYPE
    if(item === null) return null as unknown as TYPE
    switch(asType[0]) {
        case String:
        case Number:
        case Boolean:
            return item
        case Date:
            return new Date(item as string) as unknown as TYPE;
        case Array:
            return (item as Array<any>).map(x => parseObject(x, asType[1])) as unknown as TYPE
        case Tuple:
            const arr = item as Array<any>
            return asType.slice(1).map((x, index) => parseObject(arr[index], x)) as unknown as TYPE
        case Set:
            return new Set((item as Array<any>).map(x => parseObject(x, asType[1]))) as unknown as TYPE
        case Map:
            let asObj = item as object;
            let map = new Map<any, any>();
            if (asType[1] === String) {
                for (const key of Object.keys(asObj)) {
                    map.set(key, parseObject((asObj as any)[key], asType[2]));
                }
            } else {
                for (const key of Object.keys(asObj)) {
                    map.set(parseObject(key, asType[1]), parseObject((asObj as any)[key], asType[2]));
                }
            }
            return map as unknown as TYPE;
    }
    const parser = asType[0].fromJSON as (item: any, typeArguments: Array<ReifiedType>) => any
    if (typeof parser !== "function") {
        throw Error(`Type ${asType[0]} has no function fromJSON!`)
    }
    return parser(item, asType.slice(1))
}

export const Tuple = {};

(Map.prototype as any).toJSON = function(this: Map<any, any>) { return Object.fromEntries(this) };
(Set.prototype as any).toJSON = function(this: Map<any, any>) { return [...this] };

(Instant as any).fromJSON = (value: string) => Instant.parse(value);
(ZonedDateTime as any).fromJSON = (value: string) => ZonedDateTime.parse(value);
(LocalDate as any).fromJSON = (value: string) => LocalDate.parse(value);
(LocalTime as any).fromJSON = (value: string) => LocalTime.parse(value);
(OffsetDateTime as any).fromJSON = (value: string) => OffsetDateTime.parse(value);
