//Package: com.lightningkite.kwift.shared
//Converted using Kwift2

import Foundation



public final class LocationResult: Equatable, Hashable {
    
    public var latitude: Double
    public var longitude: Double
    public var accuracyMeters: Double
    public var altitudeMeters: Double
    public var altitudeAccuracyMeters: Double
    public var headingFromNorth: Double
    public var speedMetersPerSecond: Double
    public static func == (lhs: LocationResult, rhs: LocationResult) -> Bool {
        return lhs.latitude == rhs.latitude &&
            lhs.longitude == rhs.longitude &&
            lhs.accuracyMeters == rhs.accuracyMeters &&
            lhs.altitudeMeters == rhs.altitudeMeters &&
            lhs.altitudeAccuracyMeters == rhs.altitudeAccuracyMeters &&
            lhs.headingFromNorth == rhs.headingFromNorth &&
            lhs.speedMetersPerSecond == rhs.speedMetersPerSecond
    }
    public func hash(into hasher: inout Hasher) {
        hasher.combine(latitude)
        hasher.combine(longitude)
        hasher.combine(accuracyMeters)
        hasher.combine(altitudeMeters)
        hasher.combine(altitudeAccuracyMeters)
        hasher.combine(headingFromNorth)
        hasher.combine(speedMetersPerSecond)
    }
    public func copy(
        latitude: (Double)? = nil,
        longitude: (Double)? = nil,
        accuracyMeters: (Double)? = nil,
        altitudeMeters: (Double)? = nil,
        altitudeAccuracyMeters: (Double)? = nil,
        headingFromNorth: (Double)? = nil,
        speedMetersPerSecond: (Double)? = nil
    ) -> LocationResult {
        return LocationResult(
            latitude: latitude ?? self.latitude,
            longitude: longitude ?? self.longitude,
            accuracyMeters: accuracyMeters ?? self.accuracyMeters,
            altitudeMeters: altitudeMeters ?? self.altitudeMeters,
            altitudeAccuracyMeters: altitudeAccuracyMeters ?? self.altitudeAccuracyMeters,
            headingFromNorth: headingFromNorth ?? self.headingFromNorth,
            speedMetersPerSecond: speedMetersPerSecond ?? self.speedMetersPerSecond
        )
    }
    
    public init(latitude: Double = 0.0, longitude: Double = 0.0, accuracyMeters: Double = 100.0, altitudeMeters: Double = 0.0, altitudeAccuracyMeters: Double = 100.0, headingFromNorth: Double = 0.0, speedMetersPerSecond: Double = 0.0) {
        self.latitude = latitude
        self.longitude = longitude
        self.accuracyMeters = accuracyMeters
        self.altitudeMeters = altitudeMeters
        self.altitudeAccuracyMeters = altitudeAccuracyMeters
        self.headingFromNorth = headingFromNorth
        self.speedMetersPerSecond = speedMetersPerSecond
    }
    convenience public init(_ latitude: Double = 0.0, _ longitude: Double = 0.0, _ accuracyMeters: Double = 100.0, _ altitudeMeters: Double = 0.0, _ altitudeAccuracyMeters: Double = 100.0, _ headingFromNorth: Double = 0.0, _ speedMetersPerSecond: Double = 0.0) {
        self.init(latitude: latitude, longitude: longitude, accuracyMeters: accuracyMeters, altitudeMeters: altitudeMeters, altitudeAccuracyMeters: altitudeAccuracyMeters, headingFromNorth: headingFromNorth, speedMetersPerSecond: speedMetersPerSecond)
    }
    
}
 
