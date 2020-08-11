//
//  MKMapView+bind.swift
//  KhrysalisMaps
//
//  Created by Joseph Ivie on 10/28/19.
//  Copyright © 2019 Lightning Kite. All rights reserved.
//

import Foundation
import GoogleMaps
import Khrysalis

public extension GMSMapView {

    func bind(dependency: ViewDependency, style: String? = nil) {
        if let style = style {
            self.mapStyle = try? GMSMapStyle(jsonString: style)
        }
    }

    func bindView(
        dependency: ViewDependency,
        position: ObservableProperty<GeoCoordinate?>,
        zoomLevel: Float = 15,
        animate: Bool = true,
        style: String? = nil
    ) {
        self.bind(dependency: dependency, style: style)
        var marker: GMSMarker? = nil
        position.subscribeBy { value in
            if let value = value {
                let newMarker = marker ?? GMSMarker(position: value.toIos())
                newMarker.map = self
                newMarker.position = value.toIos()
                marker = newMarker
                if animate {
                    self.animate(to: GMSCameraPosition(target: newMarker.position, zoom: zoomLevel))
                } else {
                    self.camera = GMSCameraPosition(target: newMarker.position, zoom: zoomLevel)
                }
            } else {
                marker?.map = nil
                marker = nil
            }
        }.until(self.removed)
    }
    
    func bindSelect(
        dependency: ViewDependency,
        position: MutableObservableProperty<GeoCoordinate?>,
        zoomLevel: Float = 15,
        animate: Bool = true,
        style: String? = nil
    ) {
        self.bind(dependency: dependency, style: style)
        var marker: GMSMarker? = nil
        var suppress: Bool = false
        var suppressAnimation: Bool = false
        position.subscribeBy { value in
            guard !suppress else { return }
            suppress = true
            if let value = value {
                let newMarker = marker ?? GMSMarker(position: value.toIos())
                newMarker.map = self
                newMarker.position = value.toIos()
                marker = newMarker
                if animate && !suppressAnimation {
                    self.animate(to: GMSCameraPosition(target: newMarker.position, zoom: zoomLevel))
                } else {
                    self.camera = GMSCameraPosition(target: newMarker.position, zoom: zoomLevel)
                }
            } else {
                marker?.map = nil
                marker = nil
            }
            suppress = false
        }.until(self.removed)
        
        let dg = LambdaDelegate()
        self.retain(item: dg, until: self.removed)
        dg.didTapAtCoordinate = { map, coord in
            suppressAnimation = true
            position.value = coord.toKhrysalis()
            suppressAnimation = false
        }
        dg.didEndDraggingMarker = { map, marker in
            if (!suppress) {
                suppress = true
                position.value = marker.position.toKhrysalis()
                suppress = false
            }
        }
        self.delegate = dg
    }
    
    class LambdaDelegate: NSObject, GMSMapViewDelegate {
        
        public override init(){}

        public var willMove:  (GMSMapView, Bool) -> Void = { _, _ in }
        public func mapView(_ map: GMSMapView, willMove: Bool) -> Void {
            self.willMove(map, willMove)
        }

        public var didChangeCameraPosition:  (GMSMapView, GMSCameraPosition) -> Void = { _, _ in }
        public func mapView(_ map: GMSMapView, didChange didChangeCameraPosition: GMSCameraPosition) -> Void {
            self.didChangeCameraPosition(map, didChangeCameraPosition)
        }

        public var idleAtCameraPosition:  (GMSMapView, GMSCameraPosition) -> Void = { _, _ in }
        public func mapView(_ map: GMSMapView, idleAt idleAtCameraPosition: GMSCameraPosition) -> Void {
            self.idleAtCameraPosition(map, idleAtCameraPosition)
        }

        public var didTapAtCoordinate:  (GMSMapView, CLLocationCoordinate2D) -> Void = { _, _ in }
        public func mapView(_ map: GMSMapView, didTapAt didTapAtCoordinate: CLLocationCoordinate2D) -> Void {
            self.didTapAtCoordinate(map, didTapAtCoordinate)
        }

