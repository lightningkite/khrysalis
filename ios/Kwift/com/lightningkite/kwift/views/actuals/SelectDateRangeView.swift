//
//  SelectDayView.swift
//  KwiftTemplate
//
//  Created by Joseph Ivie on 9/12/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit


class SelectDateRangeView : AbstractCalendarView {
    var start: MutableObservableProperty<Date?> = StandardObservableProperty(nil) {
        didSet {
            self.refresh()
        }
    }
    var endInclusive: MutableObservableProperty<Date?> = StandardObservableProperty(nil) {
        didSet {
            self.refresh()
        }
    }
    override var ignoreDragOnDay: Bool { return false }
    override func makeChildView() -> QuickMonthView {
        let v = SelectDateRangeMonthView(frame: .zero)
        v.start = start
        v.endInclusive = endInclusive
        return v
    }
}
