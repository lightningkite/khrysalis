//
//  safePadding.swift
//  Alamofire
//
//  Created by Joseph Ivie on 3/9/20.
//

import Foundation
import UIKit

public extension UIView {
    static var fullScreenSafeInsetsObs: MutableObservableProperty<UIEdgeInsets> = StandardObservableProperty(underlyingValue: .zero)
    private static let consumesExtension: ExtensionProperty<UIView, (UIEdgeInsets)->UIEdgeInsets> = ExtensionProperty()
    func findMySafeInsets() -> UIEdgeInsets {
        if let superview = superview {
            let fromParent = superview.findMySafeInsets()
            let action = UIView.consumesExtension.get(self) ?? { $0 }
            let subInsets = action(fromParent)
            return subInsets
        } else {
            let action = UIView.consumesExtension.get(self) ?? { $0 }
            let subInsets = action(UIView.fullScreenSafeInsetsObs.value)
            return subInsets
        }
    }
    func findParentSafeInsets() -> UIEdgeInsets {
        if let superview = superview {
            return superview.findMySafeInsets()
        } else {
            return UIView.fullScreenSafeInsetsObs.value
        }
    }
    func updateSafeInsets(_ insets: UIEdgeInsets){
        let action = UIView.consumesExtension.get(self) ?? { $0 }
        let subInsets = action(insets)
        for sub in self.subviews {
            sub.updateSafeInsets(subInsets)
        }
    }
    func safeInsets(align: AlignPair) {
        var useTop = false
        var useLeft = false
        var useRight = false
        var useBottom = false
        switch align.vertical {
        case .start:
            useTop = true
        case .fill:
            useTop = true
            useBottom = true
        case .end:
            useBottom = true
        default:
            break
        }
        switch align.horizontal {
        case .start:
            useLeft = true
        case .fill:
            useLeft = true
            useRight = true
        case .end:
            useRight = true
        default:
            break
        }
        var myDefault: UIEdgeInsets? = nil
        UIView.consumesExtension.set(self, {[weak self] insets in
            guard let self = self else { return insets }
            let addedInsets = UIEdgeInsets(
                top: useTop ? insets.top : 0,
                left: useLeft ? insets.left : 0,
                bottom: useBottom ? insets.bottom : 0,
                right: useRight ? insets.right : 0
            )
            switch self {
            case let self as LinearLayout:
                if myDefault == nil {
                    myDefault = self.padding
                }
                self.padding = myDefault! + addedInsets
            case let self as FrameLayout:
                if myDefault == nil {
                    myDefault = self.padding
                }
                self.padding = myDefault! + addedInsets
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
            }
            return UIEdgeInsets(
                top: useTop ? 0 : insets.top,
                left: useLeft ? 0 : insets.left,
                bottom: useBottom ? 0 : insets.bottom,
                right: useRight ? 0 : insets.right
            )
        })
        post { [weak self] in
            guard let self = self else { return }
            self.updateSafeInsets(self.findParentSafeInsets())
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
        UIView.consumesExtension.set(self, {[weak self] insets in
            guard let self = self else { return insets }
            let addedSize = CGSize(
                width: (useLeft ? insets.left : 0) + (useRight ? insets.right : 0),
                height: (useTop ? insets.top : 0) + (useBottom ? insets.bottom : 0)
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
            return UIEdgeInsets(
                top: useTop ? 0 : insets.top,
                left: useLeft ? 0 : insets.left,
                bottom: useBottom ? 0 : insets.bottom,
                right: useRight ? 0 : insets.right
            )
        })
        post { [weak self] in
            guard let self = self else { return }
            self.updateSafeInsets(self.findParentSafeInsets())
        }
    }
    func safeInsetsBoth(align: AlignPair) {
        safeInsetsSizing(align: align)
        safeInsets(align: align)
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
