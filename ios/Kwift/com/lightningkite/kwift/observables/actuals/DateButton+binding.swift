//
//  Binding.swift
//  PennyProfit
//
//  Created by Joseph Ivie on 1/2/19.
//  Copyright © 2019 Shane Thompson. All rights reserved.
//

import Foundation
import UIKit

public extension DateButton {
    func bind(_ observable: MutableObservableProperty<Date>) {
        return bind(observable: observable)
    }
    func bind(observable: MutableObservableProperty<Date>) {
        self.date = observable.value
        observable.addAndRunWeak(referenceA: self) { (this, value) in
            if this.date != value {
                this.date = value
            }
        }
        weak var observableWeak = observable
        picker.addAction(for: .valueChanged) { [weak self] in
            if observableWeak?.value != self?.date {
                observableWeak?.value = self?.date ?? Date()
            }
        }
    }
    
    func bindDateAlone(_ observable: MutableObservableProperty<DateAlone>) {
        return bindDateAlone(observable: observable)
    }
    func bindDateAlone(observable: MutableObservableProperty<DateAlone>) {
        observable.addAndRunWeak(referenceA: self) { (this, value) in
            if this.date.dateAlone != value {
                this.date = dateFrom(value, Date().timeAlone)
            }
        }
        weak var observableWeak = observable
        picker.addAction(for: .valueChanged) { [weak self] in
            if let newValue = self?.date.dateAlone, observableWeak?.value != newValue {
                observableWeak?.value = newValue
            }
        }
    }
    
    func bindTimeAlone(_ observable: MutableObservableProperty<TimeAlone>) {
        return bindTimeAlone(observable: observable)
    }
    func bindTimeAlone(observable: MutableObservableProperty<TimeAlone>) {
        observable.addAndRunWeak(referenceA: self) { (this, value) in
            if this.date.timeAlone != value {
                this.date = dateFrom(Date().dateAlone, value)
            }
        }
        weak var observableWeak = observable
        picker.addAction(for: .valueChanged) { [weak self] in
            if let newValue = self?.date.timeAlone, observableWeak?.value != newValue {
                observableWeak?.value = newValue
            }   
        }
    }
}

