//
//  UIView+applyColor.swift
//  Khrysalis
//
//  Created by Joseph Ivie on 7/2/20.
//  Copyright © 2020 Lightning Kite. All rights reserved.
//

import UIKit


public func applyColor(_ view: UIView?, _ color: UIColor, to: (UIColor, UIControl.State)->Void) {
    to(color, .normal)
}
public func applyColor(_ view: UIView?, _ colorSet: @escaping (_ state: UIControl.State) -> UIColor, to: @escaping (UIColor, UIControl.State)->Void) {
    if let self = view as? CompoundButton {
            self.addOnCheckedChangeListener({ (_, isChecked) in
                let state: UIControl.State = isChecked ? .selected : .normal
                to(colorSet(state), state)
            })
    } else if let self = view as? UIControl {
        self.addOnStateChange(retainer: self, id: 0, action: { state in
            to(colorSet(state), state)
        })
    }
    to(colorSet(.normal), .normal)
}
