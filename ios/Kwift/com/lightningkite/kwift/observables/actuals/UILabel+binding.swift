//
//  UILabel+binding.swift
//  KwiftTemplate
//
//  Created by Joseph Ivie on 8/21/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit


public extension UILabel {
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

    func bindText<T>(_ observable: ObservableProperty<T>, _ transform: @escaping (T) -> String) {
        return bindText(observable: observable, transform: transform)
    }
    func bindText<T>(observable: ObservableProperty<T>, transform: @escaping (T) -> String) {
        observable.addAndRunWeak(referenceA: self) { (this, value) in
            let textValue = transform(value)
            if this.text != textValue {
                this.text = textValue
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
}
