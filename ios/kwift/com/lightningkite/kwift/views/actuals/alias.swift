//
//  UIRatingBar.swift
//  KwiftTemplate
//
//  Created by Joseph Ivie on 9/5/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import Cosmos
import QVRWeekView

typealias UIRatingBar = CosmosView
typealias UIWeekView = WeekView
typealias WeekViewEvent = EventData

extension WeekViewEvent {
    convenience init(id: Int64, title: String, start: Date, end: Date, colorRes: UIColor) {
        self.init(id: Int(id), title: title, startDate: start, endDate: end, color: colorRes)
    }
    convenience init(_ id: Int64, _ title: String, _ start: Date, _ end: Date, _ colorRes: UIColor) {
        self.init(id: Int(id), title: title, startDate: start, endDate: end, color: colorRes)
    }
}
