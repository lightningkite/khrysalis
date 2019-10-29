//Package: com.lightningkite.kwift.location.shared
//Converted using Kwift2

import Foundation



public class GeoLocation: Equatable, Hashable {
    
    public var coordinate: GeoCoordinate
    public var name: String?
    public var street: String?
    public var subLocality: String?
    public var locality: String?

    
    public static func == (lhs: GeoLocation, rhs: GeoLocation) -> Bool {
        return lhs.coordinate == rhs.coordinate &&
            lhs.name == rhs.name &&
            lhs.street == rhs.street &&
            lhs.subLocality == rhs.subLocality &&
            lhs.locality == rhs.locality
    }
    public func hash(into hasher: inout Hasher) {
        hasher.combine(coordinate)
        hasher.combine(name)
        hasher.combine(street)
        hasher.combine(subLocality)
        hasher.combine(locality)
    }
    public func copy(
        coordinate: (GeoCoordinate)? = nil,
        name: (String?)? = nil,
        street: (String?)? = nil,
        subLocality: (String?)? = nil,
        locality: (String?
)? = nil
    ) -> GeoLocation {
        return GeoLocation(
            coordinate: coordinate ?? self.coordinate,
            name: name ?? self.name,
            street: street ?? self.street,
            subLocality: subLocality ?? self.subLocality,
            locality: locality ?? self.locality
        )
    }
    
    
    public init(coordinate: GeoCoordinate, name: String?, street: String?, subLocality: String?, locality: String?
) {
        self.coordinate = coordinate
        self.name = name
        self.street = street
        self.subLocality = subLocality
        self.locality = locality
    }
    convenience public init(_ coordinate: GeoCoordinate, _ name: String?, _ street: String?, _ subLocality: String?, _ locality: String?
) {
        self.init(coordinate: coordinate, name: name, street: street, subLocality: subLocality, locality: locality)
    }
}
 
