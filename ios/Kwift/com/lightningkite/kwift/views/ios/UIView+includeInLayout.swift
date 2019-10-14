//
//  UIView+includeInLayout.swift
//  Kwift
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
            self.superview?.setNeedsLayout()
        }
    }
}
