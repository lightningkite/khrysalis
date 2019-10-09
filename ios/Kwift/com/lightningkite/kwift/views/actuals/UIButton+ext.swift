//
//  UIButton+ext.swift
//  Alamofire
//
//  Created by Joseph Ivie on 10/7/19.
//

import UIKit


extension UIButton {
    @objc override public func onClick(_ action: @escaping ()->Void) {
        self.addAction {
            action()
        }
    }
}
