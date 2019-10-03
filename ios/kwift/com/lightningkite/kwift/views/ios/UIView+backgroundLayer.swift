//
//  UIView+backgroundLayer.swift
//  Kwift Template
//
//  Created by Joseph Ivie on 9/26/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit

extension UIView {
    private static let extensionBackgroundLayer = ExtensionProperty<UIView, CALayer>()
    var backgroundLayer: CALayer? {
        set(value){
            let previous = UIView.extensionBackgroundLayer.get(self)
            previous?.removeFromSuperlayer()
            if let value = value {
                self.layer.insertSublayer(value, at: 0)
            }
            UIView.extensionBackgroundLayer.set(self, value)
        }
        get {
            return UIView.extensionBackgroundLayer.get(self)
        }
    }
}
