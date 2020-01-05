//
//  GeoCoordinate.ios.swift
//  Kwift
//
//  Created by Joseph Ivie on 10/28/19.
//  Copyright © 2019 Lightning Kite. All rights reserved.
//

import Foundation
import CoreLocation

public extension GeoCoordinate {
    func toIos() -> CLLocationCoordinate2D {
        return CLLocationCoordinate2D(latitude: latitude, longitude: longitude)
    }
}

public extension CLLocationCoordinate2D {
    func toKwift() -> GeoCoordinate {
        return GeoCoordinate(latitude: latitude, longitude: longitude)
    }
}
