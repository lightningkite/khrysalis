//
//  Binding.swift
//  PennyProfit
//
//  Created by Joseph Ivie on 1/2/19.
//  Copyright © 2019 Shane Thompson. All rights reserved.
//

import Foundation
import UIKit

extension DateButton {
    func bind(_ observable: MutableObservableProperty<Date>) {
        return bind(observable: observable)
    }
    func bind(observable: MutableObservableProperty<Date>) {
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
}

