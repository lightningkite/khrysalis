//
//  SelectDateRangeView+binding.swift
//  KwiftTemplate
//
//  Created by Joseph Ivie on 9/13/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit


public extension SelectMultipleDatesView {
    func bind(_ dates: MutableObservableProperty<Set<Date>>) {
        return bind(dates: dates)
    }
    func bind(dates: MutableObservableProperty<Set<Date>>) {
        self.dates = dates
    }
}

