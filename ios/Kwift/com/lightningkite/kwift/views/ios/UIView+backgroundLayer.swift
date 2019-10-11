//
//  UIView+backgroundLayer.swift
//  Kwift Template
//
//  Created by Joseph Ivie on 9/26/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit

public extension UIView {
    private static let extensionBackgroundLayer = ExtensionProperty<UIView, CALayer>()
    var backgroundLayer: CALayer? {
        set(value){
            let previous = UIView.extensionBackgroundLayer.get(self)
            previous?.removeFromSuperlayer()
            if let value = value {
                value.matchSize(self)
                self.layer.insertSublayer(value, at: 0)
            }
            UIView.extensionBackgroundLayer.set(self, value)
        }
        get {
            return UIView.extensionBackgroundLayer.get(self)
        }
    }
    private static func printLayerInfo(layer: CALayer, indent: String = "") {
        print(indent + layer.debugDescription)
        for sub in layer.sublayers ?? [] {
            printLayerInfo(layer: sub, indent: indent + " ")
        }
    }
}
