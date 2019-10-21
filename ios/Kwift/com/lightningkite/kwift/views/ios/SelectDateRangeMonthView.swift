//
//  SelectDayMonthView.swift
//  KwiftTemplate
//
//  Created by Joseph Ivie on 9/11/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit


public class SelectDateRangeMonthView : QuickMonthView {
    var start: MutableObservableProperty<Date?> = StandardObservableProperty(nil) {
        didSet {
            startup2()
            setNeedsDisplay()
        }
    }
    var endInclusive: MutableObservableProperty<Date?> = StandardObservableProperty(nil) {
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

    var closers: Array<Close> = []
    public func startup2() {
        for close in closers {
            close.close()
        }
        closers.append(start.onChange.addWeak(self) { this, value in
            this.setNeedsDisplay()
        })
        closers.append(endInclusive.onChange.addWeak(self) { this, value in
            this.setNeedsDisplay()
        })
    }

    override public func drawDayCell(_ ctx: CGContext, _ rect: CGRect, _ day: Date) {
        guard let s = start.value, let e = endInclusive.value else {
            if let s = start.value, s.sameDay(day) {
                drawDayCellBackground(ctx: ctx, rectangle: rect, day: day, colorSet: self.selectedColorSet)
                drawDayCellText(ctx: ctx, rectangle: rect, day: day, colorSet: self.selectedColorSet)
            } else {
                drawDayCellBackground(ctx: ctx, rectangle: rect, day: day, colorSet: self.defaultColorSet)
                drawDayCellText(ctx: ctx, rectangle: rect, day: day, colorSet: self.defaultColorSet)
            }
            return
        }

        if s.sameDay(e), day.sameDay(s) {
            drawDayCellBackground(ctx: ctx, rectangle: rect, day: day, colorSet: self.selectedColorSet)
            drawDayCellText(ctx: ctx, rectangle: rect, day: day, colorSet: self.selectedColorSet)
        } else if s.sameDay(day) {
            drawDayCellBackgroundStart(ctx: ctx, rectangle: rect, day: day, colorSet: self.selectedColorSet)
            drawDayCellText(ctx: ctx, rectangle: rect, day: day, colorSet: self.selectedColorSet)
        } else if e.sameDay(day) {
            drawDayCellBackgroundEnd(ctx: ctx, rectangle: rect, day: day, colorSet: self.selectedColorSet)
            drawDayCellText(ctx: ctx, rectangle: rect, day: day, colorSet: self.selectedColorSet)
        } else if day.after(s), day.before(e) {
            drawDayCellBackgroundMiddle(ctx: ctx, rectangle: rect, day: day, colorSet: self.selectedColorSet)
            drawDayCellText(ctx: ctx, rectangle: rect, day: day, colorSet: self.selectedColorSet)
        } else {
            drawDayCellBackground(ctx: ctx, rectangle: rect, day: day, colorSet: self.defaultColorSet)
            drawDayCellText(ctx: ctx, rectangle: rect, day: day, colorSet: self.defaultColorSet)
        }
    }

    private var startedDraggingOn: Date = Date()
    private var everMoved = false
    var draggingStart = true
    
    override public func onTouchDown(date: Date) -> Bool {
        startedDraggingOn = date
        everMoved = false
        
        guard let startValue = start.value, let endInclusiveValue = endInclusive.value else {
            start.value = date
            endInclusive.value = date
            return true
        }
        if date.sameDay(startValue) {
            draggingStart = true
        } else if date.sameDay(endInclusiveValue){
            draggingStart = false
        } else if date.after(endInclusiveValue), startValue.sameDay(endInclusiveValue) {
            endInclusive.value = date
            draggingStart = false
        } else {
            start.value = date
            endInclusive.value = date
            draggingStart = false
        }
        return true
    }
    override public func onTouchMove(date: Date) -> Bool {
        if !date.sameDay(startedDraggingOn) {
            everMoved = true
        }
        if let startValue = start.value, let endInclusiveValue = endInclusive.value {
            if draggingStart, date.after(endInclusiveValue) {
                start.value = endInclusiveValue
                endInclusive.value = date
                draggingStart = false
            } else if !draggingStart, date.before(startValue) {
                endInclusive.value = startValue
                start.value = date
                draggingStart = true
            }
        }

        if draggingStart {
            start.value = date
        } else {
            endInclusive.value = date
        }
        return true
    }
    
    override public func onTouchUp(date: Date) -> Bool {
        let result = onTouchMove(date: date)
        if startedDraggingOn.sameDay(date) && !everMoved {
            start.value = date
            endInclusive.value = date
        }
        return result
    }
}
