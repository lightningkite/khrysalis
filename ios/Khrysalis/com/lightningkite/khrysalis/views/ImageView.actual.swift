//
//  UIImageView+ext.swift
//
//  Created by Joseph Ivie on 10/9/19.
//

import Foundation
import UIKit


public extension UIImageView {
    func setImageResource(_ drawableMaker: (UIView?)->CALayer) {
        self.image = drawableMaker(self).toImage()
        self.superview?.setNeedsLayout()
    }
    func setImageBitmap(_ bitmap: UIImage) {
        self.image = bitmap
        self.superview?.setNeedsLayout()
    }
}
