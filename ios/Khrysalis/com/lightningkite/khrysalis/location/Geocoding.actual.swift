//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation
import CoreLocation

public extension ViewDependency {

    //--- ViewDependency.geocode(GeoCoordinate, (List<GeoAddress>)->Unit)
    func geocode(_ latLng: GeoCoordinate, _ onResult: @escaping (Array<GeoAddress>)->Void) {
        geocode(latLng: latLng, onResult: onResult)
    }
    func geocode(latLng: GeoCoordinate, onResult: @escaping (Array<GeoAddress>)->Void) {
        CLGeocoder().reverseGeocodeLocation(CLLocation(latitude: latLng.latitude, longitude: latLng.longitude)){ marks, error in
            onResult(marks?.map { translate(mark: $0) } ?? [])
        }
    }

    //--- ViewDependency.geocode(String, (List<GeoAddress>)->Unit)
    func geocode(_ address: String, _ onResult: @escaping (Array<GeoAddress>)->Void) {
        geocode(address: address, onResult: onResult)
    }
    func geocode(address: String, onResult: @escaping (Array<GeoAddress>)->Void) {
        CLGeocoder().geocodeAddressString(address){ marks, error in
            onResult(marks?.map { translate(mark: $0) } ?? [])
        }
    }
}

//--- translate
private func translate(mark: CLPlacemark) -> GeoAddress {
    return GeoAddress(mark.location?.coordinate.toKhrysalis(), mark.name, mark.street, mark.subLocality, mark.locality, mark.subAdministrativeArea, mark.administrativeArea, mark.country, mark.postalCode)
}

private extension CLPlacemark {
    
    var street: String? {
        if let x = subThoroughfare, let y = thoroughfare {
            return "\(x) \(y)"
        }
        return nil
    }
}
