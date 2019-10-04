//
//  SelectDateRangeView+binding.swift
//  KwiftTemplate
//
//  Created by Joseph Ivie on 9/13/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit


public extension SelectDateRangeView {
    func bind(_ start: MutableObservableProperty<Date?>, _ endInclusive: MutableObservableProperty<Date?>) {
        return bind(start: start, endInclusive: endInclusive)
    }
    func bind(start: MutableObservableProperty<Date?>, endInclusive: MutableObservableProperty<Date?>) {
        self.start = start
        self.endInclusive = endInclusive
    }
}
