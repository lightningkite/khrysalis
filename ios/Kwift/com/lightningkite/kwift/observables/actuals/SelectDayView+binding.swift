//
//  SelectDateRangeView+binding.swift
//  KwiftTemplate
//
//  Created by Joseph Ivie on 9/13/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit


extension SelectDayView {
    func bind(_ day: MutableObservableProperty<Date?>) {
        return bind(day: day)
    }
    func bind(day: MutableObservableProperty<Date?>) {
        self.selected = day
    }
}
