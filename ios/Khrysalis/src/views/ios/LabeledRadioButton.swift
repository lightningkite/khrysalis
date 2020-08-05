//
//  LabeledSwitch.swift
//  Lifting Generations
//
//  Created by Joseph Ivie on 3/28/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import UIKit


public class LabeledRadioButton : LinearLayout, CompoundButton, HasLabelView {
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

    public let checkViewContainer: UIView = UIView(frame: .zero)
    public let checkView: UIView = UIView(frame: .zero)
    public let labelView: UILabel = UILabel(frame: .zero)
    public var isChecked: Bool = false {
        didSet {
            UIView.animate(withDuration: 0.25, animations: { [weak self] in
                self?.resize()
            })
            onCheckChanged(self, isChecked)
            onCheckChangedOther(self, isChecked)
        }
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
            if var p = params(for: checkViewContainer) {
                p.gravity = AlignPair(p.gravity.horizontal, value)
                params(for: checkViewContainer, setTo: p)
            }
        }
    }

    override public init(frame: CGRect) {
        super.init(frame: frame)
        labelView.numberOfLines = 0
        
        addSubview(checkViewContainer, LinearLayout.LayoutParams(
            size: CGSize(width: 24, height: 24),
            margin: UIEdgeInsets(top: 8, left: 8, bottom: 8, right: 8),
            gravity: .center,
            weight: 0
        ))
        checkViewContainer.addSubview(checkView)
        checkViewContainer.addOnLayoutSubviews { [weak self] in
            guard let self = self else { return }
            self.resize()
        }
        addSubview(labelView, LinearLayout.LayoutParams(
            size: .zero,
            margin: UIEdgeInsets(top: 8, left: 0, bottom: 8, right: 8),
            gravity: .center,
            weight: 1
        ))

        checkViewContainer.layer.borderWidth = 1
        checkViewContainer.layer.cornerRadius = 12

        let tapRecognizer = UITapGestureRecognizer().addAction(until: removed) { [weak self] in
            if let self = self {
                self.isChecked = !self.isChecked
            }
        }
        self.addGestureRecognizer(tapRecognizer)
        
        post {
            self.checkView.layer.backgroundColor = self.labelView.textColor.cgColor
            self.checkViewContainer.layer.borderColor = self.labelView.textColor.cgColor
        }
    }

    func resize(){
        if isChecked {
            checkView.frame = checkViewContainer.bounds.insetBy(dx: 4, dy: 4)
            checkView.layer.cornerRadius = checkView.frame.size.width / 2
        } else {
            let centerX = checkViewContainer.bounds.origin.x + checkViewContainer.bounds.size.width / 2
            let centerY = checkViewContainer.bounds.origin.y + checkViewContainer.bounds.size.height / 2
            checkView.frame = CGRect(x: centerX, y: centerY, width: 0, height: 0)
            checkView.layer.cornerRadius = checkView.frame.size.width / 2
        }
    }

    required public init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
}
