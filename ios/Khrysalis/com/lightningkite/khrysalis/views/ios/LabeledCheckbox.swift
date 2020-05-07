//
//  LabeledSwitch.swift
//  Lifting Generations
//
//  Created by Joseph Ivie on 3/28/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import UIKit


public class LabeledCheckbox : LinearLayout, CompoundButton {

    public let checkViewContainer: UIView = UIView(frame: .zero)
    public let checkView: UILabel = UILabel(frame: .zero)
    public let labelView: UILabel = UILabel(frame: .zero)
    public var onCheckChanged: (Bool) -> Void = { _ in }
    public func addOnCheckChanged(_ item: @escaping (Bool) -> Void) {
        let prev = onCheckChanged
        onCheckChanged = { it in
            prev(it)
            item(it)
        }
    }
    public var isOn: Bool = false {
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
        checkViewContainer.addOnLayoutSubviews { [weak checkView, weak checkViewContainer] in
            guard let checkView = checkView, let checkViewContainer = checkViewContainer else { return }
            checkView.frame = checkViewContainer.bounds
        }
        addSubview(labelView, LinearLayout.LayoutParams(
            size: .zero,
            margin: UIEdgeInsets(top: 8, left: 0, bottom: 8, right: 8),
            gravity: .center,
            weight: 1
        ))

        checkView.text = "✓"
        checkView.transform = CGAffineTransform(scaleX: 0.01, y: 0.01)
        checkView.textAlignment = .center

        checkViewContainer.layer.borderWidth = 1
        checkViewContainer.layer.borderColor = checkView.textColor.cgColor
        checkViewContainer.layer.cornerRadius = 2

        let tapRecognizer = UITapGestureRecognizer().addAction(until: removed) { [weak self] in
            if let self = self {
                self.isOn = !self.isOn
            }
        }
        self.addGestureRecognizer(tapRecognizer)
    }



    required public init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
}
