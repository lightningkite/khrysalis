//
//  general.swift
//  KwiftTemplate
//
//  Created by Joseph Ivie on 8/20/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit

public typealias View = UIView
public class ViewDependency {
    public init(){}
    public func getString(_ reference: StringReference) -> String {
        return reference
    }
}

extension UIButton {
    @objc override public func onClick(_ action: @escaping ()->Void) {
        self.addAction {
            action()
        }
    }
}

extension UIView {
    @objc public func onClick(_ action: @escaping ()->Void) {
        self.addGestureRecognizer(UITapGestureRecognizer().addAction { [weak self] in
            action()
        })
    }
}
