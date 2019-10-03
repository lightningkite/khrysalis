//
//  CompoundButton.swift
//  Klyp
//
//  Created by Joseph Ivie on 9/26/19.
//  Copyright © 2019 Klyp. All rights reserved.
//

import Foundation
import UIKit

protocol CompoundButton: AnyObject {
    var onCheckChanged: (Bool) -> Void { get set }
    var isOn: Bool { get set }
}

extension UISwitch : CompoundButton {
    var onCheckChanged: (Bool) -> Void {
        get{
            return { _ in }
        }
        set(value){
            self.addAction(for: .valueChanged, id: "onCheckChanged", action: {
                value(self.isOn)
            })
        }
    }
}
