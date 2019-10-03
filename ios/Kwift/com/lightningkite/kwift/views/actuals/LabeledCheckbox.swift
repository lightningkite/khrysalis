//
//  LabeledSwitch.swift
//  Lifting Generations
//
//  Created by Joseph Ivie on 3/28/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import UIKit
import FlexLayout


class LabeledCheckbox : UIView, CompoundButton {
    
    let checkViewContainer: UIView = UIView(frame: .zero)
    let checkView: UILabel = UILabel(frame: .zero)
    let labelView: UILabel = UILabel(frame: .zero)
    var onCheckChanged: (Bool) -> Void = { _ in }
    var isOn: Bool = false {
        didSet {
            if isOn {
                UIView.animate(withDuration: 0.25, animations: { [checkView] in
                    checkView.transform = CGAffineTransform(scaleX: 1, y: 1)
                })
            } else {
                UIView.animate(withDuration: 0.25, animations: { [checkView] in
                    checkView.transform = CGAffineTransform(scaleX: 0.01, y: 0.01)
                })
            }
            onCheckChanged(isOn)
        }
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        labelView.numberOfLines = 0
        flex.direction(.row).alignContent(.center)
        flex.addItem(checkViewContainer)
            .marginRight(8)
            .width(24)
            .height(24)
            .direction(.row)
            .alignContent(.center)
            .addItem(checkView)
            .width(24)
            .height(24)
        flex.addItem(labelView).grow(1)
        
        checkView.text = "✓"
        checkView.transform = CGAffineTransform(scaleX: 0.01, y: 0.01)
        checkView.textAlignment = .center
        
        checkViewContainer.layer.borderWidth = 1
        checkViewContainer.layer.borderColor = checkView.textColor.cgColor
        checkViewContainer.layer.cornerRadius = 2
        
        let tapRecognizer = UITapGestureRecognizer().addAction { [weak self] in
            if let self = self {
                self.isOn = !self.isOn
            }
        }
        self.addGestureRecognizer(tapRecognizer)
    }
    
    
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
}
