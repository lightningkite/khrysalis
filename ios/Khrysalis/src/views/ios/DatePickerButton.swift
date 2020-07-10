//
//  DatePickerButton.swift
//  Lifting Generations
//
//  Created by Joseph Ivie on 5/13/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit


public class DatePickerButton : UIControl {
    let pickerView = UIDatePicker(frame: CGRect.zero)
    let toolbar: UIToolbar = {
        let toolBar = UIToolbar()
        toolBar.barStyle = UIBarStyle.default
        toolBar.isTranslucent = true

        toolBar.sizeToFit()

        return toolBar
    }()
    private weak var currentView: UIView?

    var date: Date = Date()

    override init(frame: CGRect) {
        super.init(frame: frame)
        sharedInit()
    }

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        sharedInit()
    }
    private func sharedInit() {

        self.isUserInteractionEnabled = true
        let doneButton = UIBarButtonItem(title: "Done", style: UIBarButtonItem.Style.plain, target: self, action: #selector(doneClick))
        let spaceButton = UIBarButtonItem(barButtonSystemItem: UIBarButtonItem.SystemItem.flexibleSpace, target: nil, action: nil)
        toolbar.setItems([ spaceButton, doneButton], animated: false)

        let tapRecognizer = UITapGestureRecognizer(target: self, action: #selector(launchPicker))
        self.addGestureRecognizer(tapRecognizer)
    }

    override public var canBecomeFirstResponder: Bool {
        get {
            return true
        }
    }

    override public var inputView: UIView {
        get {
            return pickerView
        }
    }
    override public var inputAccessoryView: UIView? {
        return toolbar
    }

    var selectedView: UIView? {
        get {
            return currentView
        }
        set(value) {
            currentView?.removeFromSuperview()
            currentView = nil
            if let newView = value {
                currentView = newView
                addSubview(newView)
            }
        }
    }

    override public func layoutSubviews() {
        super.layoutSubviews()
        currentView?.frame.size = self.frame.size
    }

    @objc public func launchPicker() {
        becomeFirstResponder()
    }

    @objc public func doneClick() {
        resignFirstResponder()
    }

}

