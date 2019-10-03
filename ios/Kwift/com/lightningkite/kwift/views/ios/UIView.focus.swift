//
//  UIView.focus.swift
//  Lifting Generations
//
//  Created by Joseph Ivie on 6/11/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit



extension UIView {
    private func findNextChildFocus(afterIndex: Int = 0) -> UIView? {
        var index = afterIndex + 1
        while index < subviews.count {
            let sub = subviews[index]
            if sub is UITextField {
                return sub
            } else {
                if let subFocus = sub.findNextChildFocus() {
                    return subFocus
                }
            }
            index += 1
        }
        return nil
    }
    
    private func findNextParentFocus(afterIndex: Int = 0) -> UIView? {
        if let child = findNextChildFocus(afterIndex: afterIndex) {
            return child
        }
        
        if let superview = superview {
            let myIndex = superview.subviews.indexOf(self)
            return superview.findNextParentFocus(afterIndex: Int(myIndex))
        }
        
        return nil
    }
    
    func findNextFocus(afterIndex: Int = 0) -> UIView? {
        
        if let superview = superview {
            let myIndex = superview.subviews.indexOf(self)
            return superview.findNextParentFocus(afterIndex: Int(myIndex))
        }
        
        return nil
    }
}

extension UITextField {
    
    class DoneDelegate: NSObject, UITextFieldDelegate {
        static let shared = DoneDelegate()
        
        func textFieldShouldReturn(_ textField: UITextField) -> Bool {
            if let next = textField.findNextFocus() {
                next.becomeFirstResponder()
            } else {
                textField.resignFirstResponder()
            }
            return true
        }
    }
}
