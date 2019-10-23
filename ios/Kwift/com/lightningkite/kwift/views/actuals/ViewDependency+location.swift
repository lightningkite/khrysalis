//
//  ViewDependency+location.swift
//  Alamofire
//
//  Created by Joseph Ivie on 10/23/19.
//

import UIKit
import CoreLocation


public extension ViewDependency {
    func requestLocation(_ accuracyBetterThanMeters: Double, _ timeoutInSeconds: Double, _ onResult: @escaping (LocationResult?, String?)->Void) {
        requestLocation(accuracyBetterThanMeters: accuracyBetterThanMeters, timeoutInSeconds: timeoutInSeconds, onResult: onResult)
    }
    func requestLocation(accuracyBetterThanMeters: Double, timeoutInSeconds: Double, onResult: @escaping (LocationResult?, String?)->Void) {
        let singleTime = SingleTime()
        
        let manager = CLLocationManager()
        manager.requestWhenInUseAuthorization()
        
        let sd = ScreamingDelegate(callback: { [weak manager] result in
            manager?.stopUpdatingLocation()
            singleTime.once {
                onResult(result, nil)
            }
        })
        manager.delegate = sd
        manager.retain(as: "delegate", item: sd)
        
        DispatchQueue.main.asyncAfter(deadline: .now() + timeoutInSeconds) {
            manager.stopUpdatingLocation()
            singleTime.once {
                onResult(nil, "Timeout")
            }
        }
    }
    
    private class SingleTime {
        public var happened: Bool = false
        public func once(action: ()->Void) {
            if happened { return }
            happened = true
            action()
        }
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
