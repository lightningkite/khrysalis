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
    public var onCheckChanged: (CompoundButton, Bool) -> Void = { (_, _) in }
    public func setOnCheckedChangeListener(_ item: @escaping (CompoundButton, Bool) -> Void) {
        onCheckChanged = item
    }
    public var onCheckChangedOther: (CompoundButton, Bool) -> Void = { (_, _) in }
    public func addOnCheckedChangeListener(_ item: @escaping (CompoundButton, Bool) -> Void) {
        let prev = onCheckChangedOther
        onCheckChangedOther = { (self, it) in
            prev(self, it)
            item(self, it)
        }
    }
    
    public var textOn: String = "On"{
        didSet {
            syncText()
        }
    }
    
    public var textOff: String = "Off"{
        didSet {
            syncText()
        }
    }
    
    public var isChecked: Bool = false {
        didSet {
            self.isSelected = isChecked
            syncText()
            onCheckChanged(self, isChecked)
            onCheckChangedOther(self, isChecked)
        }
    }
    
    public func syncText(){
        if isChecked{
            var toSet = textOn
            if textAllCaps {
                toSet = toSet.uppercased()
            }
            setAttributedTitle(
                NSAttributedString(string: toSet, attributes: [.kern: letterSpacing * (titleLabel?.font.pointSize ?? 12)]),
                for: .normal
            )
        } else{
            var toSet = textOff
            if textAllCaps {
                toSet = toSet.uppercased()
            }
            setAttributedTitle(
                NSAttributedString(string: toSet, attributes: [.kern: letterSpacing * (titleLabel?.font.pointSize ?? 12)]),
                for: .normal
            )
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
            self.isChecked = !self.isChecked
        }
    }
}

public extension ToggleButton{
    override var textResource: String {
        get {
            return title(for: .normal) ?? ""
        }
        set(value) {
            self.textOn = value
            self.textOff = value
        }
    }
    
    override var textString: String {
        get{
            return title(for: .normal) ?? ""
        }
        set(value){
            self.textOn = value
            self.textOff = value
        }
    }
}
