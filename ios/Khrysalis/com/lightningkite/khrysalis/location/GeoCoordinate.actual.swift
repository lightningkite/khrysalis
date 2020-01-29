//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation
import CoreLocation

//--- GeoCoordinate.distanceToMiles(GeoCoordinate)
public extension GeoCoordinate {
    func distanceToMiles(_ other: GeoCoordinate) -> Double {
        return CLLocation(latitude: self.latitude, longitude: self.longitude).distance(from: CLLocation(latitude: other.latitude, longitude: other.longitude)) * 0.000621371

    }
    func distanceToMiles(other: GeoCoordinate) -> Double {
        return distanceToMiles(other)
    }
}
