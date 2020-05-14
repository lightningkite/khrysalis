// Generated by Khrysalis TypeScript converter - this file will be overwritten.
// File: location/ViewDependency.location.actual.kt
// Package: com.lightningkite.khrysalis.location
import { LocationCache } from './ViewDependency.location.actual'
import { ViewDependency } from './../views/ViewDependency.actual'
import { LocationResult } from './LocationResult.shared'

//! Declares com.lightningkite.khrysalis.location.requestLocation
export function ComLightningkiteKhrysalisAndroidActivityAccessRequestLocation(this_RequestLocation: ViewDependency, accuracyBetterThanMeters: number = 10.0, timeoutInSeconds: number = 100.0, onResult: (a: (LocationResult | null), b: (string | null)) => void){}


//! Declares com.lightningkite.khrysalis.location.LocationCache
export class LocationCache {
    public location: LocationResult;
    public timeSinceCall: Date;
    public accuracy: number;
    public constructor( location: LocationResult,  timeSinceCall: Date,  accuracy: number) {
        this.location = location;
        this.timeSinceCall = timeSinceCall;
        this.accuracy = accuracy;
    }
    public hashCode(): number {
        let hash = 17;
        hash = 31 * hash + this.location.hashCode();
        hash = 31 * hash + this.timeSinceCall.hashCode();
        hash = 31 * hash + Math.floor(this.accuracy);
        return hash;
    }
    public equals(other: any): boolean { return other instanceof LocationCache && this.location.equals(other.location) && this.timeSinceCall.equals(other.timeSinceCall) && this.accuracy === other.accuracy }
    public toString(): string { return `LocationCache(location = ${this.location}, timeSinceCall = ${this.timeSinceCall}, accuracy = ${this.accuracy})` }
    public copy(location: LocationResult = this.location, timeSinceCall: Date = this.timeSinceCall, accuracy: number = this.accuracy) { return new LocationCache(location, timeSinceCall, accuracy); }
}

//! Declares com.lightningkite.khrysalis.location.lastLocation
export let _lastLocation: (LocationCache | null) = null;


//! Declares com.lightningkite.khrysalis.location.requestLocationCached
export function ComLightningkiteKhrysalisAndroidActivityAccessRequestLocationCached(this_RequestLocationCached: ViewDependency, accuracyBetterThanMeters: number = 10.0, timeoutInSeconds: number = 100.0, onResult: (a: (LocationResult | null), b: (string | null)) => void){}

