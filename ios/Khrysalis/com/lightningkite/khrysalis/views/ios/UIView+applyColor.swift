//
//  UIView+applyColor.swift
//  Khrysalis
//
//  Created by Joseph Ivie on 7/2/20.
//  Copyright © 2020 Lightning Kite. All rights reserved.
//

import UIKit

extension UIView {
    func applyColor(_ color: UIColor, to: (UIColor)->Void) {
        to(color)
    }
    func applyColor(_ colorSet: @escaping (_ state: UIControl.State) -> UIColor, to: @escaping (UIColor)->Void) {
        if let self = self as? UIControl {
            self.addOnStateChange(retainer: self, id: 0, action: { state in
                to(colorSet(state))
            })
        } else if let self = self as? CompoundButton {
            self.addOnCheckChanged({ isOn in
                to(colorSet(isOn ? .selected : .normal))
            })
        }
        to(colorSet(.normal))
    }
}
