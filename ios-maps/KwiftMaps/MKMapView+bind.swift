//
//  MKMapView+bind.swift
//  KwiftMaps
//
//  Created by Joseph Ivie on 10/28/19.
//  Copyright © 2019 Lightning Kite. All rights reserved.
//

import Foundation
import MapKit
import Kwift


public extension MKMapView {

    func bindView(
        dependency: ViewDependency,
        position: ObservableProperty<GeoCoordinate?>,
        zoomLevel: Float = 15,
        animate: Bool = true
    ) {
        bindView(dependency, position, zoomLevel, animate)
    }
    func bindView(
        _ dependency: ViewDependency,
        _ position: ObservableProperty<GeoCoordinate?>,
        _ zoomLevel: Float = 15,
        _ animate: Bool = true
    ) {
        var annotation: MKPointAnnotation? = nil
        position.addAndRunWeak(self) { (self, value) in
            if let value = value {
                let point = annotation ?? {
                    let new = MKPointAnnotation()
                    new.coordinate = value.toIos()
                    self.addAnnotation(new)
                    return new
                }()
                let view = self.view(for: point)
                view?.isDraggable = true
                point.coordinate = value.toIos()
                annotation = point
                self.setCenter(value.toIos(), animated: true)
                
            } else {
                if let point = annotation {
                    self.removeAnnotation(point)
                }
                annotation = nil
            }
        }
        self.retain(as: "delegate", item: delegate)
    }
    
    func bindSelect(
        dependency: ViewDependency,
        position: MutableObservableProperty<GeoCoordinate?>,
        zoomLevel: Float = 15,
        animate: Bool = true
    ) {
        bindSelect(dependency, position, zoomLevel, animate)
    }
    func bindSelect(
        _ dependency: ViewDependency,
        _ position: MutableObservableProperty<GeoCoordinate?>,
        _ zoomLevel: Float = 15,
        _ animate: Bool = true
    ) {
        let delegate = SelectDelegate(position)
        var annotation: MKPointAnnotation? = nil
        position.addAndRunWeak(self) { [unowned delegate] (self, value) in
            if let value = value {
                if !delegate.suppress {
                    delegate.suppress = true
                    let point = annotation ?? {
                        let new = MKPointAnnotation()
                        new.coordinate = value.toIos()
                        self.addAnnotation(new)
                        return new
                    }()
                    let view = self.view(for: point)
                    view?.isDraggable = true
                    point.coordinate = value.toIos()
                    annotation = point
                    delegate.suppress = false
                    if !delegate.suppressAnimation {
                        self.setCenter(value.toIos(), animated: true)
                    }
                }
            } else {
                if let point = annotation {
                    self.removeAnnotation(point)
                }
                annotation = nil
            }
        }
        self.delegate = delegate
        self.retain(as: "delegate", item: delegate)
        
        onLongClickWithGR { [weak self, unowned delegate] gr in
            guard let self = self else { return }
//            let coords = self.convert(gr.locationInView(self), toCoo(in: : self)
            let coords = self.convert(gr.location(in: self), toCoordinateFrom: self)
            delegate.suppressAnimation = true
            position.value = coords.toKwift()
            delegate.suppressAnimation = false
        }
    }
    
    private class SelectDelegate : NSObject, MKMapViewDelegate {
        
        var suppress = false
        var suppressAnimation = false
        
        let position: MutableObservableProperty<GeoCoordinate?>
        init(_ position: MutableObservableProperty<GeoCoordinate?>){
            self.position = position
        }
        
        func mapView(_ mapView: MKMapView, annotationView view: MKAnnotationView, didChange newState: MKAnnotationView.DragState, fromOldState oldState: MKAnnotationView.DragState) {
            switch newState {
            case .starting:
                view.dragState = .dragging
            case .ending, .canceling:
                if let coordinate = (view.annotation as? MKPointAnnotation)?.coordinate, !suppress {
                    suppress = true
                    position.value = coordinate.toKwift()
                    suppress = false
                }
                view.dragState = .none
            default: break
            }
        }
    }
}
