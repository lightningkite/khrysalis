//Package: com.lightningkite.khrysalis.location
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



public class GeoAddress: Codable, Equatable, Hashable {
    
    public var coordinate: GeoCoordinate? 
    public var name: String? 
    public var street: String? 
    public var subLocality: String? 
    public var locality: String? 
    public var subAdminArea: String? 
    public var adminArea: String? 
    public var countryName: String? 
    public var postalCode: String? 
    
    public static func == (lhs: GeoAddress, rhs: GeoAddress) -> Bool {
        return lhs.coordinate == rhs.coordinate &&
            lhs.name == rhs.name &&
            lhs.street == rhs.street &&
            lhs.subLocality == rhs.subLocality &&
            lhs.locality == rhs.locality &&
            lhs.subAdminArea == rhs.subAdminArea &&
            lhs.adminArea == rhs.adminArea &&
            lhs.countryName == rhs.countryName &&
            lhs.postalCode == rhs.postalCode
    }
    public func hash(into hasher: inout Hasher) {
        hasher.combine(coordinate)
        hasher.combine(name)
        hasher.combine(street)
        hasher.combine(subLocality)
        hasher.combine(locality)
        hasher.combine(subAdminArea)
        hasher.combine(adminArea)
        hasher.combine(countryName)
        hasher.combine(postalCode)
    }
    public func copy(
        coordinate: (GeoCoordinate? )? = nil,
        name: (String? )? = nil,
        street: (String? )? = nil,
        subLocality: (String? )? = nil,
        locality: (String? )? = nil,
        subAdminArea: (String? )? = nil,
        adminArea: (String? )? = nil,
        countryName: (String? )? = nil,
        postalCode: (String? )? = nil
    ) -> GeoAddress {
        return GeoAddress(
            coordinate: coordinate ?? self.coordinate,
            name: name ?? self.name,
            street: street ?? self.street,
            subLocality: subLocality ?? self.subLocality,
            locality: locality ?? self.locality,
            subAdminArea: subAdminArea ?? self.subAdminArea,
            adminArea: adminArea ?? self.adminArea,
            countryName: countryName ?? self.countryName,
            postalCode: postalCode ?? self.postalCode
        )
    }
    
    
    public func oneLine(withCountry: Bool = false, withZip: Bool = false) -> String {
        var builder = StringBuilder()
        
        if let it = (street) {
            builder.append(it) 
        }
        
        if let it = (locality) {
            builder.append(" ") 
 builder.append(it) 
        }
        
        if let it = (adminArea) {
            builder.append(", ") 
 builder.append(it) 
        }
        if withCountry {
            
            if let it = (adminArea) {
                builder.append(" ") 
 builder.append(it) 
            }
        }
        if withZip {
            
            if let it = (postalCode) {
                builder.append(" ") 
 builder.append(it) 
            }
        }
        return builder.toString().trim()
    }
    public func oneLine(_ withCountry: Bool, _ withZip: Bool = false) -> String {
        return oneLine(withCountry: withCountry, withZip: withZip)
    }
    
    public init(coordinate: GeoCoordinate?  = nil, name: String?  = nil, street: String?  = nil, subLocality: String?  = nil, locality: String?  = nil, subAdminArea: String?  = nil, adminArea: String?  = nil, countryName: String?  = nil, postalCode: String?  = nil) {
        self.coordinate = coordinate
        self.name = name
        self.street = street
        self.subLocality = subLocality
        self.locality = locality
        self.subAdminArea = subAdminArea
        self.adminArea = adminArea
        self.countryName = countryName
        self.postalCode = postalCode
    }
    convenience public init(_ coordinate: GeoCoordinate? , _ name: String?  = nil, _ street: String?  = nil, _ subLocality: String?  = nil, _ locality: String?  = nil, _ subAdminArea: String?  = nil, _ adminArea: String?  = nil, _ countryName: String?  = nil, _ postalCode: String?  = nil) {
        self.init(coordinate: coordinate, name: name, street: street, subLocality: subLocality, locality: locality, subAdminArea: subAdminArea, adminArea: adminArea, countryName: countryName, postalCode: postalCode)
    }
    required public init(from decoder: Decoder) throws {
        let values = try decoder.container(keyedBy: CodingKeys.self)
        coordinate = try values.decodeIfPresent(GeoCoordinate? .self, forKey: .coordinate) ?? nil
        name = try values.decodeIfPresent(String? .self, forKey: .name) ?? nil
        street = try values.decodeIfPresent(String? .self, forKey: .street) ?? nil
        subLocality = try values.decodeIfPresent(String? .self, forKey: .subLocality) ?? nil
        locality = try values.decodeIfPresent(String? .self, forKey: .locality) ?? nil
        subAdminArea = try values.decodeIfPresent(String? .self, forKey: .subAdminArea) ?? nil
        adminArea = try values.decodeIfPresent(String? .self, forKey: .adminArea) ?? nil
        countryName = try values.decodeIfPresent(String? .self, forKey: .countryName) ?? nil
        postalCode = try values.decodeIfPresent(String? .self, forKey: .postalCode) ?? nil
    }
    
    enum CodingKeys: String, CodingKey {
        case coordinate = "coordinate"
        case name = "name"
        case street = "street"
        case subLocality = "subLocality"
        case locality = "locality"
        case subAdminArea = "subAdminArea"
        case adminArea = "adminArea"
        case countryName = "countryName"
        case postalCode = "postalCode"
    }
    
    public func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encodeIfPresent(self.coordinate, forKey: .coordinate)
        try container.encodeIfPresent(self.name, forKey: .name)
        try container.encodeIfPresent(self.street, forKey: .street)
        try container.encodeIfPresent(self.subLocality, forKey: .subLocality)
        try container.encodeIfPresent(self.locality, forKey: .locality)
        try container.encodeIfPresent(self.subAdminArea, forKey: .subAdminArea)
        try container.encodeIfPresent(self.adminArea, forKey: .adminArea)
        try container.encodeIfPresent(self.countryName, forKey: .countryName)
        try container.encodeIfPresent(self.postalCode, forKey: .postalCode)
    }
    
}
 
