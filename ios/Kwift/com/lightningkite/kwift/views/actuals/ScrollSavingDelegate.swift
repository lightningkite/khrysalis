//
//  ScrollSavingDelegate.swift
//  Lifting Generations
//
//  Created by Joseph Ivie on 5/13/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit


public class ScrollSavingDelegate : NSObject, UIScrollViewDelegate {
    public var lastNonzeroOffset: CGPoint = CGPoint.zero
    public func scrollViewDidScroll(_ scrollView: UIScrollView) {
        if scrollView.contentOffset != CGPoint.zero {
            print("Set offset to \(scrollView.contentOffset)")
            lastNonzeroOffset = scrollView.contentOffset
        }
    }
}
