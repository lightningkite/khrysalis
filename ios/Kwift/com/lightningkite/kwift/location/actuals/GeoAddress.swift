//
//  GeoAddress.swift
//  Kwift
//
//  Created by Joseph Ivie on 10/28/19.
//  Copyright © 2019 Lightning Kite. All rights reserved.
//

import Foundation
import CoreLocation

public class GeoAddress {
    
    public let mark: CLPlacemark?
    public init(_ mark: CLPlacemark) {
        self.mark = mark
    }
    public init() {
        self.mark = nil
    }
    
    public var street: String {
        return "\(mark?.subThoroughfare ?? "") \(mark?.thoroughfare ?? "")"
    }
    
    public var adminArea: String? {
        return mark?.administrativeArea
    }
    
    public var subAdminArea: String? {
        return mark?.subAdministrativeArea
    }
    
    public var locality: String? {
        return mark?.locality
    }
    
    public var subLocality: String? {
        return mark?.subLocality
    }
    
    public var countryName: String? {
        return mark?.country
    }
    
    public var coordinate: GeoCoordinate? {
        return self.mark?.location?.coordinate.toKwift()
    }
    
    public func oneLine(_ withCountry: Bool, _ withZip: Bool = false) -> String {
        return oneLine(withCountry: withCountry, withZip: withZip)
    }
    public func oneLine(withCountry: Bool = false, withZip: Bool = false) -> String {
        var result = self.street
        if let x = self.mark?.locality, let y = self.mark?.administrativeArea {
            result += " "
            result += x
            result += ", "
            result += y
        }
        if withCountry, let x = self.mark?.country {
            result += " "
            result += x
        }
        if withZip, let x = self.mark?.postalCode {
            result += " "
            result += x
        }
        return result.trim()
    }
}
