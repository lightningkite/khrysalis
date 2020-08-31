//
//  UIViewController+keyboard.swift
//  Khrysalis
//
//  Created by Joseph Ivie on 10/21/19.
//  Copyright © 2019 Lightning Kite. All rights reserved.
//

import Foundation
import UIKit


// MARK: - Extensions to make view controller keyboard-aware.
// Move these to a separate file if you want

public extension ViewDependency {
    func runKeyboardUpdate(root: UIView? = nil, discardingRoot: UIView? = nil) {
        let currentFocus = UIResponder.current as? UIView
        var dismissOld = false
        if let currentFocus = currentFocus {
            if let discardingRoot = discardingRoot, discardingRoot.contains(currentFocus) {
                //We're discarding the focus
                dismissOld = true
            }
        }
        if let root = root, let keyboardView = root.findFirstFocus() {
            keyboardView.requestFocus()
            dismissOld = false
        }
        if dismissOld {
            self.parentViewController.view.endEditing(true)
        }
    }
}

private extension UIView {
    func contains(other: UIView?) -> Bool {
        if self === other { return true }
        guard let other = other else { return false }
        return self.contains(other: other.superview)
    }
}

public extension UIView {
    var firstResponder: UIView? {
        guard !isFirstResponder else { return self }
        
        for subview in subviews {
            if let firstResponder = subview.firstResponder {
                return firstResponder
            }
        }
        
        return nil
    }
}

extension UIResponder {
    private weak static var _currentFirstResponder: UIResponder? = nil

    public static var current: UIResponder? {
        UIResponder._currentFirstResponder = nil
        UIApplication.shared.sendAction(#selector(findFirstResponder(sender:)), to: nil, from: nil, for: nil)
        return UIResponder._currentFirstResponder
    }

    @objc internal func findFirstResponder(sender: AnyObject) {
        UIResponder._currentFirstResponder = self
    }
}

//public extension UIViewController {
//    /// How much space (in percentage of remaining available space) to designate under the focused
//    /// text field. The higher the number, the close the textfield will be to the keyboard. If you
//    /// have a field that won't be covered by the keyboard, keep this number close to 1.
//    func spacingPercentageFromTop() -> CGFloat {
//        return 0.8
//    }
//    
//    /// Asks the system to resign all first responders (usually input fields), which effectively
//    /// causes the keyboard to dismiss itself.
//    func resignAllFirstResponders() {
//        view.endEditing(true)
//    }
//    
//    func hideKeyboardWhenTappedAround() {
//        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(UIViewController.dismissKeyboard))
//        tap.cancelsTouchesInView = false
//        view.addGestureRecognizer(tap)
//    }
//    
//    @objc func dismissKeyboard() {
//        resignAllFirstResponders()
//    }
//    
//    func addKeyboardObservers() {
//        NotificationCenter.default.addObserver(
//            self,
//            selector: #selector(keyboardWillChangeFrame),
//            name: UIResponder.keyboardWillChangeFrameNotification,
//            object: nil
//        )
//        NotificationCenter.default.addObserver(
//            self,
//            selector: #selector(keyboardWillHide),
//            name: UIResponder.keyboardWillHideNotification,
//            object: nil
//        )
//    }
//    
//    /// Remove observers that were added previously.
//    func removeKeyboardObservers() {
//        NotificationCenter.default.removeObserver(
//            self,
//            name: UIResponder.keyboardWillChangeFrameNotification,
//            object: self.view.window
//        )
//        NotificationCenter.default.removeObserver(
//            self,
//            name: UIResponder.keyboardWillHideNotification,
//            object: self.view.window
//        )
//    }
//    
//    
//    /// Method's notified when the keyboard is about to be shown or change its size.
//    ///
//    /// - Parameter notification: System keyboard notification
//    @objc func keyboardWillChangeFrame(notification: NSNotification) {
//        if
//            let window = view.window,
//            let responder = view.firstResponder,
//            let userInfo = notification.userInfo,
//            let keyboardFrameValue = userInfo[UIResponder.keyboardFrameBeginUserInfoKey] as? NSValue,
//            let keyboardAnimationDuration = userInfo[UIResponder.keyboardAnimationDurationUserInfoKey] as? NSNumber
//        {
//            let keyboardHeight = keyboardFrameValue.cgRectValue.height
//            UIView.animate(
//                withDuration: keyboardAnimationDuration.doubleValue,
//                animations: {
//                    window.frame.origin.y = min(
//                        0,
//                        -min(
//                            keyboardHeight,
//                            responder.layer.position.y
//                                - (window.bounds.height - keyboardHeight - responder.bounds.height)
//                                * self.spacingPercentageFromTop()
//                        )
//                    )
//                    self.view.setNeedsLayout()
//                    self.view.layoutIfNeeded()
//            }
//            )
//        }
//    }
//    
//    /// Method's notified when the keyboard is about to be dismissed.
//    ///
//    /// - Parameter notification: System keyboard notification
//    @objc func keyboardWillHide(notification: NSNotification) {
//        if
//            let window = self.view.window,
//            let userInfo = notification.userInfo,
//            let animationDuration = userInfo[UIResponder.keyboardAnimationDurationUserInfoKey] as? NSNumber
//        {
//            UIView.animate(
//                withDuration: animationDuration.doubleValue,
//                animations: {
//                    window.frame.origin.y = 0
//                    self.view.layoutIfNeeded()
//            }
//            )
//        }
//    }
//}
