//
//  QuickMonthView.swift
//  KwiftTemplate
//
//  Created by Joseph Ivie on 9/11/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit
import CoreGraphics


open class QuickMonthView: UIView {
    struct ColorSet {
        var foreground: UIColor
        var background: UIColor
    }
    
    private var _firstDay: Date = Date()
    var firstDay: Date { return _firstDay }
    private var _month: Date = Date()
    var month: Date {
        set(value) {
            _month = value.dayOfMonth(1).hourOfDay(0).minuteOfHour(0).secondOfMinute(0)
            _firstDay = _month.dayOfWeek(1)
            
            setNeedsDisplay()
        }
        get {
            return _month
        }
    }
    
    var labelColorSet = ColorSet(foreground: UIColor.black, background: UIColor.white)
    var defaultColorSet = ColorSet(foreground: UIColor.black, background: UIColor.white)
    var selectedColorSet = ColorSet(foreground: UIColor.white, background: UIColor.red)
    var labelFont: CGFloat = 12
    var dayFont: CGFloat = 16
    var internalPadding: CGFloat = 8
    var dayCellMargin: CGFloat = 8
    
    public override init(frame: CGRect) {
        super.init(frame: frame)
        startup()
    }
    
    required public init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        startup()
    }
    
    func startup(){
        self.month = Date()
        self.backgroundColor = UIColor.clear
        self.isUserInteractionEnabled = true
    }
    
    private var dayLabelHeight: CGFloat = 0
    private var dayCellHeight: CGFloat = 0
    private var dayCellWidth: CGFloat = 0
    private func measure(){
        dayLabelHeight = "Tj".size(withAttributes: [.font: UIFont.get(size: labelFont, style: [])]).height + internalPadding * 2
        dayCellWidth = self.frame.size.width / 7
        dayCellHeight = (self.frame.size.height - dayLabelHeight) / 6
    }
    
    override open func layoutSubviews() {
        measure()
        super.layoutSubviews()
    }
    
    open func onTouchDown(date: Date) -> Bool {
        print("Down on \(date)")
        return false
    }
    
    open func onTouchMove(date: Date) -> Bool {
        print("Move on \(date)")
        return false
    }
    
    open func onTouchUp(date: Date) -> Bool {
        print("Up on \(date)")
        return false
    }
    
    func dayAtPixel(point: CGPoint) -> Date? {
        if (point.y < dayLabelHeight) { return nil }
        let column = Int(point.x / dayCellWidth)
        let row = Int((point.y - dayLabelHeight) / dayCellHeight)
        if (row < 0 || row > 5) { return nil }
        if (column < 0 || column > 6) { return nil }
        return dayAt(row: row, column: column)
    }
    
    func dayAt(row: Int, column: Int) -> Date {
        return firstDay.advancedBy(days: row * 7 + column)
    }

