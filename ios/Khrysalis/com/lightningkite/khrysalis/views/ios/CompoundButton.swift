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
    func addOnCheckChanged(_ item: @escaping (Bool) -> Void)
    var isOn: Bool { get set }
}

extension UISwitch : CompoundButton {
    public func addOnCheckChanged(_ item: @escaping (Bool) -> Void) {
        self.addAction(for: .valueChanged, id: "onCheckChanged", action: {
            item(self.isOn)
        })
    }
}
