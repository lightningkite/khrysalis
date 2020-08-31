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
    
    private func findNextChildFocus(startingAtIndex: Int = 0) -> UIView? {
        var index = startingAtIndex
        while index < subviews.count - 1 {
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

    private func findNextParentFocus(startingAtIndex: Int = 0) -> UIView? {
        if let child = findNextChildFocus(startingAtIndex: startingAtIndex) {
            return child
        }

        if let superview = superview {
            let myIndex = superview.subviews.firstIndex(of: self) ?? -1
            return superview.findNextParentFocus(startingAtIndex: myIndex + 1)
        }

        return nil
    }
    
    func findFirstFocus() -> UIView? {
        return findNextChildFocus()
    }

    func findNextFocus(afterIndex: Int = 0) -> UIView? {
        if let superview = superview {
            let myIndex = superview.subviews.firstIndex(of: self) ?? -1
            return superview.findNextParentFocus(startingAtIndex: myIndex + 1)
        }

        return nil
    }
    
    func scrollToMe(animated: Bool = true) {
        if let superview = superview {
            superview.scrollRectToVisibleClimb(origin: self.frame.origin, size: self.frame.size, animated: animated)
        }
    }
    
    private func scrollRectToVisibleClimb(origin: CGPoint, size: CGSize, animated: Bool = true) {
        if let self = self as? UIScrollView {
            let topLeftOfCenteredView = CGPoint(
                x: origin.x + (size.width - self.frame.size.width) / 2,
                y: origin.y + (/*size.height*/CGFloat(16) - self.frame.size.height) / 2
            )
            let offset = CGPoint(
                x: max(0, min(self.contentSize.width - self.bounds.size.width, topLeftOfCenteredView.x)),
                y: max(0, min(self.contentSize.height - self.bounds.size.height, topLeftOfCenteredView.y))
            )
            self.setContentOffset(offset, animated: animated)
            if let superview = superview {
                superview.scrollRectToVisibleClimb(origin: self.frame.origin, size: size, animated: animated)
            }
        } else if let superview = superview {
            superview.scrollRectToVisibleClimb(origin: origin + self.frame.origin, size: size, animated: animated)
        }
    }
    
    func requestFocus(){
        if(self.canBecomeFirstResponder){
            self.becomeFirstResponder()
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

    public func setOnDoneClick(action: @escaping ()->Void) {
        let dg = LambdaDelegate(action: action)
        retain(item: dg, until: self.removed)
        delegate = dg
    }
    public class LambdaDelegate: NSObject, UITextFieldDelegate {
        public let action: ()->Void
        public init(action: @escaping ()->Void){
            self.action = action
        }

        public func textFieldDidBeginEditing(_ textField: UITextField) {
//            textField.scrollToMe()
        }

        public func textFieldShouldReturn(_ textField: UITextField) -> Bool {
            action()
            return true
        }
    }
}
