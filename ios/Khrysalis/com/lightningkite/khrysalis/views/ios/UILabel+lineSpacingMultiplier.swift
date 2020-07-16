//
//  UILabel+lineSpacingMultiplier.swift
//  Lifting Generations
//
//  Created by Joseph Ivie on 6/21/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit


extension UILabel {
    var lineSpacingMultiplier: CGFloat {
        get {
            if let attr = self.attributedText?.attribute(.paragraphStyle, at: 0, effectiveRange: nil) as? NSParagraphStyle {
                return attr.lineHeightMultiple
            } else {
                return 1
            }
        }
        set(value){
            guard let labelText = self.text else { return }
            
            let paragraphStyle = NSMutableParagraphStyle()
            paragraphStyle.lineHeightMultiple = value
            
            let attributedString:NSMutableAttributedString
            if let labelattributedText = self.attributedText {
                attributedString = NSMutableAttributedString(attributedString: labelattributedText)
            } else {
                attributedString = NSMutableAttributedString(string: labelText)
            }
            
            // Line spacing attribute
            attributedString.addAttribute(NSAttributedString.Key.paragraphStyle, value:paragraphStyle, range:NSMakeRange(0, attributedString.length))
            
            self.attributedText = attributedString
        }
    }
    
    public var maxLines: Int32{
        get{
            return Int32(self.numberOfLines)
        }
        set(newLineCount){
            if self.numberOfLines != Int(newLineCount) {
                self.numberOfLines = Int(newLineCount)
                self.notifyParentSizeChanged()
            }
        }
    }
}
