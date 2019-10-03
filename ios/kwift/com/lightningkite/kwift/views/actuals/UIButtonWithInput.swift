//
//  UIButtonWithInput.swift
//  Lifting Generations
//
//  Created by Joseph Ivie on 5/13/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit


class UIButtonWithInput: UIButton {
    private var underlyingInputAccessoryView: UIView? = nil
    private var underlyingCanBecomeFirstResponder: Bool = true
    
    override var inputAccessoryView: UIView? {
        get { return underlyingInputAccessoryView }
        set(value) {
            underlyingInputAccessoryView = value
        }
    }
    override var canBecomeFirstResponder: Bool {
        get { return underlyingCanBecomeFirstResponder }
        set(value) {
            underlyingCanBecomeFirstResponder = value
        }
    }
}
