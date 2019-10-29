//
//  UIView+ext.swift
//  Alamofire
//
//  Created by Joseph Ivie on 10/7/19.
//

import UIKit

public extension UIView {
    @objc func onClick(_ action: @escaping ()->Void) {
        self.isUserInteractionEnabled = true
        let recognizer = UITapGestureRecognizer().addAction { [weak self] in
            action()
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
        self.addAction {
            action()
        }
    }
}
