//
//  UIImageView.ext.swift
//  Lifting Generations
//
//  Created by Joseph Ivie on 4/30/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import UIKit


extension UIImage {
    public func withInsets(insetDimen: CGFloat) -> UIImage {
        return withInset(insets: UIEdgeInsets(top: insetDimen, left: insetDimen, bottom: insetDimen, right: insetDimen))
    }

    public func withInset(insets: UIEdgeInsets) -> UIImage {
        UIGraphicsBeginImageContextWithOptions(
            CGSize(width: self.size.width + insets.left + insets.right,
                   height: self.size.height + insets.top + insets.bottom), false, self.scale)
        let origin = CGPoint(x: insets.left, y: insets.top)
        self.draw(at: origin)
        let imageWithInsets = UIGraphicsGetImageFromCurrentImageContext()?.withRenderingMode(self.renderingMode)
        UIGraphicsEndImageContext()
        return imageWithInsets!
    }

}
