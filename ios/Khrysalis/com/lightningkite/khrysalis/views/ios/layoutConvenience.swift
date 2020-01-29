//
//  layoutConvenience.swift
//  Khrysalis
//
//  Created by Joseph Ivie on 10/14/19.
//  Copyright © 2019 Lightning Kite. All rights reserved.
//

import UIKit

public enum LayoutSettings {
    static let debugDraw = false
}

public extension UIView {
    func addSubview<T: UIView>(_ view: T, setup: (T)->Void) {
        setup(view)
        self.addSubview(view)
    }
}

public extension UIScrollView {
    func addVerticalSubview<T: UIView>(_ view: T, fill: Bool = false, setup: (T)->Void) {
        setup(view)
        self.addSubview(view)
        self.addOnLayoutSubviews { [weak self, weak view] in
            guard let self = self, let view = view else { return }
            let measuredSize = view.sizeThatFits(self.bounds.size)
            view.frame = CGRect(
                x: 0,
                y: 0,
                width: self.bounds.size.width,
                height: fill ? max(measuredSize.height, self.bounds.size.height) : measuredSize.height
            )
            self.contentSize = CGSize(width: 0, height: measuredSize.height)
        }
    }
    func addHorizontalSubview<T: UIView>(_ view: T, fill: Bool = false, setup: (T)->Void) {
        setup(view)
        self.addSubview(view)
        self.addOnLayoutSubviews { [weak self, weak view] in
            guard let self = self, let view = view else { return }
            let measuredSize = view.sizeThatFits(self.bounds.size)
            view.frame = CGRect(
                x: 0,
                y: 0,
                width: fill ? max(measuredSize.width, self.bounds.size.width) : measuredSize.width,
                height: self.bounds.size.height
            )
            self.contentSize = CGSize(width: measuredSize.width, height: 0)
        }
    }
}

public extension LinearLayout {
    func addSubview<T: UIView>(
        _ view: T,
        minimumSize: CGSize = .zero,
        size: CGSize = .zero,
        margin: UIEdgeInsets = .zero,
        padding: UIEdgeInsets = .zero,
        gravity: AlignPair = .center,
        weight: CGFloat = 0,
        setup: (T)->Void
    ) {
        setup(view)
        self.addSubview(view, minimumSize: minimumSize, size: size, margin: margin, padding: padding, gravity: gravity, weight: weight)
    }
}


public extension FrameLayout {
    func addSubview<T: UIView>(
        _ view: T,
        minimumSize: CGSize = .zero,
        size: CGSize = .zero,
        margin: UIEdgeInsets = .zero,
        padding: UIEdgeInsets = .zero,
        gravity: AlignPair = .center,
        setup: (T)->Void
    ) {
        setup(view)
        self.addSubview(view, minimumSize: minimumSize, size: size, margin: margin, padding: padding, gravity: gravity)
    }
}
