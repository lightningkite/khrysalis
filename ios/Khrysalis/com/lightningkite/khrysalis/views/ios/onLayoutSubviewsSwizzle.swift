//
//  onLayoutSubviewsSwizzle.swift
//  KhrysalisTemplate
//
//  Created by Joseph Ivie on 8/6/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit

extension UIView {

    private static var extOngoing = ExtensionProperty<UIView, Bool>()
    private static var ext = ExtensionProperty<UIView, StandardEvent<UIView>>()
    var onLayoutSubviewsOngoing: Bool {
        get {
            return UIView.extOngoing.get(self) ?? false
        }
        set(value) {
            UIView.extOngoing.set(self, value)
        }
    }
    var onLayoutSubviews: StandardEvent<UIView> {
        get {
            if let current = UIView.ext.get(self) {
                return current
            }
            let new = StandardEvent<UIView>()
            new.add { view in
                return false
            }
            UIView.ext.set(self, new)
            return new
        }
        set(value) {
            UIView.ext.set(self, value)
        }
    }

    public func addOnLayoutSubviews(action:@escaping ()->Void) {
        action()
        let _ = onLayoutSubviews.add(listener: { [weak self] view in
            action()
            return false
        })
    }



    private static let theSwizzler: Void = {
        let instance = UIView(frame: .zero)
        let aClass: AnyClass! = object_getClass(instance)
        let originalMethod = class_getInstanceMethod(aClass, #selector(layoutSubviews))
        let swizzledMethod = class_getInstanceMethod(aClass, #selector(swizzled_layoutSubviews))
        if let originalMethod = originalMethod, let swizzledMethod = swizzledMethod {
            // switch implementation..
            method_exchangeImplementations(originalMethod, swizzledMethod)
        }
    }()
    public static func useLayoutSubviewsLambda() {
        _ = theSwizzler
        _ = UIButton.theSwizzler
    }
    @objc func swizzled_layoutSubviews(){
        self.swizzled_layoutSubviews()
        if !onLayoutSubviewsOngoing {
            onLayoutSubviewsOngoing = true
            self.onLayoutSubviews.invokeAll(self)
            onLayoutSubviewsOngoing = false
        }
    }
}

extension UIButton {
    fileprivate static let theSwizzler: Void = {
        let instance = UIButton(frame: .zero)
        let aClass: AnyClass! = object_getClass(instance)
        let originalMethod = class_getInstanceMethod(aClass, #selector(layoutSubviews))
        let swizzledMethod = class_getInstanceMethod(aClass, #selector(swizzled_layoutSubviewsButton))
        if let originalMethod = originalMethod, let swizzledMethod = swizzledMethod {
            // switch implementation..
            method_exchangeImplementations(originalMethod, swizzledMethod)
        }
    }()
    @objc func swizzled_layoutSubviewsButton(){
        self.swizzled_layoutSubviewsButton()
        if !onLayoutSubviewsOngoing {
            onLayoutSubviewsOngoing = true
            self.onLayoutSubviews.invokeAll(self)
            onLayoutSubviewsOngoing = false
        }
    }
}
