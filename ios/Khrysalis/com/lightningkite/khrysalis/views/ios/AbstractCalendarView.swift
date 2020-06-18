//
//  SelectDayView.swift
//  KhrysalisTemplate
//
//  Created by Joseph Ivie on 9/12/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit


open class AbstractCalendarView : LinearLayout {
    public var headerColorSet = QuickMonthView.ColorSet(foreground: UIColor.black, background: UIColor.white)
    public var labelColorSet = QuickMonthView.ColorSet(foreground: UIColor.black, background: UIColor.white)
    public var defaultColorSet = QuickMonthView.ColorSet(foreground: UIColor.black, background: UIColor.white)
    public var selectedColorSet = QuickMonthView.ColorSet(foreground: UIColor.white, background: UIColor.red)
    public var headerFont: CGFloat = 16
    public var labelFont: CGFloat = 12
    public var dayFont: CGFloat = 16
    public var internalPadding: CGFloat = 8
    public var dayCellMargin: CGFloat = 8
    public var leftText: String = "‹"
    public var rightText: String = "›"

    public func styleMonth(quickMonthView: QuickMonthView) {
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

    public func makeChildView() -> QuickMonthView {
        fatalError("makeChildView not overridden!")
    }

    static private let centerIndex: Int32 = 400 * 12
    static public func monthFromPosition(_ index: Int32) -> Date {
        var components = DateComponents(calendar: Calendar.current, year: 1970, month: 1, day: 1, hour: 0, minute: 0, second: 0, nanosecond: 0)
        let date = Calendar.current.date(from: components)!
        return Calendar.current.date(byAdding: .month, value: Int(index - centerIndex), to: date, wrappingComponents: false)!
    }
    static public func positionFromMonth(_ date: Date) -> Int32 {
        return centerIndex + (date.yearAd - 1970) * 12 + date.monthOfYear - 1
    }


    public func refresh(){
        collectionView?.reloadData()
    }

    override open func didMoveToSuperview() {
        setup()
    }

    weak var collectionView: UICollectionView?
    public func setup() {
        if currentPage.value == 0 {
            currentPage.value = AbstractCalendarView.positionFromMonth(Date())
        }
        self.subviews.forEach { $0.removeFromSuperview() }
        self.backgroundColor = headerColorSet.background
        
        orientation = .y
        
        addSubview(LinearLayout()) { view in
            view.orientation = .x
            view.addSubview(UIButton()) { view in
                view.setTitleColor(headerColorSet.foreground, for: .normal)
                view.setTitle(leftText, for: .normal)
                view.titleLabel?.font = UIFont.get(size: headerFont, style: [])
                view.addAction { [weak self] in
                    self?.currentPage.value -= 1
                }
            }
            view.addSubview(UILabel()) { view in
                view.textColor = headerColorSet.foreground
                view.font = UIFont.get(size: headerFont, style: [])
                view.textAlignment = .center
                currentPage.subscribeBy{ value in
                    let month = AbstractCalendarView.monthFromPosition(value)
                    view.text = Calendar.current.monthSymbols[Int(month.monthOfYear - 1)] + " " + month.yearAd.toString()
                }.until(view.removed)
            }
            view.addSubview(UIButton()) { view in
                view.setTitleColor(headerColorSet.foreground, for: .normal)
                view.setTitle(rightText, for: .normal)
                view.titleLabel?.font = UIFont.get(size: headerFont, style: [])
                view.addAction { [weak self] in
                    self?.currentPage.value += 1
                }
            }
        }
        addSubview(UICollectionView(frame: .zero, collectionViewLayout: ViewPagerLayout()), gravity: .fill, weight: 1) { view in
            view.canCancelContentTouches = false
            view.showsHorizontalScrollIndicator = false
            view.backgroundColor = defaultColorSet.background
            view.bind(count: Int32(AbstractCalendarView.centerIndex * 2)) { [weak self] index in
                guard let self = self else {
                    return UIView(frame: .zero)
                }
                let month = AbstractCalendarView.monthFromPosition(index)
                let view = self.makeChildView()
                self.styleMonth(quickMonthView: view)
                view.month = month
                return view
            }
            view.bindIndex(self.currentPage)
        }
    }
}
