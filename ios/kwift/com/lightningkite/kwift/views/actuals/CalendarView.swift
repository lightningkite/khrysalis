//
//  SelectDayView.swift
//  KwiftTemplate
//
//  Created by Joseph Ivie on 9/12/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit


class CalendarView : AbstractCalendarView {
    override var ignoreDragOnDay: Bool { return false }
    override func makeChildView() -> QuickMonthView {
        return QuickMonthView(frame: .zero)
    }
}
