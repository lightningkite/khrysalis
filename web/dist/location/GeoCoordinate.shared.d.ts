import { Codable } from '../Codable.actual';
export declare class GeoCoordinate implements Codable {
    static implementsInterfaceComLightningkiteKhrysalisCodable: boolean;
    readonly latitude: number;
    readonly longitude: number;
    constructor(latitude: number, longitude: number);
    static fromJson(obj: any): GeoCoordinate;
    toJSON(): object;
    hashCode(): number;
    equals(other: any): boolean;
    toString(): string;
    copy(latitude?: number, longitude?: number): GeoCoordinate;
}
