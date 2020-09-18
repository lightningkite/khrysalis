//
//  UIView+includeInLayout.swift
//  Khrysalis
//
//  Created by Joseph Ivie on 10/14/19.
//  Copyright © 2019 Lightning Kite. All rights reserved.
//

import UIKit


public extension UIView {
    static private let isIncludedExt = ExtensionProperty<UIView, Bool>()
    
    var includeInLayout: Bool {
        get {
            return UIView.isIncludedExt.get(self) ?? true
        }
        set(value) {
            UIView.isIncludedExt.set(self, value)
            self.notifyParentSizeChanged()
        }
    }
    
    func notifyParentSizeChanged() {
        if let p = self.superview {
            p.setNeedsLayout()
             var current = p
             while
                 !(current is LinearLayout) &&
                     !(current is FrameLayout) &&
                     !(current is UIScrollView)
             {
                 if let su = current.superview {
                     current = su
                     if let cell = current as? SizedUICollectionViewCell {
                         cell.refreshSize()
                         break
                     }
                 } else {
                     break
                 }
             }
        }
    }
}
