//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation
import CoreLocation

//--- GeoAddress

public class GeoAddress {
    
    public let mark: CLPlacemark?
    public init(_ mark: CLPlacemark) {
        self.mark = mark
    }
    
    //--- GeoAddress()
    public init() {
        self.mark = nil
    }

    //--- GeoAddress.street
    public var street: String? {
        if let x = mark?.subThoroughfare, let y = mark?.thoroughfare {
            return "\(x) \(y)"
        }
        return nil
    }

    //--- GeoAddress.adminArea
    public var adminArea: String? {
        return mark?.administrativeArea
    }
    
    public var subAdminArea: String? {
        return mark?.subAdministrativeArea
    }

    //--- GeoAddress.locality
    public var locality: String? {
        return mark?.locality
    }
    
    public var subLocality: String? {
        return mark?.subLocality
    }

    //--- GeoAddress.countryName
    public var countryName: String? {
        return mark?.country
    }

    //--- GeoAddress.postalCode
    public var postalCode: String? {
        return mark?.postalCode
    }

    //--- GeoAddress.coordinate
    public var coordinate: GeoCoordinate? {
        return self.mark?.location?.coordinate.toKhrysalis()
    }

    public var oneLineShort: String {
        return oneLine()
    }

    //--- GeoAddress.oneLine(Boolean, Boolean)
    public func oneLine(_ withCountry: Bool, _ withZip: Bool = false) -> String {
        return oneLine(withCountry: withCountry, withZip: withZip)
    }
    public func oneLine(withCountry: Bool = false, withZip: Bool = false) -> String {
        var result = self.street ?? ""
        if let x: String = self.mark?.locality, let y: String = self.mark?.administrativeArea {
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
