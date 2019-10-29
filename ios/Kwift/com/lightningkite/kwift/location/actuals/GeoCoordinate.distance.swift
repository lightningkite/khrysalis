//
//  LatLng.swift
//  KwiftMaps
//
//  Created by Joseph Ivie on 10/28/19.
//  Copyright © 2019 Lightning Kite. All rights reserved.
//

import Foundation
import CoreLocation

public extension GeoCoordinate {
    
    func distanceToMiles(_ other: GeoCoordinate) -> Double {
        CLLocation(latitude: self.latitude, longitude: self.longitude).distance(from: CLLocation(latitude: other.latitude, longitude: other.longitude)) * 0.000621371
    }
}
