//
//  PreviewVariedFlipper.swift
//  Klyp
//
//  Created by Joseph Ivie on 9/26/19.
//  Copyright © 2019 Klyp. All rights reserved.
//

import Foundation
import UIKit

class PreviewVariedFlipper: UIView {
    
    var current = Int(arc4random())
    
    override func layoutSubviews() {
        super.layoutSubviews()
        
        for child in subviews {
            child.isHidden = true
        }
        subviews[current % (subviews.count)].isHidden = false
    }
}
