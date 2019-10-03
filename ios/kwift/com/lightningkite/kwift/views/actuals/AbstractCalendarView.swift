//
//  SelectDayView.swift
//  KwiftTemplate
//
//  Created by Joseph Ivie on 9/12/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit
import FlexLayout


open class AbstractCalendarView : UIView {
    var headerColorSet = QuickMonthView.ColorSet(foreground: UIColor.black, background: UIColor.white)
    var labelColorSet = QuickMonthView.ColorSet(foreground: UIColor.black, background: UIColor.white)
    var defaultColorSet = QuickMonthView.ColorSet(foreground: UIColor.black, background: UIColor.white)
    var selectedColorSet = QuickMonthView.ColorSet(foreground: UIColor.white, background: UIColor.red)
    var headerFont: CGFloat = 16
    var labelFont: CGFloat = 12
    var dayFont: CGFloat = 16
    var internalPadding: CGFloat = 8
    var dayCellMargin: CGFloat = 8
    var leftText: String = "‹"
    var rightText: String = "›"
    
    func styleMonth(quickMonthView: QuickMonthView) {
        quickMonthView.labelColorSet = self.labelColorSet
        quickMonthView.defaultColorSet = self.defaultColorSet
        quickMonthView.selectedColorSet = self.selectedColorSet
        quickMonthView.labelFont = self.labelFont
        quickMonthView.dayFont = self.dayFont
        quickMonthView.internalPadding = self.internalPadding
        quickMonthView.dayCellMargin = self.dayCellMargin
    }
    
    let currentPage = StandardObservableProperty(Int32(0))
    var ignoreDragOnDay: Bool { return true }
    
    func makeChildView() -> QuickMonthView {
        fatalError("makeChildView not overridden!")
    }
    
    static private let centerIndex: Int32 = 400 * 12
    static func monthFromPosition(_ index: Int32) -> Date {
        var components = DateComponents(calendar: Calendar.current, year: 1970, month: 1, day: 1, hour: 0, minute: 0, second: 0, nanosecond: 0)
        let date = Calendar.current.date(from: components)!
        return Calendar.current.date(byAdding: .month, value: Int(index - centerIndex), to: date, wrappingComponents: false)!
    }
    static func positionFromMonth(_ date: Date) -> Int32 {
        return centerIndex + (date.yearAd - 1970) * 12 + date.monthOfYear - 1
    }
    
    
    func refresh(){
        collectionView?.reloadData()
    }
    
    override open func didMoveToSuperview() {
        setup()
    }
    
    weak var collectionView: UICollectionView?
    func setup() {
        if currentPage.value == 0 {
            currentPage.value = AbstractCalendarView.positionFromMonth(Date())
        }
        self.subviews.forEach { $0.removeFromSuperview() }
        self.backgroundColor = headerColorSet.background
        flex.direction(.column).alignItems(.stretch).define { flex in
            flex.addItem().direction(.row).alignItems(.center).define { flex in
                flex.addItem({ () -> UIButton in
                    let sub = UIButton(frame: .zero)
                    sub.setTitleColor(headerColorSet.foreground, for: .normal)
                    sub.setTitle(leftText, for: .normal)
                    sub.titleLabel?.font = UIFont.get(size: headerFont, style: [])
                    sub.addAction { [weak self] in
                        self?.currentPage.value -= 1
                    }
                    return sub
                }())
                flex.addItem({ () -> UILabel in
                    let sub = UILabel(frame: .zero)
                    sub.textColor = headerColorSet.foreground
                    sub.font = UIFont.get(size: headerFont, style: [])
                    sub.textAlignment = .center
                    currentPage.addAndRunWeak(sub) { sub, value in
                        let month = AbstractCalendarView.monthFromPosition(value)
                        sub.text = Calendar.current.monthSymbols[Int(month.monthOfYear - 1)] + " " + month.yearAd.toString()
                    }
                    return sub
                }()).grow(1)
                flex.addItem({ () -> UIButton in
                    let sub = UIButton(frame: .zero)
                    sub.setTitleColor(headerColorSet.foreground, for: .normal)
                    sub.setTitle(rightText, for: .normal)
                    sub.titleLabel?.font = UIFont.get(size: headerFont, style: [])
                    sub.addAction { [weak self] in
                        self?.currentPage.value += 1
                    }
                    return sub
                }())
            }
            
            flex.addItem({ () -> UICollectionView in
                let sub = UICollectionView(frame: .zero, collectionViewLayout: ViewPagerLayout())
                self.collectionView = sub
                sub.canCancelContentTouches = false
                sub.showsHorizontalScrollIndicator = false
                sub.backgroundColor = defaultColorSet.background
                sub.bind(count: Int32(AbstractCalendarView.centerIndex * 2)) { [weak self] index in
                    guard let self = self else {
                        return UIView(frame: .zero)
                    }
                    let month = AbstractCalendarView.monthFromPosition(index)
                    let view = self.makeChildView()
                    self.styleMonth(quickMonthView: view)
                    view.month = month
                    return view
                }
                sub.bindIndex(self.currentPage)
                return sub
            }()).grow(1)
        }
    }
}
