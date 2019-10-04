//
//  UISlider+binding.swift
//  KwiftTemplate
//
//  Created by Joseph Ivie on 9/4/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit


public extension UISlider {
    func bind(_ start: Int32, _ endInclusive: Int32, _ observable: MutableObservableProperty<Int32>) {
        bind(start: start, endInclusive: endInclusive, observable: observable)
    }
    func bind(start: Int32, endInclusive: Int32, observable: MutableObservableProperty<Int32>) {
        var suppress = false
        self.minimumValue = Float(start)
        self.maximumValue = Float(endInclusive)
        self.addAction(for: .valueChanged, action: { [weak self] in
            guard let self = self, !suppress else { return }
            suppress = true
            observable.value = Int32(self.value.rounded())
            suppress = false
        })
        observable.addAndRunWeak(self) { (self, value) in
            guard !suppress else { return }
            suppress = true
            self.value = Float(self.value)
            suppress = false
        }
    }
}
