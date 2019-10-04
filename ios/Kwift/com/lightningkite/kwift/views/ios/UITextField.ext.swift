//
//  UITextField.ext.swift
//  Lifting Generations
//
//  Created by Joseph Ivie on 4/30/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import UIKit

public extension UITextField {
    func setLeftPaddingPoints(_ amount:CGFloat){
        let paddingView = UIView(frame: CGRect(x: 0, y: 0, width: amount, height: self.frame.size.height))
        self.leftView = paddingView
        self.leftViewMode = .always
    }
    func setRightPaddingPoints(_ amount:CGFloat) {
        let paddingView = UIView(frame: CGRect(x: 0, y: 0, width: amount, height: self.frame.size.height))
        self.rightView = paddingView
        self.rightViewMode = .always
    }
    var numberOfLines: Int {
        get { return 0 }
        set(value) { }
    }
    var baselineAdjustment: UIBaselineAdjustment {
        get { return .alignBaselines }
        set(value){}
    }
    func addDismissButton(){
        var toolbar = UIToolbar()
        toolbar.barStyle = UIBarStyle.default
        toolbar.isTranslucent = true
        toolbar.sizeToFit()
        let doneButton = UIBarButtonItem(title: "Done", style: UIBarButtonItem.Style.plain) { [weak self] in
            self?.resignFirstResponder()
        }
        let spaceButton = UIBarButtonItem(barButtonSystemItem: UIBarButtonItem.SystemItem.flexibleSpace, target: nil, action: nil)
        toolbar.setItems([ spaceButton, doneButton], animated: false)
        inputAccessoryView = toolbar
    }
}
