//
//  Geocoding.swift
//  KwiftMaps
//
//  Created by Joseph Ivie on 10/28/19.
//  Copyright © 2019 Lightning Kite. All rights reserved.
//

import Foundation
import CoreLocation
import Contacts

public extension ViewDependency {
    func geocode(_ latLng: GeoCoordinate, _ onResult: @escaping (Array<GeoAddress>)->Void) {
        geocode(latLng: latLng, onResult: onResult)
    }
    func geocode(latLng: GeoCoordinate, onResult: @escaping (Array<GeoAddress>)->Void) {
        CLGeocoder().reverseGeocodeLocation(CLLocation(latitude: latLng.latitude, longitude: latLng.longitude)){ marks, error in
            onResult(marks?.map { GeoAddress($0) } ?? [])
        }
    }
    
    func geocode(_ address: String, _ onResult: @escaping (Array<GeoAddress>)->Void) {
        geocode(address: address, onResult: onResult)
    }
    func geocode(address: String, onResult: @escaping (Array<GeoAddress>)->Void) {
        CLGeocoder().geocodeAddressString(address){ marks, error in
            onResult(marks?.map { GeoAddress($0) } ?? [])
        }
    }
}
