//
//  layoutConvenience.swift
//  Kwift
//
//  Created by Joseph Ivie on 10/14/19.
//  Copyright © 2019 Lightning Kite. All rights reserved.
//

import UIKit

public extension UIView {
    func addSubview<T: UIView>(_ view: T, setup: (T)->Void) {
        setup(view)
        self.addSubview(view)
    }
}

public extension UIScrollView {
    func addVerticalSubview<T: UIView>(_ view: T, setup: (T)->Void) {
        setup(view)
        self.addSubview(view)
        self.addOnLayoutSubviews { [weak self, weak view] in
            guard let self = self, let view = view else { return }
            view.frame = CGRect(
                x: 0,
                y: 0,
                width: self.bounds.size.width,
                height: view.sizeThatFits(self.bounds.size).height
            )
        }
    }
    func addHorizontalSubview<T: UIView>(_ view: T, setup: (T)->Void) {
        setup(view)
        self.addSubview(view)
        self.addOnLayoutSubviews { [weak self, weak view] in
            guard let self = self, let view = view else { return }
            view.frame = CGRect(
                x: 0,
                y: 0,
                width: view.sizeThatFits(self.bounds.size).width,
                height: self.bounds.size.height
            )
        }
    }
}

public extension LinearLayout {
    func addSubview<T: UIView>(
        _ view: T,
        minimumSize: CGSize = .zero,
        size: CGSize = .zero,
        margin: UIEdgeInsets = .zero,
        gravity: AlignPair = .center,
        weight: CGFloat = 0,
        setup: (T)->Void
    ) {
        setup(view)
        self.addSubview(view, minimumSize: minimumSize, size: size, margin: margin, gravity: gravity, weight: weight)
    }
}


public extension FrameLayout {
    func addSubview<T: UIView>(
        _ view: T,
        minimumSize: CGSize = .zero,
        size: CGSize = .zero,
        margin: UIEdgeInsets = .zero,
        gravity: AlignPair = .center,
        setup: (T)->Void
    ) {
        setup(view)
        self.addSubview(view, minimumSize: minimumSize, size: size, margin: margin, gravity: gravity)
    }
}
