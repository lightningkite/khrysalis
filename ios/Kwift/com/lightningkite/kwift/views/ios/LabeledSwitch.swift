//
//  LabeledSwitch.swift
//  Lifting Generations
//
//  Created by Joseph Ivie on 3/28/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import UIKit


public class LabeledSwitch : LinearLayout {
    
    public var control: UIControl {
        return switchView
    }
    public let switchView: UISwitch = UISwitch(frame: CGRect.zero)
    public let labelView: UILabel = UILabel(frame: CGRect.zero)

    override public init(frame: CGRect) {
        super.init(frame: frame)
        labelView.numberOfLines = 0
        self.addSubview(labelView, LinearLayout.LayoutParams(
            size: .zero,
            margin: UIEdgeInsets(top: 8, left: 8, bottom: 8, right: 8),
            gravity: .center,
            weight: 0
        ))
        self.addSubview(switchView, LinearLayout.LayoutParams(
            size: .zero,
            margin: UIEdgeInsets(top: 8, left: 0, bottom: 8, right: 8),
            gravity: .center,
            weight: 1
        ))
    }

    required public init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
}
