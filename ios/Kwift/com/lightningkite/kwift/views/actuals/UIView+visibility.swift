//
//  UIView+visibility.swift
//  Kwift
//
//  Created by Joseph Ivie on 10/16/19.
//  Copyright © 2019 Lightning Kite. All rights reserved.
//

import UIKit

public extension UIView {
    
    enum Visibility { case gone, invisible, visible }
    static let GONE = Visibility.gone
    static let VISIBLE = Visibility.visible
    static let INVISIBLE = Visibility.invisible
    
    var visibility: Visibility {
        get {
            if self.isHidden {
                return .invisible
            }
            return .visible
        }
        set(value) {
            switch value {
            case .gone:
                self.isHidden = true
            case .invisible:
                self.isHidden = true
            case .visible:
                self.isHidden = false
            }
        }
    }
}
