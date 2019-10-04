//
//  SelectDayMonthView.swift
//  KwiftTemplate
//
//  Created by Joseph Ivie on 9/11/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit


public class SelectDayMonthView : QuickMonthView {
    var selected: MutableObservableProperty<Date?> = StandardObservableProperty(nil) {
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
        closers.append(selected.onChange.addWeak(self) { this, value in
            this.setNeedsDisplay()
        })
    }

    override public func drawDayCell(_ ctx: CGContext, _ rect: CGRect, _ day: Date) {
        if let s = selected.value, day.sameDay(s) {
            drawDayCellBackground(ctx: ctx, rectangle: rect, day: day, colorSet: self.selectedColorSet)
            drawDayCellText(ctx: ctx, rectangle: rect, day: day, colorSet: self.selectedColorSet)
        } else {
            drawDayCellBackground(ctx: ctx, rectangle: rect, day: day, colorSet: self.defaultColorSet)
            drawDayCellText(ctx: ctx, rectangle: rect, day: day, colorSet: self.defaultColorSet)
        }
    }

    var downOn: Date? = nil
    override public func onTouchDown(date: Date) -> Bool {
        downOn = date
        return true
    }
    override public func onTouchMove(date: Date) -> Bool {
        return downOn == date
    }
    override public func onTouchUp(date: Date) -> Bool {
        if downOn == date {
            selected.value = date
        }
        return true
    }
}
