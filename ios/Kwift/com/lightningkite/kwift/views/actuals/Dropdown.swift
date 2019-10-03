//
//  Dropdown.swift
//  Lifting Generations
//
//  Created by Joseph Ivie on 3/28/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import UIKit


class Dropdown : UIControl {
    let pickerView = UIPickerView(frame: CGRect.zero)
    let toolbar: UIToolbar = {
        let toolBar = UIToolbar()
        toolBar.barStyle = UIBarStyle.default
        toolBar.isTranslucent = true
    
        toolBar.sizeToFit()
    
        return toolBar
    }()
    private weak var currentView: UIView?
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        sharedInit()
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        sharedInit()
    }
    private func sharedInit() {
        flex.alignItems(.stretch).alignContent(.stretch)
        
        self.isUserInteractionEnabled = true
        let doneButton = UIBarButtonItem(title: "Done", style: UIBarButtonItem.Style.plain, target: self, action: #selector(doneClick))
        let spaceButton = UIBarButtonItem(barButtonSystemItem: UIBarButtonItem.SystemItem.flexibleSpace, target: nil, action: nil)
        toolbar.setItems([ spaceButton, doneButton], animated: false)
        
        let tapRecognizer = UITapGestureRecognizer(target: self, action: #selector(launchPicker))
        self.addGestureRecognizer(tapRecognizer)
    }
    
    override var canBecomeFirstResponder: Bool {
        get {
            return true
        }
    }
    
    override var inputView: UIView {
        get {
            return pickerView
        }
    }
    override var inputAccessoryView: UIView? {
        return toolbar
    }
    var dataSource: UIPickerViewDataSource? {
        get {
            return pickerView.dataSource
        }
        set(value){
            pickerView.dataSource = value
        }
    }
    var delegate: UIPickerViewDelegate? {
        get {
            return pickerView.delegate
        }
        set(value){
            pickerView.delegate = value
        }
    }
    
    var selectedView: UIView? {
        get {
            return currentView
        }
        set(value) {
            currentView?.removeFromSuperview()
            currentView = nil
            if let newView = value {
                flex.addItem(newView).marginHorizontal(8)
                currentView = newView
            }
        }
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        currentView?.frame.size = self.frame.size
    }
    
    @objc func launchPicker() {
        becomeFirstResponder()
    }
    
    @objc func doneClick() {
        resignFirstResponder()
    }
    
}
