//
//  UIButton+compoundDrawable.swift
//  Kwift
//
//  Created by Joseph Ivie on 10/17/19.
//  Copyright © 2019 Lightning Kite. All rights reserved.
//

import Foundation
import UIKit


public extension UIButtonWithLayer {
    var compoundDrawable: Drawable? {
        get {
            if let iconLayer = iconLayer {
                return { _ in iconLayer }
            } else {
                return nil
            }
        }
        set(value) {
            if let value = value {
                iconLayer = value(self)
            } else {
                iconLayer = nil
            }
        }
    }
}
