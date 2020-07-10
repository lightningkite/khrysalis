//
//  PreviewVariedFlipper.swift
//  Klyp
//
//  Created by Joseph Ivie on 9/26/19.
//  Copyright © 2019 Klyp. All rights reserved.
//

import Foundation
import UIKit

public class PreviewVariedFlipper: FrameLayout {

    var current = Int(arc4random())
    
    override public func didAddSubview(_ subview: UIView) {
        super.didAddSubview(subview)
        setup()
    }
    
    func setup(){
        if !subviews.isEmpty {
            for child in subviews {
                child.includeInLayout = false
            }
            subviews[current % (subviews.count)].includeInLayout = true
        }
    }
    
}
