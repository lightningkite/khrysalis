//
//  ViewFlipper.swift
//  Kwift
//
//  Created by Joseph Ivie on 10/24/19.
//  Copyright © 2019 Lightning Kite. All rights reserved.
//

import Foundation
import UIKit


open class ViewFlipper: FrameLayout {
    private var previousSubview: UIView? = nil
    public var displayedChild: Int32 = 0 {
        didSet {
            if displayedChild >= subviews.size {
                return
            }
            let displayingView = subviews[displayedChild]
            if displayingView == previousSubview { return }
            displayingView.isHidden = false
            self.setNeedsLayout()
            if let disappearingView = previousSubview {
                displayingView.alpha = 0
                UIView.animate(
                    withDuration: 0.25,
                    animations: {
                        displayingView.alpha = 1
                        disappearingView.alpha = 0
                    },
                    completion: { _ in
                        if disappearingView != self.subviews[self.displayedChild] {
                            disappearingView.isHidden = false
                            self.setNeedsLayout()
                        }
                    }
                )
            } else {
                displayingView.alpha = 1
            }
        }
    }
    
    override open func didAddSubview(_ subview: UIView) {
        if subviews.size == 1 {
            subview.isHidden = false
            previousSubview = subview
        } else {
            subview.isHidden = true
        }
    }
}
