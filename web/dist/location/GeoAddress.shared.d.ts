import { GeoCoordinate } from './GeoCoordinate.shared';
export declare class GeoAddress {
    readonly coordinate: (GeoCoordinate | null);
    readonly name: (string | null);
    readonly street: (string | null);
    readonly subLocality: (string | null);
    readonly locality: (string | null);
    readonly subAdminArea: (string | null);
    readonly adminArea: (string | null);
    readonly countryName: (string | null);
    readonly postalCode: (string | null);
    constructor(coordinate?: (GeoCoordinate | null), name?: (string | null), street?: (string | null), subLocality?: (string | null), locality?: (string | null), subAdminArea?: (string | null), adminArea?: (string | null), countryName?: (string | null), postalCode?: (string | null));
    static fromJson(obj: any): GeoAddress;
    toJSON(): object;
    hashCode(): number;
    equals(other: any): boolean;
    toString(): string;
    copy(coordinate?: (GeoCoordinate | null), name?: (string | null), street?: (string | null), subLocality?: (string | null), locality?: (string | null), subAdminArea?: (string | null), adminArea?: (string | null), countryName?: (string | null), postalCode?: (string | null)): GeoAddress;
    oneLine(withCountry?: boolean, withZip?: boolean): string;
}
