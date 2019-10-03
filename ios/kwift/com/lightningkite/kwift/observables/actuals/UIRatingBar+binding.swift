//
//  CosmosView+binding.swift
//  KwiftTemplate
//
//  Created by Joseph Ivie on 9/5/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation

extension UIRatingBar {
    func bind(_ stars: Int32, _ observable: MutableObservableProperty<Int32>) {
        bind(stars: stars, observable: observable)
    }
    func bind(stars: Int32, observable: MutableObservableProperty<Int32>) {
        self.settings.totalStars = Int(stars)
        
        self.settings.fillMode = .full
        
        var suppress = false
        observable.addAndRunWeak(self) { (self, value) in
            guard !suppress else { return }
            suppress = true
            self.rating = Double(value)
            suppress = false
        }
        self.didTouchCosmos = { rating in
            guard !suppress else { return }
            suppress = true
            observable.value = Int32(rating)
            suppress = false
        }
    }
    
    func bind(_ stars: Int32, _ observable: ObservableProperty<Int32>) {
        bind(stars: stars, observable: observable)
    }
    func bind(stars: Int32, observable: ObservableProperty<Int32>) {
        self.settings.totalStars = Int(stars)
        self.settings.fillMode = .full
        
        observable.addAndRunWeak(self) { (self, value) in
            self.rating = Double(value)
        }
    }
    
    
    func bindFloat(_ stars: Int32, _ observable: MutableObservableProperty<Float>) {
        bindFloat(stars: stars, observable: observable)
    }
    func bindFloat(stars: Int32, observable: MutableObservableProperty<Float>) {
        self.settings.totalStars = Int(stars)
        self.settings.fillMode = .precise
        
        var suppress = false
        observable.addAndRunWeak(self) { (self, value) in
            guard !suppress else { return }
            suppress = true
            self.rating = Double(value)
            suppress = false
        }
        self.didTouchCosmos = { rating in
            guard !suppress else { return }
            suppress = true
            observable.value = Float(rating)
            suppress = false
        }
    }
    
    func bindFloat(_ stars: Int32, _ observable: ObservableProperty<Float>) {
        bindFloat(stars: stars, observable: observable)
    }
    func bindFloat(stars: Int32, observable: ObservableProperty<Float>) {
        self.settings.totalStars = Int(stars)
        self.settings.fillMode = .precise
        
        observable.addAndRunWeak(self) { (self, value) in
            self.rating = Double(value)
        }
    }
    
}
