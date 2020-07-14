// Generated by Khrysalis Swift converter - this file will be overwritten.
// File: location/LocationResult.shared.kt
// Package: com.lightningkite.khrysalis.location
import Foundation

public class LocationResult : KDataClass {
    public var coordinate: GeoCoordinate
    public var accuracyMeters: Double
    public var altitudeMeters: Double
    public var altitudeAccuracyMeters: Double
    public var headingFromNorth: Double
    public var speedMetersPerSecond: Double
    public init(coordinate: GeoCoordinate = GeoCoordinate(latitude: 0.0, longitude: 0.0), accuracyMeters: Double = 100.0, altitudeMeters: Double = 0.0, altitudeAccuracyMeters: Double = 100.0, headingFromNorth: Double = 0.0, speedMetersPerSecond: Double = 0.0) {
        self.coordinate = coordinate
        self.accuracyMeters = accuracyMeters
        self.altitudeMeters = altitudeMeters
        self.altitudeAccuracyMeters = altitudeAccuracyMeters
        self.headingFromNorth = headingFromNorth
        self.speedMetersPerSecond = speedMetersPerSecond
    }
    public func hash(into hasher: inout Hasher) {
        hasher.combine(coordinate)
        hasher.combine(accuracyMeters)
        hasher.combine(altitudeMeters)
        hasher.combine(altitudeAccuracyMeters)
        hasher.combine(headingFromNorth)
        hasher.combine(speedMetersPerSecond)
    }
    public static func == (lhs: LocationResult, rhs: LocationResult) -> Bool { return lhs.coordinate == rhs.coordinate && lhs.accuracyMeters == rhs.accuracyMeters && lhs.altitudeMeters == rhs.altitudeMeters && lhs.altitudeAccuracyMeters == rhs.altitudeAccuracyMeters && lhs.headingFromNorth == rhs.headingFromNorth && lhs.speedMetersPerSecond == rhs.speedMetersPerSecond }
    public var description: String { return "LocationResult(coordinate = \(self.coordinate), accuracyMeters = \(self.accuracyMeters), altitudeMeters = \(self.altitudeMeters), altitudeAccuracyMeters = \(self.altitudeAccuracyMeters), headingFromNorth = \(self.headingFromNorth), speedMetersPerSecond = \(self.speedMetersPerSecond))" }
    public func copy(coordinate: GeoCoordinate? = nil, accuracyMeters: Double? = nil, altitudeMeters: Double? = nil, altitudeAccuracyMeters: Double? = nil, headingFromNorth: Double? = nil, speedMetersPerSecond: Double? = nil) -> LocationResult { return LocationResult(coordinate: coordinate ?? self.coordinate, accuracyMeters: accuracyMeters ?? self.accuracyMeters, altitudeMeters: altitudeMeters ?? self.altitudeMeters, altitudeAccuracyMeters: altitudeAccuracyMeters ?? self.altitudeAccuracyMeters, headingFromNorth: headingFromNorth ?? self.headingFromNorth, speedMetersPerSecond: speedMetersPerSecond ?? self.speedMetersPerSecond) }
}


