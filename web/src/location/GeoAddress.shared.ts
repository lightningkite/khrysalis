// Generated by Khrysalis TypeScript converter - this file will be overwritten.
// File: location/GeoAddress.shared.kt
// Package: com.lightningkite.khrysalis.location
// FQImport: com.lightningkite.khrysalis.location.GeoAddress.postalCode TS postalCode
// FQImport: com.lightningkite.khrysalis.location.GeoAddress.street TS street
// FQImport: com.lightningkite.khrysalis.location.GeoAddress.oneLine.<anonymous>.it TS it
// FQImport: com.lightningkite.khrysalis.location.GeoAddress.oneLine.builder TS builder
// FQImport: java.lang.StringBuilder.toString TS toString
// FQImport: com.lightningkite.khrysalis.location.GeoAddress.adminArea TS adminArea
// FQImport: com.lightningkite.khrysalis.location.GeoCoordinate TS GeoCoordinate
// FQImport: com.lightningkite.khrysalis.location.GeoAddress.oneLine.withCountry TS withCountry
// FQImport: com.lightningkite.khrysalis.Codable TS Codable
// FQImport: com.lightningkite.khrysalis.location.GeoAddress.locality TS locality
// FQImport: com.lightningkite.khrysalis.location.GeoAddress.oneLine.withZip TS withZip
import { hashString } from '../Kotlin'
import { StringBuilder } from '../kotlin/kotlin.text'
import { GeoCoordinate } from './GeoCoordinate.shared'
import { parse as parseJsonTyped } from '../net/jsonParsing'
import { Codable } from '../Codable.actual'

//! Declares com.lightningkite.khrysalis.location.GeoAddress
export class GeoAddress implements Codable {
    public static implementsInterfaceComLightningkiteKhrysalisCodable = true;
    public readonly coordinate: (GeoCoordinate | null);
    public readonly name: (string | null);
    public readonly street: (string | null);
    public readonly subLocality: (string | null);
    public readonly locality: (string | null);
    public readonly subAdminArea: (string | null);
    public readonly adminArea: (string | null);
    public readonly countryName: (string | null);
    public readonly postalCode: (string | null);
    public constructor(coordinate: (GeoCoordinate | null) = null, name: (string | null) = null, street: (string | null) = null, subLocality: (string | null) = null, locality: (string | null) = null, subAdminArea: (string | null) = null, adminArea: (string | null) = null, countryName: (string | null) = null, postalCode: (string | null) = null) {
        this.coordinate = coordinate;
        this.name = name;
        this.street = street;
        this.subLocality = subLocality;
        this.locality = locality;
        this.subAdminArea = subAdminArea;
        this.adminArea = adminArea;
        this.countryName = countryName;
        this.postalCode = postalCode;
    }
    public static fromJson(obj: any): GeoAddress { return new GeoAddress(
            parseJsonTyped(obj["coordinate"], [GeoCoordinate]) as GeoCoordinate, 
            parseJsonTyped(obj["name"], [String]) as string, 
            parseJsonTyped(obj["street"], [String]) as string, 
            parseJsonTyped(obj["subLocality"], [String]) as string, 
            parseJsonTyped(obj["locality"], [String]) as string, 
            parseJsonTyped(obj["subAdminArea"], [String]) as string, 
            parseJsonTyped(obj["adminArea"], [String]) as string, 
            parseJsonTyped(obj["countryName"], [String]) as string, 
            parseJsonTyped(obj["postalCode"], [String]) as string
    ) }
    public toJson(): object { return {
            coordinate: this.coordinate, 
            name: this.name, 
            street: this.street, 
            subLocality: this.subLocality, 
            locality: this.locality, 
            subAdminArea: this.subAdminArea, 
            adminArea: this.adminArea, 
            countryName: this.countryName, 
            postalCode: this.postalCode
    } }
    public hashCode(): number {
        let hash = 17;
        hash = 31 * hash + this.coordinate?.hashCode() ?? 0;
        hash = 31 * hash + hashString(this.name);
        hash = 31 * hash + hashString(this.street);
        hash = 31 * hash + hashString(this.subLocality);
        hash = 31 * hash + hashString(this.locality);
        hash = 31 * hash + hashString(this.subAdminArea);
        hash = 31 * hash + hashString(this.adminArea);
        hash = 31 * hash + hashString(this.countryName);
        hash = 31 * hash + hashString(this.postalCode);
        return hash;
    }
    public equals(other: any): boolean { return other instanceof GeoAddress && (this.coordinate?.equals(other.coordinate) ?? other.coordinate === null) && this.name === other.name && this.street === other.street && this.subLocality === other.subLocality && this.locality === other.locality && this.subAdminArea === other.subAdminArea && this.adminArea === other.adminArea && this.countryName === other.countryName && this.postalCode === other.postalCode }
    public toString(): string { return `GeoAddress(coordinate = ${this.coordinate}, name = ${this.name}, street = ${this.street}, subLocality = ${this.subLocality}, locality = ${this.locality}, subAdminArea = ${this.subAdminArea}, adminArea = ${this.adminArea}, countryName = ${this.countryName}, postalCode = ${this.postalCode})` }
    public copy(coordinate: (GeoCoordinate | null) = this.coordinate, name: (string | null) = this.name, street: (string | null) = this.street, subLocality: (string | null) = this.subLocality, locality: (string | null) = this.locality, subAdminArea: (string | null) = this.subAdminArea, adminArea: (string | null) = this.adminArea, countryName: (string | null) = this.countryName, postalCode: (string | null) = this.postalCode) { return new GeoAddress(coordinate, name, street, subLocality, locality, subAdminArea, adminArea, countryName, postalCode); }
    
    public oneLine(withCountry: boolean = false, withZip: boolean = false): string {
        const builder = new StringBuilder();
        
        const temp38 = this.street;
        if(temp38 !== null) ((it) => builder.value += it)(temp38);
        const temp40 = this.locality;
        if(temp40 !== null) ((it) => {
                builder.value += ' ';
                return builder.value += it;
        })(temp40);
        const temp42 = this.adminArea;
        if(temp42 !== null) ((it) => {
                builder.value += ", ";
                return builder.value += it;
        })(temp42);
        if (withCountry) {
            const temp45 = this.adminArea;
            if(temp45 !== null) ((it) => {
                    builder.value += ' ';
                    return builder.value += it;
            })(temp45);
        }
        if (withZip) {
            const temp47 = this.postalCode;
            if(temp47 !== null) ((it) => {
                    builder.value += ' ';
                    return builder.value += it;
            })(temp47);
        }
        return builder.toString().trim();
    }
}

