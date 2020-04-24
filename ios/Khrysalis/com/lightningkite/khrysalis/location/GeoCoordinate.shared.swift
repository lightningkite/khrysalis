//Package: com.lightningkite.khrysalis.location
//Converted using Khrysalis2

import Foundation
import Khrysalis
import RxSwift
import RxRelay



public class GeoCoordinate: Codable, Equatable, Hashable {
    
    public var latitude: Double
    public var longitude: Double
    
    public static func == (lhs: GeoCoordinate, rhs: GeoCoordinate) -> Bool {
        return lhs.latitude == rhs.latitude &&
            lhs.longitude == rhs.longitude
    }
    public func hash(into hasher: inout Hasher) {
        hasher.combine(latitude)
        hasher.combine(longitude)
    }
    public func copy(
        latitude: (Double)? = nil,
        longitude: (Double)? = nil
    ) -> GeoCoordinate {
        return GeoCoordinate(
            latitude: latitude ?? self.latitude,
            longitude: longitude ?? self.longitude
        )
    }
    
    
    public init(latitude: Double, longitude: Double) {
        self.latitude = latitude
        self.longitude = longitude
    }
    convenience public init(_ latitude: Double, _ longitude: Double) {
        self.init(latitude: latitude, longitude: longitude)
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
    
}
 
