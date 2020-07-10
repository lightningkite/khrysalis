//
//  ScrollView.actual.swift
//  Alamofire
//
//  Created by Joseph Ivie on 1/4/20.
//

import UIKit

public extension UIScrollView {
    func scrollTo(_ x: Int32, _ y: Int32) {
        self.setContentOffset(CGPoint(x: CGFloat(x), y: CGFloat(y)), animated: false)
    }
    func smoothScrollTo(_ x: Int32, _ y: Int32) {
        self.setContentOffset(CGPoint(x: CGFloat(x), y: CGFloat(y)), animated: true)
    }
    var scrollX: Int32 {
        return Int32(self.contentOffset.x)
    }
    var scrollY: Int32 {
        return Int32(self.contentOffset.y)
    }
    func scrollToBottom(){
        let bottomOffset = CGPoint(x: 0, y: self.contentSize.height - self.bounds.size.height)
        self.setContentOffset(bottomOffset, animated: true)
    }
}

