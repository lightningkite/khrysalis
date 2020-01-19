//Package: com.lightningkite.kwift.location
//Converted using Kwift2

import Foundation
import RxSwift
import RxRelay



public class GeoCoordinate: Equatable, Hashable {
    
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
}
 
