//
//  LabeledSwitch.swift
//  Lifting Generations
//
//  Created by Joseph Ivie on 3/28/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import UIKit
import FlexLayout


public class LabeledSwitch : UIView {

    public let switchView: UISwitch = UISwitch(frame: CGRect.zero)
    public let labelView: UILabel = UILabel(frame: CGRect.zero)

    override public init(frame: CGRect) {
        super.init(frame: frame)
        labelView.numberOfLines = 0
        flex.direction(.row).alignContent(.center)
        flex.addItem(labelView).grow(1).marginRight(8)
        flex.addItem(switchView)
    }

    required public init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
}
