import { GeoCoordinate } from './GeoCoordinate.shared';
export declare class LocationResult {
    readonly coordinate: GeoCoordinate;
    readonly accuracyMeters: number;
    readonly altitudeMeters: number;
    readonly altitudeAccuracyMeters: number;
    readonly headingFromNorth: number;
    readonly speedMetersPerSecond: number;
    constructor(coordinate?: GeoCoordinate, accuracyMeters?: number, altitudeMeters?: number, altitudeAccuracyMeters?: number, headingFromNorth?: number, speedMetersPerSecond?: number);
    hashCode(): number;
    equals(other: any): boolean;
    toString(): string;
    copy(coordinate?: GeoCoordinate, accuracyMeters?: number, altitudeMeters?: number, altitudeAccuracyMeters?: number, headingFromNorth?: number, speedMetersPerSecond?: number): LocationResult;
}
