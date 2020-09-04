//
//  PlacesAutoComplete.swift
//  KhrysalisGoogleMaps
//
//  Created by Brady Svedin on 8/7/20.
//  Copyright © 2019 Lightning Kite. All rights reserved.
//

import Foundation
import Khrysalis
import GooglePlaces
import RxSwift


public class PlacesAutocomplete{
    private var placesClient: GMSPlacesClient = GMSPlacesClient.shared()
    var token:GMSAutocompleteSessionToken? = nil
    private var working = false
    private var cachedRequest: Array<GMSAutocompletePrediction> = []
    private let detailFields = [
        GMSPlaceField.coordinate,
        GMSPlaceField.placeID,
        GMSPlaceField.name,
        GMSPlaceField.addressComponents,
        GMSPlaceField.formattedAddress
        
    ]
    
    public init (dependency:ViewDependency){
        
    }

    public func request(query: String, filter: GMSPlacesAutocompleteTypeFilter? = nil) -> Single<Array<GMSAutocompletePrediction>> {
        return Single<Array<GMSAutocompletePrediction>>.create({ emitter in
            if self.working{
                emitter.onSuccess(self.cachedRequest)
            }else{
                self.working = true
                if self.token == nil {
                    self.token = GMSAutocompleteSessionToken.init()
                }
                let temp = GMSAutocompleteFilter()
                temp.countries = ["US"]
                if let filter = filter{
                    temp.type = filter
                }
                self.placesClient.findAutocompletePredictions(
                    fromQuery: query,
                    filter: temp,
                    sessionToken: self.token,
                    callback: {(results, error) in
                    if let error = error {
                        self.working = false
                        emitter.onError(error)
                    }
                    if let results = results{
                        self.working = false
                        emitter.onSuccess(results)
                    }
                })
            }

        })
    }
    
    public func request(
        query: ObservableProperty<String>,
        disposeCondition: DisposeCondition,
        filter: GMSPlacesAutocompleteTypeFilter? = nil
    ) -> Observable<Array<GMSAutocompletePrediction>> {
        let subject = PublishSubject<Array<GMSAutocompletePrediction>>.create()
        query
            .observableNN
            .debounce(RxTimeInterval.milliseconds(750), scheduler: MainScheduler.instance)
            .subscribeBy{value in
                if !self.working {
                    self.working = true
                    if self.token == nil {
                        self.token = GMSAutocompleteSessionToken.init()
                    }
                    let temp = GMSAutocompleteFilter()
                    temp.countries = ["US"]
                    if let filter = filter{
                        temp.type = filter
                    }
                    self.placesClient.findAutocompletePredictions(
                        fromQuery: value,
                        filter: temp,
                        sessionToken: self.token,
                        callback: {(results, error) in
                        if let _ = error {
                            self.working = false
                        }
                        if let results = results{
                            self.working = false
                            subject.onNext(results)
                        }
                    })
                    
                }
            }.until(disposeCondition)
        
        return subject
    }
    
    
    public func details(id: String, details: Array<GMSPlaceField>? = nil) -> Single<GMSPlace> {
        return Single<GMSPlace>.create({emitter in
            self.working = true
            var detailValue = UInt(0)
            (details ?? self.detailFields).forEach({it in
                detailValue = detailValue | UInt(it.rawValue)
            })
            if let field = GMSPlaceField(rawValue: detailValue){
                self.placesClient.fetchPlace(fromPlaceID: id, placeFields: field, sessionToken: self.token, callback: { (place, error) in
                    self.token = nil
                    if let error = error{
                        emitter.onError(error)
                    }
                    if let place = place{
                        emitter.onSuccess(place)
                    }
                })
            }
        })
    }

}