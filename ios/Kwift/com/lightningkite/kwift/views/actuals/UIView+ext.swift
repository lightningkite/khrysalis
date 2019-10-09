//
//  UIView+ext.swift
//  Alamofire
//
//  Created by Joseph Ivie on 10/7/19.
//

import UIKit
import FlexLayout

public extension UIView {
    @objc func onClick(_ action: @escaping ()->Void) {
        self.addGestureRecognizer(UITapGestureRecognizer().addAction { [weak self] in
            action()
        })
    }
    
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
                self.flex.display(.none)
            case .visible:
                self.isHidden = false
                self.flex.display(.flex)
            case .invisible:
                self.isHidden = true
                self.flex.display(.flex)
            }
        }
    }
}
