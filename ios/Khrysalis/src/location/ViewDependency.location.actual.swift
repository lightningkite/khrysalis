//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation
import CoreLocation

//--- LocationCache.{
class LocationCache{

    var location: LocationResult
    var timeSinceCall: Date
    var accuracy: Double
    
    //--- LocationCache.Primary Constructor
    init(location: LocationResult, time: Date, accuracy: Double){
        self.location = location
        self.timeSinceCall = time
        self.accuracy = accuracy
    }
    
    init(_ location: LocationResult, _ time: Date, _ accuracy: Double){
        self.location = location
        self.timeSinceCall = time
        self.accuracy = accuracy
    }
    //--- LocationCache.}
}

//--- lastLocation
var lastLocation: LocationCache? = nil

public extension ViewDependency {
    //--- ViewDependency.requestLocation(Double, Double, (LocationResult?,String?)->Unit)
    func requestLocation(_ accuracyBetterThanMeters: Double = 10.0, _ timeoutInSeconds: Double = 100.0, _ onResult: @escaping (LocationResult?, String?) -> Void) -> Void {
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
        manager.retain(as: "delegate", item: sd, until: DisposeCondition{ _ in })
        
        DispatchQueue.main.asyncAfter(deadline: .now() + timeoutInSeconds) {
            manager.stopUpdatingLocation()
            singleTime.once {
                onResult(nil, "Timeout")
            }
        }
    }
    func requestLocation(accuracyBetterThanMeters: Double = 10.0, timeoutInSeconds: Double = 100.0, onResult: @escaping (LocationResult?, String?) -> Void) -> Void {
        return requestLocation(accuracyBetterThanMeters, timeoutInSeconds, onResult)
    }
    
    //--- ViewDependency.requestLocationCached(Double, Double, (LocationResult?,String?)->Unit)
    func requestLocationCached(_ accuracyBetterThanMeters: Double = 10.0, _ timeoutInSeconds: Double = 100.0, _ onResult: @escaping (LocationResult?, String?)->Void) {
        requestLocationCached(accuracyBetterThanMeters: accuracyBetterThanMeters, timeoutInSeconds: timeoutInSeconds, onResult: onResult)
    }
    
    func requestLocationCached(accuracyBetterThanMeters: Double = 10.0, timeoutInSeconds: Double = 100.0, onResult: @escaping (LocationResult?, String?)->Void) {
        if let location = lastLocation{
            if location.timeSinceCall.timeIntervalSinceNow.milliseconds > 300000 && location.accuracy < accuracyBetterThanMeters{
                onResult(location.location, nil)
            }else{
                requestLocation(accuracyBetterThanMeters, timeoutInSeconds){(result, string) in
                    if let result = result{
                        lastLocation = LocationCache(result, Date(), accuracyBetterThanMeters)
                    }
                    onResult(result, string)
                }
            }
        }else{
            requestLocation(accuracyBetterThanMeters, timeoutInSeconds){(result, string) in
                if let result = result{
                    lastLocation = LocationCache(result, Date(), accuracyBetterThanMeters)
                }
                onResult(result, string)
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
                    coordinate: GeoCoordinate(
                        latitude: didUpdateToLocation.coordinate.latitude,
                        longitude: didUpdateToLocation.coordinate.longitude
                    ),
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
