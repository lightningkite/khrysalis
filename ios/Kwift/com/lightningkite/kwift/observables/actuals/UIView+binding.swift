//
//  UIView+binding.swift
//  KwiftTemplate
//
//  Created by Joseph Ivie on 8/21/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit
import FlexLayout

public extension UIView {
    func bindVisible(_ observable: ObservableProperty<Bool>) {
        return bindVisible(observable: observable)
    }
    func bindVisible(observable: ObservableProperty<Bool>) {
        observable.addAndRunWeak(referenceA: self) { (this, value) in
            this.isHidden = !value
        }
    }

    func bindExists(_ observable: ObservableProperty<Bool>) {
        return bindExists(observable: observable)
    }
    func bindExists(observable: ObservableProperty<Bool>) {
        observable.addAndRunWeak(referenceA: self) { (this, value) in
            this.includeInLayout = value
        }
    }
}
