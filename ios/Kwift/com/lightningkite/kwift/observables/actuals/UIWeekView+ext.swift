//
//  UIWeekView+ext.swift
//  KwiftTemplate
//
//  Created by Joseph Ivie on 9/10/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit
import QVRWeekView

public extension UIWeekView {
    func bind(
        _ data: ObservableProperty<(Date, Date) -> Array<WeekViewEvent>>,
        _ onEventClick: @escaping (WeekViewEvent) -> Void,
        _ onEmptyClick: @escaping (Date) -> Void
    ) {
        return bind(data: data, onEventClick: onEventClick, onEmptyClick: onEmptyClick)
    }
    func bind(
        data: ObservableProperty<(Date, Date) -> Array<WeekViewEvent>>,
        onEventClick: @escaping (WeekViewEvent) -> Void,
        onEmptyClick: @escaping (Date) -> Void
    ) {
        let delegate = BindDelegate(data: { a, b in data.value(a, b) }, onEventClick: onEventClick, onEmptyClick: onEmptyClick)
        data.addAndRunWeak(self) { this, load in
            this.didMoveToWindow()
        }
        self.delegate = delegate
        self.retain(as: "delegate", item: delegate)

        self.zoomOffsetPreservation = .reset
        self.eventStyleCallback = { (layer, data) in
//            layer.borderWidth = 2.0
//            layer.borderColor = UIColor.black.cgColor
//            layer.cornerRadius = 5.0
        }
        self.visibleDaysInPortraitMode = 3
        self.visibleDaysInLandscapeMode = 3
//        self.showPreviewOnLongPress = false
        self.showToday()
        self.notifyDataSetChanged()
    }

    class BindDelegate: WeekViewDelegate {

        let data: (Date, Date) -> Array<WeekViewEvent>
        let onEventClick: (WeekViewEvent) -> Void
        let onEmptyClick: (Date) -> Void

        init(
            data: @escaping (Date, Date) -> Array<WeekViewEvent>,
            onEventClick: @escaping (WeekViewEvent) -> Void,
            onEmptyClick: @escaping (Date) -> Void
        ){
            self.data = data
            self.onEventClick = onEventClick
            self.onEmptyClick = onEmptyClick
        }

        public func didLongPressDayView(in weekView: WeekView, atDate date: Date) {
            onEmptyClick(date)
            weekView.notifyDataSetChanged()
        }

        public func didTapEvent(in weekView: WeekView, withId eventId: String) {
            onEventClick(weekView.allVisibleEvents.find { $0.id == eventId }!)
        }

        public func eventLoadRequest(in weekView: WeekView, between startDate: Date, and endDate: Date) {
            print("--- Loading from \(startDate) to \(endDate)")
            weekView.loadEvents(withData: data(startDate, endDate))
        }
    }
}
