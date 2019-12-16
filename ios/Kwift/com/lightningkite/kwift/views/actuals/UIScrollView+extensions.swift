//
//  UIScrollView+extensions.swift
//  Alamofire
//
//  Created by Joseph Ivie on 12/16/19.
//

import UIKit

public extension UIScrollView {
    func scrollTo(_ x: Int32, _ y: Int32) {
        self.setContentOffset(CGPoint(x: CGFloat(x), y: CGFloat(y)), animated: false)
    }
    func smoothScrollTo(_ x: Int32, _ y: Int32) {
        self.setContentOffset(CGPoint(x: CGFloat(x), y: CGFloat(y)), animated: true)
    }
}