//    override func hitTest(_ point: CGPoint, with event: UIEvent?) -> UIView? {
//
//    }
    
    override open func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        guard touches.count == 1, let touch = touches.first else { return }
        if let date = dayAtPixel(point: touch.location(in: self)) {
            onTouchDown(date: date)
            super.touchesBegan(touches, with: event)
        }
    }
    
    override open func touchesMoved(_ touches: Set<UITouch>, with event: UIEvent?) {
        guard touches.count == 1, let touch = touches.first else { return }
        if let date = dayAtPixel(point: touch.location(in: self)) {
            onTouchMove(date: date)
            super.touchesMoved(touches, with: event)
        }
    }
    
    override open func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
        guard touches.count == 1, let touch = touches.first else { return }
        if let date = dayAtPixel(point: touch.location(in: self)) {
            onTouchUp(date: date)
            super.touchesEnded(touches, with: event)
        }
    }
    
    override open func draw(_ rect: CGRect) {
        guard let ctx = UIGraphicsGetCurrentContext() else { return }
        drawLabels(ctx, rect)
        for row in 0...5 {
            for col in 0...6 {
                let day = dayAt(row: row, column: col)
                drawDayCell(
                    ctx,
                    CGRect(
                        x: CGFloat(col) * dayCellWidth,
                        y: dayLabelHeight + CGFloat(row) * dayCellHeight,
                        width: dayCellWidth,
                        height: dayCellHeight
                    ),
                    day
                )
            }
        }
    }
    
    open func drawLabels(_ ctx: CGContext, _ rect: CGRect) {
//        ctx.move(to:  )
//        ctx.addLine(to:  )
//        ctx.drawPath(using: .fill)
//        "asdf".draw(in: CGRect.zero, withAttributes: [NSAttributedString.Key.font: UIFont.get(size: 12, style: [])])
        
        ctx.addRect(CGRect(x: 0, y: 0, width: self.bounds.size.width, height: dayLabelHeight))
        ctx.setFillColor(labelColorSet.background.cgColor)
        ctx.drawPath(using: .fill)
        
        for day in 0 ... 6 {
            let symbol = Calendar.current.shortWeekdaySymbols[day]
            let paragraphStyle = NSMutableParagraphStyle()
            paragraphStyle.alignment = .center
            symbol.draw(
                centeredAt: CGPoint(x: (CGFloat(day) + 0.5) * dayCellWidth, y: dayLabelHeight / 2),
                withAttributes: [
                    .font: UIFont.get(size: labelFont, style: []),
                    .foregroundColor: labelColorSet.foreground,
                    .paragraphStyle: paragraphStyle
                ]
            )
        }
    }
    
    open func drawDayCell(_ ctx: CGContext, _ rect: CGRect, _ day: Date){
        drawDayCellBackground(ctx: ctx, rectangle: rect, day: day, colorSet: self.defaultColorSet)
        drawDayCellText(ctx: ctx, rectangle: rect, day: day, colorSet: self.defaultColorSet)
    }
    
    func drawDayCellBackground(ctx: CGContext, rectangle: CGRect, day: Date, colorSet: ColorSet) {
        let size = min(rectangle.width, rectangle.height) / 2 - dayCellMargin
        ctx.addArc(center: CGPoint(x: rectangle.midX, y: rectangle.midY), radius: size, startAngle: 0, endAngle: CGFloat.pi * 2, clockwise: true)
        ctx.setFillColor(colorSet.background.cgColor)
        ctx.drawPath(using: .fill)
    }
    func drawDayCellBackgroundStart(ctx: CGContext, rectangle: CGRect, day: Date, colorSet: ColorSet) {
        drawDayCellBackground(ctx: ctx, rectangle: rectangle, day: day, colorSet: colorSet)
        ctx.addRect(CGRect(x: rectangle.midX, y: rectangle.origin.y + dayCellMargin, width: rectangle.width/2 + 1, height: rectangle.height - dayCellMargin * 2))
        ctx.setFillColor(colorSet.background.cgColor)
        ctx.drawPath(using: .fill)
    }
    func drawDayCellBackgroundMiddle(ctx: CGContext, rectangle: CGRect, day: Date, colorSet: ColorSet) {
        drawDayCellBackground(ctx: ctx, rectangle: rectangle, day: day, colorSet: colorSet)
        ctx.addRect(CGRect(x: rectangle.origin.x - 1, y: rectangle.origin.y + dayCellMargin, width: rectangle.width + 2, height: rectangle.height - dayCellMargin * 2))
        ctx.setFillColor(colorSet.background.cgColor)
        ctx.drawPath(using: .fill)
    }
    func drawDayCellBackgroundEnd(ctx: CGContext, rectangle: CGRect, day: Date, colorSet: ColorSet) {
        drawDayCellBackground(ctx: ctx, rectangle: rectangle, day: day, colorSet: colorSet)
        ctx.addRect(CGRect(x: rectangle.origin.x - 1, y: rectangle.origin.y + dayCellMargin, width: rectangle.width/2, height: rectangle.height - dayCellMargin * 2))
        ctx.setFillColor(colorSet.background.cgColor)
        ctx.drawPath(using: .fill)
    }
    
    func drawDayCellText(ctx: CGContext, rectangle: CGRect, day: Date, colorSet: ColorSet) {
        let symbol = day.dayOfMonth.toString()
        let paragraphStyle = NSMutableParagraphStyle()
        paragraphStyle.alignment = .center
        var color = colorSet.foreground
        if !day.sameMonth(month) {
            color = color.withAlphaComponent(0.25)
        }
        symbol.draw(
            centeredAt: CGPoint(x: rectangle.midX, y: rectangle.midY),
            withAttributes: [
                .font: UIFont.get(size: labelFont, style: []),
                .foregroundColor: color,
                .paragraphStyle: paragraphStyle
            ]
        )
    }
}

private extension String {
    func draw(centeredAt: CGPoint, withAttributes: [NSAttributedString.Key : Any]) {
        let sizeTaken = self.size(withAttributes: withAttributes)
        draw(at: CGPoint(x: centeredAt.x - sizeTaken.width / 2, y: centeredAt.y - sizeTaken.height / 2), withAttributes: withAttributes)
    }
}
