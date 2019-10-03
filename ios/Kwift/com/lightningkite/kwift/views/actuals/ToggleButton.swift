//
//  ToggleButton.swift
//  Klyp
//
//  Created by Joseph Ivie on 9/26/19.
//  Copyright © 2019 Klyp. All rights reserved.
//

import Foundation
import UIKit


class ToggleButton: UIButton, CompoundButton {
    var onCheckChanged: (Bool) -> Void = { _ in }
    var isOn: Bool = false {
        didSet {
            self.isSelected = isOn
            onCheckChanged(isOn)
        }
    }
}
