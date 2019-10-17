//
//  UIImage+background.swift
//  Kwift
//
//  Created by Joseph Ivie on 10/17/19.
//  Copyright © 2019 Lightning Kite. All rights reserved.
//

import Foundation
import UIKit


public extension UIView {
    var backgroundDrawable: Drawable? {
        set(value){
            if let value = value {
                backgroundLayer = value(self)
            } else {
                backgroundLayer = nil
            }
        }
        get {
            if let backgroundLayer = backgroundLayer {
                return { _ in backgroundLayer }
            } else {
                return nil
            }
        }
    }
}
