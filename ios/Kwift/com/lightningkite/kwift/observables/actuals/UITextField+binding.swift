//
//  UITextField+binding.swift
//  KwiftTemplate
//
//  Created by Joseph Ivie on 8/21/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit


public extension UITextField {
    func bindString(_ observable: ObservableProperty<String>) {
        return bindString(observable: observable)
    }
    func bindString(observable: ObservableProperty<String>) {
        observable.addAndRunWeak(referenceA: self) { (this, value) in
            if this.text != value {
                this.text = value
            }
            this.setNeedsLayout()
        }
    }

    func bindStringRes(_ observableReference: ObservableProperty<StringReference?>) {
        return bindStringRes(observableReference: observableReference)
    }
    func bindStringRes(observableReference: ObservableProperty<StringReference?>) {
        observableReference.addAndRunWeak(referenceA: self) { (this, value) in
            if let value = value {
                let localValue = NSLocalizedString(value, comment: "")
                if this.text != localValue {
                    this.text = localValue
                }
            } else {
                this.text = nil
            }
            this.setNeedsLayout()
        }
    }

    func bindString(_ observable: MutableObservableProperty<String>) { bindString(observable: observable) }
    func bindString(observable: MutableObservableProperty<String>) {
        delegate = DoneDelegate.shared
        observable.addAndRunWeak(referenceA: self) { (this, value) in
            if this.text != value {
                this.text = value
            }
            this.setNeedsLayout()
        }
        weak var observableWeak = observable
        addAction(for: UITextField.Event.editingChanged) { [weak self] in
            if observableWeak?.value != self?.text {
                observableWeak?.value = self?.text ?? ""
            }
        }
    }

    func bindDouble(_ observable: MutableObservableProperty<Double>) { bindDouble(observable: observable) }
    func bindDouble(observable: MutableObservableProperty<Double>) {
        delegate = DoneDelegate.shared
        if observable.value != 0.0 {
            text = observable.value.toString()
        }
        observable.addAndRunWeak(referenceA: self) { (this, value) in
            let currentValue = Double(this.text ?? "")
            if currentValue != nil, currentValue != value {
                this.text = value.toString()
                this.setNeedsLayout()
            }
        }
        weak var observableWeak = observable
        addAction(for: UITextField.Event.editingChanged) { [weak self] in
            if let self = self, let currentValue = Double(self.text ?? ""), observableWeak?.value != currentValue {
                observableWeak?.value = currentValue
            }
        }
    }

    func bindInteger(_ observable: MutableObservableProperty<Int>) { bindInteger(observable: observable) }
    func bindInteger(observable: MutableObservableProperty<Int>) {
        delegate = DoneDelegate.shared
        if observable.value != 0 {
            text = observable.value.toString()
        }
        observable.addAndRunWeak(referenceA: self) { (this, value) in
            let currentValue = Int(this.text ?? "")
            if currentValue != nil, currentValue != value {
                this.text = value.toString()
                this.setNeedsLayout()
            }
        }
        weak var observableWeak = observable
        addAction(for: UITextField.Event.editingChanged) { [weak self] in
            if let self = self, let currentValue = Int(self.text ?? ""), observableWeak?.value != currentValue {
                observableWeak?.value = currentValue
            }
        }
    }
}

