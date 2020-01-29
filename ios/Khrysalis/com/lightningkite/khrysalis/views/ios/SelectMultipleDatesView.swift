//
//  SelectDayView.swift
//  KhrysalisTemplate
//
//  Created by Joseph Ivie on 9/12/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit


public class SelectMultipleDatesView : AbstractCalendarView {
    var dates: MutableObservableProperty<Set<Date>> = StandardObservableProperty([]) {
        didSet {
            self.refresh()
        }
    }
    override var ignoreDragOnDay: Bool { return false }
    override public func makeChildView() -> QuickMonthView {
        let v = SelectMultipleDatesMonthView(frame: .zero)
        v.dates = dates
        return v
    }
}
