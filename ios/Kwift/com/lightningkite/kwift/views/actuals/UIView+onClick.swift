//
//  UIView+ext.swift
//  Alamofire
//
//  Created by Joseph Ivie on 10/7/19.
//

import UIKit

public extension UIView {
    @objc func onClick(_ action: @escaping ()->Void) {
        onClick(disabledMilliseconds: 500, action)
    }
    @objc func onClick(disabledMilliseconds: Int64, _ action: @escaping ()->Void) {
        self.isUserInteractionEnabled = true
        var lastActivated = Date()
        let recognizer = UITapGestureRecognizer().addAction {
            if Date().timeIntervalSince(lastActivated) > Double(disabledMilliseconds)/1000.0 {
                action()
                lastActivated = Date()
            }
        }
        retain(as: "onClickRecognizer", item: recognizer)
        self.addGestureRecognizer(recognizer)
    }
    @objc func onLongClick(_ action: @escaping ()->Void) {
        self.isUserInteractionEnabled = true
        let recognizer = UILongPressGestureRecognizer().addAction { [weak self] in
            action()
        }
        retain(as: "onLongClickRecognizer", item: recognizer)
        self.addGestureRecognizer(recognizer)
    }
    @objc func onLongClickWithGR(_ action: @escaping (UILongPressGestureRecognizer)->Void) {
        self.isUserInteractionEnabled = true
        let recognizer = UILongPressGestureRecognizer()
        recognizer.addAction { [unowned recognizer, weak self] in
            action(recognizer)
        }
        retain(as: "onLongClickRecognizer", item: recognizer)
        self.addGestureRecognizer(recognizer)
    }
}

extension UIButton {
    @objc override public func onClick(_ action: @escaping ()->Void) {
        onClick(disabledMilliseconds: 500, action)
    }
    @objc override public func onClick(disabledMilliseconds: Int64, _ action: @escaping ()->Void) {
        var lastActivated = Date()
        self.addAction {
            if Date().timeIntervalSince(lastActivated) > Double(disabledMilliseconds)/1000.0 {
                action()
                lastActivated = Date()
            }
        }
    }
}
