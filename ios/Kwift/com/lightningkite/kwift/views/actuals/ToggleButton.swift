//
//  ToggleButton.swift
//  Klyp
//
//  Created by Joseph Ivie on 9/26/19.
//  Copyright © 2019 Klyp. All rights reserved.
//

import Foundation
import UIKit


public class ToggleButton: UIButton, CompoundButton {
    public var onCheckChanged: (Bool) -> Void = { _ in }
    public var isOn: Bool = false {
        didSet {
            self.isSelected = isOn
            onCheckChanged(isOn)
        }
    }
}
