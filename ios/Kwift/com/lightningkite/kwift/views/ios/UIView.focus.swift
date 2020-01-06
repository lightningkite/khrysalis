//
//  UIView.focus.swift
//  Lifting Generations
//
//  Created by Joseph Ivie on 6/11/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit



public extension UIView {
    
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
    
    func scrollToMe(animated: Bool = true) {
        if let superview = superview {
            superview.scrollRectToVisibleClimb(origin: self.frame.origin, size: self.frame.size, animated: animated)
        }
    }
    
    private func scrollRectToVisibleClimb(origin: CGPoint, size: CGSize, animated: Bool = true) {
        print("Climbing... \(type(of:self))")
        if let self = self as? UIScrollView {
            print("It's a scroll view!")
            print("Origin: \(origin)")
            print("Size: \(size)")
            print("Frame: \(self.frame.size)")
            let topLeftOfCenteredView = CGPoint(
                x: origin.x + (size.width - self.frame.size.width) / 2,
                y: origin.y + (size.height - self.frame.size.height) / 2
            )
            print("Goal point: \(topLeftOfCenteredView)")
            print("Y Limits: \(self.contentSize.height - self.bounds.size.height) .. 0")
            let offset = CGPoint(
                x: max(0, min(self.contentSize.width - self.bounds.size.width, topLeftOfCenteredView.x)),
                y: max(0, min(self.contentSize.height - self.bounds.size.height, topLeftOfCenteredView.y))
            )
            print("I'm going to set it to \(offset)")
            self.setContentOffset(offset, animated: animated)
            if let superview = superview {
                print("Get my parents to pass to me...")
                superview.scrollRectToVisibleClimb(origin: self.frame.origin, size: size, animated: animated)
            }
        } else if let superview = superview {
            print("My parent needs to scroll to my part at \(origin) + \(self.frame.origin)")
            superview.scrollRectToVisibleClimb(origin: origin + self.frame.origin, size: size, animated: animated)
        }
    }
}

func +(lhs: CGPoint, rhs: CGPoint) -> CGPoint {
    return CGPoint(x: lhs.x + rhs.x, y: lhs.y + rhs.y)
}

extension UITextField {

    public class DoneDelegate: NSObject, UITextFieldDelegate {
        static let shared = DoneDelegate()
        
        public func textFieldDidBeginEditing(_ textField: UITextField) {
//            textField.scrollToMe()
        }

        public func textFieldShouldReturn(_ textField: UITextField) -> Bool {
            if let next = textField.findNextFocus() {
                next.becomeFirstResponder()
            } else {
                textField.resignFirstResponder()
            }
            return true
        }
    }
}
