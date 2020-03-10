//
//  safePadding.swift
//  Alamofire
//
//  Created by Joseph Ivie on 3/9/20.
//

import Foundation
import UIKit

public extension UIView {
    static var fullScreenSafeInsets: UIEdgeInsets = .zero
    func safeInsets(align: AlignPair) {
        var useTop = false
        var useLeft = false
        var useRight = false
        var useBottom = false
        switch align.vertical {
        case .start:
            useTop = true
        case .center, .fill:
            useTop = true
            useBottom = true
        case .end:
            useBottom = true
        }
        switch align.horizontal {
        case .start:
            useLeft = true
        case .center, .fill:
            useLeft = true
            useRight = true
        case .end:
            useRight = true
        }
        var myDefault: UIEdgeInsets? = nil
        self.addOnLayoutSubviews { [weak self] in
            guard let self = self else { return }
            let addedInsets = UIEdgeInsets(
                top: useTop ? UIView.fullScreenSafeInsets.top : 0,
                left: useLeft ? UIView.fullScreenSafeInsets.left : 0,
                bottom: useBottom ? UIView.fullScreenSafeInsets.bottom : 0,
                right: useRight ? UIView.fullScreenSafeInsets.right : 0
            )
            switch self {
            case let self as LinearLayout:
                if myDefault == nil {
                    myDefault = self.padding
                }
                self.padding = myDefault! + addedInsets
                break
            case let self as FrameLayout:
                if myDefault == nil {
                    myDefault = self.padding
                }
                self.padding = myDefault! + addedInsets
                break
            default:
                if let superview = self.superview as? LinearLayout {
                    if var current = superview.params(for: self) {
                        if myDefault == nil {
                            myDefault = current.padding
                        }
                        current.padding = myDefault! + addedInsets
                        superview.params(for: self, setTo: current)
                    }
                } else if let superview = self.superview as? FrameLayout {
                    if var current = superview.params(for: self) {
                        if myDefault == nil {
                            myDefault = current.padding
                        }
                        current.padding = myDefault! + addedInsets
                        superview.params(for: self, setTo: current)
                    }
                }
                break
            }
        }
    }
    func safeInsetsSizing(align: AlignPair) {
        var useTop = false
        var useLeft = false
        var useRight = false
        var useBottom = false
        switch align.vertical {
        case .start:
            useTop = true
        case .center, .fill:
            useTop = true
            useBottom = true
        case .end:
            useBottom = true
        }
        switch align.horizontal {
        case .start:
            useLeft = true
        case .center, .fill:
            useLeft = true
            useRight = true
        case .end:
            useRight = true
        }
        var myDefault: CGSize? = nil
        self.addOnLayoutSubviews { [weak self] in
            guard let self = self else { return }
            let addedSize = CGSize(
                width: (useLeft ? UIView.fullScreenSafeInsets.left : 0) + (useRight ? UIView.fullScreenSafeInsets.right : 0),
                height: (useTop ? UIView.fullScreenSafeInsets.top : 0) + (useBottom ? UIView.fullScreenSafeInsets.bottom : 0)
            )
            if let superview = self.superview as? LinearLayout {
                if var current = superview.params(for: self) {
                    if myDefault == nil {
                        myDefault = current.size
                    }
                    current.size = addIfNotZero(myDefault!, addedSize)
                    superview.params(for: self, setTo: current)
                }
            } else if let superview = self.superview as? FrameLayout {
                if var current = superview.params(for: self) {
                    if myDefault == nil {
                        myDefault = current.size
                    }
                    current.size = addIfNotZero(myDefault!, addedSize)
                    superview.params(for: self, setTo: current)
                }
            }
        }
    }
}

private func +(lhs: UIEdgeInsets, rhs: UIEdgeInsets) -> UIEdgeInsets {
    return UIEdgeInsets(
        top: lhs.top + rhs.top,
        left: lhs.left + rhs.left,
        bottom: lhs.bottom + rhs.bottom,
        right: lhs.right + rhs.right
    )
}


private func addIfNotZero(_ lhs: CGSize, _ rhs: CGSize) -> CGSize {
    return CGSize(
        width: lhs.width != 0 ? lhs.width + rhs.width : 0,
        height: lhs.height != 0 ? lhs.height + rhs.height : 0
    )
}
