//
//  ViewDependency+location.swift
//  Alamofire
//
//  Created by Joseph Ivie on 10/23/19.
//

import UIKit
import CoreLocation


public extension ViewDependency {
    func requestLocation(_ accuracyBetterThanMeters: Double, _ onResult: @escaping (LocationResult)->Void) {
        requestLocation(accuracyBetterThanMeters: accuracyBetterThanMeters, onResult: onResult)
    }
    func requestLocation(accuracyBetterThanMeters: Double, onResult: @escaping (LocationResult)->Void) {
        let manager = CLLocationManager()
        manager.requestWhenInUseAuthorization()
        let retainId = "locationManager" + String(arc4random() % 10000)
        self.parentViewController.retain(as: retainId, item: manager)
        let sd = ScreamingDelegate(callback: { [weak manager] result in
            manager?.stopUpdatingLocation()
            onResult(result)
            self.parentViewController.unretain(retainId)
        })
        manager.delegate = sd
        manager.retain(as: "delegate", item: sd)
    }
    
    private class ScreamingDelegate: NSObject, CLLocationManagerDelegate {
        let callback: (LocationResult)->Void
        
        init(callback: @escaping (LocationResult)->Void) {
            self.callback = callback
        }
        
        func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
            //GOTTEM
            if let didUpdateToLocation = locations.last {
                callback(LocationResult(
                    latitude: didUpdateToLocation.coordinate.latitude,
                    longitude: didUpdateToLocation.coordinate.longitude,
                    accuracyMeters: didUpdateToLocation.horizontalAccuracy,
                    altitudeMeters: didUpdateToLocation.altitude,
                    altitudeAccuracyMeters: didUpdateToLocation.verticalAccuracy,
                    headingFromNorth: didUpdateToLocation.course + 90,
                    speedMetersPerSecond: didUpdateToLocation.speed
                ))
            }
        }
        
        func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
            if status == CLAuthorizationStatus.authorizedWhenInUse || status == CLAuthorizationStatus.authorizedAlways {
                if CLLocationManager.locationServicesEnabled() {
                    manager.desiredAccuracy = kCLLocationAccuracyNearestTenMeters
                    manager.startUpdatingLocation()
                }
            }
        }
    }
}
