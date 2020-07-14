// Generated by Khrysalis Swift converter - this file will be overwritten.
// File: location/GeoCoordinate.shared.kt
// Package: com.lightningkite.khrysalis.location
import Foundation

public class GeoCoordinate : Codable, KDataClass {
    public var latitude: Double
    public var longitude: Double
    public init(latitude: Double, longitude: Double) {
        self.latitude = latitude
        self.longitude = longitude
    }
    required public init(from decoder: Decoder) throws {
        let values = try decoder.container(keyedBy: CodingKeys.self)
        latitude = try values.decodeDouble(forKey: .latitude)
        longitude = try values.decodeDouble(forKey: .longitude)
    }
    
    enum CodingKeys: String, CodingKey {
        case latitude = "latitude"
        case longitude = "longitude"
    }
    
    public func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encode(self.latitude, forKey: .latitude)
        try container.encode(self.longitude, forKey: .longitude)
    }
    
    public func hash(into hasher: inout Hasher) {
        hasher.combine(latitude)
        hasher.combine(longitude)
    }
    public static func == (lhs: GeoCoordinate, rhs: GeoCoordinate) -> Bool { return lhs.latitude == rhs.latitude && lhs.longitude == rhs.longitude }
    public var description: String { return "GeoCoordinate(latitude = \(self.latitude), longitude = \(self.longitude))" }
    public func copy(latitude: Double? = nil, longitude: Double? = nil) -> GeoCoordinate { return GeoCoordinate(latitude: latitude ?? self.latitude, longitude: longitude ?? self.longitude) }
}


