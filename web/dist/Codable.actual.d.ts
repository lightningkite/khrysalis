export interface Codable {
}
export declare type IsCodable = any;
export declare type JsonList = Array<any>;
export declare let JsonList: ArrayConstructor;
export declare type JsonMap = Map<any, any>;
export declare let JsonMap: MapConstructor;
export declare function kotlinAnyToJsonString(this_: (IsCodable | null)): string;
export declare function kotlinStringFromJsonString<T extends IsCodable>(this_: string, T: any): (T | null);
export declare function kotlinStringFromJsonStringUntyped(this_: string): (IsCodable | null);
