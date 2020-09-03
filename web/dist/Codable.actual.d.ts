export interface Codable {
}
export declare type IsCodable = any;
export declare type JsonList = Array<any>;
export declare let JsonList: ArrayConstructor;
export declare type JsonMap = Map<any, any>;
export declare let JsonMap: MapConstructor;
export declare function xAnyToJsonString(this_: (IsCodable | null)): string;
export declare function xStringFromJsonString<T extends IsCodable>(this_: string, T: any): (T | null);
export declare function xStringFromJsonStringUntyped(this_: string): (IsCodable | null);
