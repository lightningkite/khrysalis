//
//  UITextView.placeholder.swift
//  Lifting Generations
//
//  Created by Joseph Ivie on 4/1/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import UIKit


public extension UITextView {
    var placeholder: String {
        get {
            return text
        }
        set(value) {
            text = value
        }
    }
    func setLeftPaddingPoints(_ amount:CGFloat){
        self.textContainerInset.left = amount
    }
    func setRightPaddingPoints(_ amount:CGFloat) {
        self.textContainerInset.right = amount
    }
    var numberOfLines: Int {
        get { return 0 }
        set(value) { }
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
