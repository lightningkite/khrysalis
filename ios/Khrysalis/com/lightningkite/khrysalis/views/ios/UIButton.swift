//
//  UIButtonWithLayer.swift
//  Khrysalis
//
//  Created by Brady Svedin on 7/23/20.
//  Copyright © 2019 Lightning Kite. All rights reserved.
//

import Foundation
import UIKit

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

