//
//  UIButtonWithLayer.swift
//  Khrysalis
//
//  Created by Joseph Ivie on 10/16/19.
//  Copyright © 2019 Lightning Kite. All rights reserved.
//

import Foundation
import UIKit

ex

public extension UIButton {
    var isActivated:Bool{
        get {
            return self.isUserInteractionEnabled
        }
        set(value) {
            self.isUserInteractionEnabled = value
        }
    }
}

