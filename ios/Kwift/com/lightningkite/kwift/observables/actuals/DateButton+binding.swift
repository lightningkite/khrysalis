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
        self.onDateEntered.addWeak(self) { (self, value) in
            if observableWeak?.value != value {
                observableWeak?.value = value
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
        self.onDateEntered.addWeak(self) { (self, value) in
            let newValue = self.date.dateAlone
            if observableWeak?.value != newValue {
                observableWeak?.value = newValue
            }
        }
    }
}

public extension TimeButton {
    
    func bindTimeAlone(_ observable: MutableObservableProperty<TimeAlone>, _ minuteInterval: Int = 1) {
        return bindTimeAlone(observable: observable, minuteInterval: minuteInterval)
    }
    func bindTimeAlone(observable: MutableObservableProperty<TimeAlone>, minuteInterval: Int = 1) {
        self.minuteInterval = minuteInterval
        observable.addAndRunWeak(referenceA: self) { (this, value) in
            if this.date.timeAlone != value {
                this.date = dateFrom(Date().dateAlone, value)
            }
        }
        weak var observableWeak = observable
        self.onDateEntered.addWeak(self) { (self, value) in
            let newValue = self.date.timeAlone
            if observableWeak?.value != newValue {
                observableWeak?.value = newValue
            }
        }
    }
    
    
    func bind(_ observable: MutableObservableProperty<Date>, _ minuteInterval: Int = 1) {
        return bind(observable: observable, minuteInterval: minuteInterval)
    }
    func bind(observable: MutableObservableProperty<Date>, minuteInterval: Int = 1) {
        self.minuteInterval = minuteInterval
        self.date = observable.value
        observable.addAndRunWeak(referenceA: self) { (this, value) in
            if this.date != value {
                this.date = value
            }
        }
        weak var observableWeak = observable
        self.onDateEntered.addWeak(self) { (self, value) in
            if observableWeak?.value != value {
                observableWeak?.value = value
            }
        }
    }
    
}
