//Package: com.lightningkite.kwift.location.shared
//Converted using Kwift2

import Foundation



public class LocationResult: Equatable, Hashable {
    
    public var coordinate: GeoCoordinate
    public var accuracyMeters: Double
    public var altitudeMeters: Double
    public var altitudeAccuracyMeters: Double
    public var headingFromNorth: Double
    public var speedMetersPerSecond: Double
    
    public static func == (lhs: LocationResult, rhs: LocationResult) -> Bool {
        return lhs.coordinate == rhs.coordinate &&
            lhs.accuracyMeters == rhs.accuracyMeters &&
            lhs.altitudeMeters == rhs.altitudeMeters &&
            lhs.altitudeAccuracyMeters == rhs.altitudeAccuracyMeters &&
            lhs.headingFromNorth == rhs.headingFromNorth &&
            lhs.speedMetersPerSecond == rhs.speedMetersPerSecond
    }
    public func hash(into hasher: inout Hasher) {
        hasher.combine(coordinate)
        hasher.combine(accuracyMeters)
        hasher.combine(altitudeMeters)
        hasher.combine(altitudeAccuracyMeters)
        hasher.combine(headingFromNorth)
        hasher.combine(speedMetersPerSecond)
    }
    public func copy(
        coordinate: (GeoCoordinate)? = nil,
        accuracyMeters: (Double)? = nil,
        altitudeMeters: (Double)? = nil,
        altitudeAccuracyMeters: (Double)? = nil,
        headingFromNorth: (Double)? = nil,
        speedMetersPerSecond: (Double)? = nil
    ) -> LocationResult {
        return LocationResult(
            coordinate: coordinate ?? self.coordinate,
            accuracyMeters: accuracyMeters ?? self.accuracyMeters,
            altitudeMeters: altitudeMeters ?? self.altitudeMeters,
            altitudeAccuracyMeters: altitudeAccuracyMeters ?? self.altitudeAccuracyMeters,
            headingFromNorth: headingFromNorth ?? self.headingFromNorth,
            speedMetersPerSecond: speedMetersPerSecond ?? self.speedMetersPerSecond
        )
    }
    
    
    public init(coordinate: GeoCoordinate = GeoCoordinate(0.0, 0.0), accuracyMeters: Double = 100.0, altitudeMeters: Double = 0.0, altitudeAccuracyMeters: Double = 100.0, headingFromNorth: Double = 0.0, speedMetersPerSecond: Double = 0.0) {
        self.coordinate = coordinate
        self.accuracyMeters = accuracyMeters
        self.altitudeMeters = altitudeMeters
        self.altitudeAccuracyMeters = altitudeAccuracyMeters
        self.headingFromNorth = headingFromNorth
        self.speedMetersPerSecond = speedMetersPerSecond
    }
    convenience public init(_ coordinate: GeoCoordinate, _ accuracyMeters: Double = 100.0, _ altitudeMeters: Double = 0.0, _ altitudeAccuracyMeters: Double = 100.0, _ headingFromNorth: Double = 0.0, _ speedMetersPerSecond: Double = 0.0) {
        self.init(coordinate: coordinate, accuracyMeters: accuracyMeters, altitudeMeters: altitudeMeters, altitudeAccuracyMeters: altitudeAccuracyMeters, headingFromNorth: headingFromNorth, speedMetersPerSecond: speedMetersPerSecond)
    }
}
 
