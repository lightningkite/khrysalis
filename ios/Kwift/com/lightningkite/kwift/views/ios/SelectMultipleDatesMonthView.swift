//
//  SelectDayMonthView.swift
//  KwiftTemplate
//
//  Created by Joseph Ivie on 9/11/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit
import RxSwift


public class SelectMultipleDatesMonthView : QuickMonthView {
    var dates: MutableObservableProperty<Set<Date>> = StandardObservableProperty([]) {
        didSet {
            startup2()
            setNeedsDisplay()
        }
    }
    public override init(frame: CGRect) {
        super.init(frame: frame)
        startup2()
    }

    required public init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        startup2()
    }

    var closers: Array<Disposable> = []
    public func startup2() {
        for close in closers {
            close.dispose()
        }
        closers.append(dates.onChange.addWeak(self) { this, value in
            this.setNeedsDisplay()
        })
    }

    override public func drawDayCell(_ ctx: CGContext, _ rect: CGRect, _ day: Date) {
        if dates.value.any({ it in day.sameDay(it) }) {
            let leftCal = Calendar.current.date(byAdding: .day, value: -1, to: day)!
            let left = dates.value.any { it in leftCal.sameDay(it) }
            let rightCal = Calendar.current.date(byAdding: .day, value: 1, to: day)!
            let right = dates.value.any { it in rightCal.sameDay(it) }
            if !left && !right {
                drawDayCellBackground(ctx: ctx, rectangle: rect, day: day, colorSet: self.selectedColorSet)
                drawDayCellText(ctx: ctx, rectangle: rect, day: day, colorSet: self.selectedColorSet)
            } else if !left && right {
                drawDayCellBackgroundStart(ctx: ctx, rectangle: rect, day: day, colorSet: self.selectedColorSet)
                drawDayCellText(ctx: ctx, rectangle: rect, day: day, colorSet: self.selectedColorSet)
            } else if left && !right {
                drawDayCellBackgroundEnd(ctx: ctx, rectangle: rect, day: day, colorSet: self.selectedColorSet)
                drawDayCellText(ctx: ctx, rectangle: rect, day: day, colorSet: self.selectedColorSet)
            } else if left && right {
                drawDayCellBackgroundMiddle(ctx: ctx, rectangle: rect, day: day, colorSet: self.selectedColorSet)
                drawDayCellText(ctx: ctx, rectangle: rect, day: day, colorSet: self.selectedColorSet)
            } else {
                drawDayCellBackground(ctx: ctx, rectangle: rect, day: day, colorSet: self.selectedColorSet)
                drawDayCellText(ctx: ctx, rectangle: rect, day: day, colorSet: self.selectedColorSet)
            }
        } else {
            drawDayCellBackground(ctx: ctx, rectangle: rect, day: day, colorSet: self.defaultColorSet)
            drawDayCellText(ctx: ctx, rectangle: rect, day: day, colorSet: self.defaultColorSet)
        }
    }

    var adding = false
    override public func onTouchDown(date: Date) -> Bool {
        adding = dates.value.none { it in date.sameDay(it) }
        return onTouchMove(date: date)
    }
    override public func onTouchMove(date: Date) -> Bool {
        if adding {
            if dates.value.none({ it in date.sameDay(it) }) {
                dates.value = dates.value.union([date])
            }
        } else {
            dates.value = dates.value.filter { it in !it.sameDay(date) }
        }
        return true
    }
    
    override public func onTouchUp(date: Date) -> Bool {
        return true
    }
}
