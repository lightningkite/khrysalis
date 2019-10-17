//
//  ToggleButton.swift
//  Klyp
//
//  Created by Joseph Ivie on 9/26/19.
//  Copyright © 2019 Klyp. All rights reserved.
//

import Foundation
import UIKit


public class ToggleButton: UIButtonWithLayer, CompoundButton {
    public var onCheckChanged: (Bool) -> Void = { _ in }
    public var isOn: Bool = false {
        didSet {
            self.isSelected = isOn
            onCheckChanged(isOn)
        }
    }
    
    override public init(frame: CGRect) {
        super.init(frame: frame)
        commonInit()
    }
    
    override public required init?(coder: NSCoder) {
        super.init(coder: coder)
        commonInit()
    }
    
    func commonInit(){
        onClick { [unowned self] in
            self.isOn = !self.isOn
        }
    }
}
