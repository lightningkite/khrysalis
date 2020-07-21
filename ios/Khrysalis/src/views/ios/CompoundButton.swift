//
//  CompoundButton.swift
//  Klyp
//
//  Created by Joseph Ivie on 9/26/19.
//  Copyright © 2019 Klyp. All rights reserved.
//

import Foundation
import UIKit

public protocol CompoundButton: AnyObject {
    func setOnCheckedChangeListener(_ item: @escaping (CompoundButton, Bool) -> Void)
    func addOnCheckedChangeListener(_ item: @escaping (CompoundButton, Bool) -> Void)
    var isChecked: Bool { get set }
}

extension UISwitch : CompoundButton {
    public var isChecked: Bool {
        get { return isOn }
        set(value) { isOn = value }
    }
    public func addOnCheckedChangeListener(_ item: @escaping (CompoundButton, Bool) -> Void) {
        self.addAction(for: .valueChanged, id: "onCheckChanged", action: {
            item(self, self.isChecked)
        })
    }
    public func setOnCheckedChangeListener(_ item: @escaping (CompoundButton, Bool) -> Void) {
        self.addAction(for: .valueChanged, id: "onCheckChanged", action: {
            item(self, self.isChecked)
        })
    }
}
