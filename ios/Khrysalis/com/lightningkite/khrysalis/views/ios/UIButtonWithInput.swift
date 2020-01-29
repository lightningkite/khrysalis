//
//  UIButtonWithInput.swift
//  Lifting Generations
//
//  Created by Joseph Ivie on 5/13/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit


public class UIButtonWithInput: UIButtonWithLayer {
    private var underlyingInputAccessoryView: UIView? = nil
    private var underlyingCanBecomeFirstResponder: Bool = true

    override public var inputAccessoryView: UIView? {
        get { return underlyingInputAccessoryView }
        set(value) {
            underlyingInputAccessoryView = value
        }
    }
    override public var canBecomeFirstResponder: Bool {
        get { return underlyingCanBecomeFirstResponder }
        set(value) {
            underlyingCanBecomeFirstResponder = value
        }
    }
}
