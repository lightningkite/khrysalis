//
//  safePadding.swift
//  Alamofire
//
//  Created by Joseph Ivie on 3/9/20.
//

import Foundation
import UIKit

public extension UIView {
    static let fullScreenSafeInsetsObs: StandardObservableProperty<UIEdgeInsets> = StandardObservableProperty(.zero)
    func safeInsets(align: AlignPair) {
        post {
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
            UIView.fullScreenSafeInsetsObs.addAndRunWeak(self) { (self, fullScreenSafeInsets) in
                print("Activate safe insets: \(fullScreenSafeInsets) useTop: \(useTop)")
                let addedInsets = UIEdgeInsets(
                    top: useTop ? fullScreenSafeInsets.top : 0,
                    left: useLeft ? fullScreenSafeInsets.left : 0,
                    bottom: useBottom ? fullScreenSafeInsets.bottom : 0,
                    right: useRight ? fullScreenSafeInsets.right : 0
                )
                switch self {
                case let self as LinearLayout:
                    if myDefault == nil {
                        myDefault = self.padding
                    }
                    self.padding = myDefault! + addedInsets
                    print("Activate safe insets via self.padding (LL)")
                case let self as FrameLayout:
                    if myDefault == nil {
                        myDefault = self.padding
                    }
                    self.padding = myDefault! + addedInsets
                    print("Activate safe insets via self.padding (FL)")
                default:
                    if let superview = self.superview as? LinearLayout {
                        if var current = superview.params(for: self) {
                            if myDefault == nil {
                                myDefault = current.padding
                            }
                            current.padding = myDefault! + addedInsets
                            superview.params(for: self, setTo: current)
                            print("Activate safe insets via superview.padding (LL)")
                        }
                    } else if let superview = self.superview as? FrameLayout {
                        if var current = superview.params(for: self) {
                            if myDefault == nil {
                                myDefault = current.padding
                            }
                            current.padding = myDefault! + addedInsets
                            superview.params(for: self, setTo: current)
                            print("Activate safe insets via superview.padding (FL)")
                        }
                    }
                }
            }
        }
    }
    func safeInsetsSizing(align: AlignPair) {
        post {
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
            UIView.fullScreenSafeInsetsObs.addAndRunWeak(self) { (self, fullScreenSafeInsets) in
                let addedSize = CGSize(
                    width: (useLeft ? fullScreenSafeInsets.left : 0) + (useRight ? fullScreenSafeInsets.right : 0),
                    height: (useTop ? fullScreenSafeInsets.top : 0) + (useBottom ? fullScreenSafeInsets.bottom : 0)
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
