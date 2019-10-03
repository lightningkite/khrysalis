//
//  SelectDayView.swift
//  KwiftTemplate
//
//  Created by Joseph Ivie on 9/12/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit


class SelectDayView : AbstractCalendarView {
    var selected: MutableObservableProperty<Date?> = StandardObservableProperty(nil) {
        didSet {
            self.refresh()
        }
    }
    override var ignoreDragOnDay: Bool { return true }
    override func makeChildView() -> QuickMonthView {
        let v = SelectDayMonthView(frame: .zero)
        v.selected = selected
        return v
    }
}