        public var didLongPressAtCoordinate:  (GMSMapView, CLLocationCoordinate2D) -> Void = { _, _ in }
        public func mapView(_ map: GMSMapView, didLongPressAt didLongPressAtCoordinate: CLLocationCoordinate2D) -> Void {
            self.didLongPressAtCoordinate(map, didLongPressAtCoordinate)
        }

        public var didTapMarker:  (GMSMapView, GMSMarker) -> Bool = { _, _ in true }
        public func mapView(_ map: GMSMapView, didTap didTapMarker: GMSMarker) -> Bool {
            return self.didTapMarker(map, didTapMarker)
        }

        public var didTapInfoWindowOfMarker: (GMSMapView, GMSMarker) -> Void = { _, _ in }
        public func mapView(_ map: GMSMapView, didTapInfoWindowOf didTapInfoWindowOfMarker:GMSMarker) -> Void {
            self.didTapInfoWindowOfMarker(map, didTapInfoWindowOfMarker)
        }

        public var didLongPressInfoWindowOfMarker: (GMSMapView, GMSMarker) -> Void = { _, _ in }
        public func mapView(_ map: GMSMapView, didLongPressInfoWindowOf didLongPressInfoWindowOfMarker:GMSMarker) -> Void {
            self.didLongPressInfoWindowOfMarker(map, didLongPressInfoWindowOfMarker)
        }

        public var didTapOverlay:  (GMSMapView, GMSOverlay) -> Void = { _, _ in }
        public func mapView(_ map: GMSMapView, didTap didTapOverlay: GMSOverlay) -> Void {
            self.didTapOverlay(map, didTapOverlay)
        }

        public var markerInfoWindow:  (GMSMapView, GMSMarker) -> UIView? = { _, _ in nil }
        public func mapView(_ map: GMSMapView, markerInfoWindow: GMSMarker) -> UIView? {
            self.markerInfoWindow(map, markerInfoWindow)
        }

        public var markerInfoContents:  (GMSMapView, GMSMarker) -> UIView? = { _, _ in nil }
        public func mapView(_ map: GMSMapView, markerInfoContents: GMSMarker) -> UIView? {
            self.markerInfoContents(map, markerInfoContents)
        }

        public var didCloseInfoWindowOfMarker:  (GMSMapView, GMSMarker) -> Void = { _, _ in }
        public func mapView(_ map: GMSMapView, didCloseInfoWindowOf didCloseInfoWindowOfMarker: GMSMarker) -> Void {
            self.didCloseInfoWindowOfMarker(map, didCloseInfoWindowOfMarker)
        }

        public var didBeginDraggingMarker:  (GMSMapView, GMSMarker) -> Void = { _, _ in }
        public func mapView(_ map: GMSMapView, didBeginDragging didBeginDraggingMarker: GMSMarker) -> Void {
            self.didBeginDraggingMarker(map, didBeginDraggingMarker)
        }

        public var didEndDraggingMarker:  (GMSMapView, GMSMarker) -> Void = { _, _ in }
        public func mapView(_ map: GMSMapView, didEndDragging didEndDraggingMarker: GMSMarker) -> Void {
            self.didEndDraggingMarker(map, didEndDraggingMarker)
        }

        public var didDragMarker:  (GMSMapView, GMSMarker) -> Void = { _, _ in }
        public func mapView(_ map: GMSMapView, didDrag didDragMarker: GMSMarker) -> Void {
            self.didDragMarker(map, didDragMarker)
        }

        public var didTapMyLocation:  (GMSMapView, CLLocationCoordinate2D) -> Void = { _, _ in }
        public func mapView(_ map :GMSMapView, didTapMyLocation: CLLocationCoordinate2D) -> Void {
            self.didTapMyLocation(map, didTapMyLocation)
        }
    }
}
