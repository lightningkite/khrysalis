//
//  LabeledSwitch.swift
//  Lifting Generations
//
//  Created by Joseph Ivie on 3/28/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import UIKit


public class LabeledSwitch : LinearLayout, CompoundButton, HasLabelView {
    public func setOnCheckedChangeListener(_ item: @escaping (CompoundButton, Bool) -> Void) {
        switchView.setOnCheckedChangeListener(item)
    }
    public func addOnCheckedChangeListener(_ item: @escaping (CompoundButton, Bool) -> Void) {
        switchView.addOnCheckedChangeListener(item)
    }
    public var isChecked: Bool {
        get { return switchView.isOn }
        set(value) {
            switchView.isOn = value
        }
    }
    
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
            weight: 1
        ))
        self.addSubview(switchView, LinearLayout.LayoutParams(
            size: .zero,
            margin: UIEdgeInsets(top: 8, left: 0, bottom: 8, right: 8),
            gravity: .center,
            weight: 0
        ))
    }
    
    public var verticalAlign: Align {
        get {
            if let p = params(for: labelView) {
                return p.gravity.vertical
            }
            return .center
        }
        set(value) {
            if var p = params(for: labelView) {
                p.gravity = AlignPair(p.gravity.horizontal, value)
                params(for: labelView, setTo: p)
            }
            if var p = params(for: switchView) {
                p.gravity = AlignPair(p.gravity.horizontal, value)
                params(for: switchView, setTo: p)
            }
        }
    }

    required public init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
}
