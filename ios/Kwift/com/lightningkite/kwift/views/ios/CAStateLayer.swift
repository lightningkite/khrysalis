//
//  CAStateLayer.swift
//  Kwift Template
//
//  Created by Joseph Ivie on 9/26/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit

public protocol CALayerToImage {
    func toImage() -> UIImage?
}

extension CALayer : CALayerToImage {
    public func addOnStateChange(_ view: UIView?, action: @escaping (UIControl.State) -> Void) {
        if let view = view as? UIControl {
            let _ = view.addOnStateChange(retainer: self, id: 0, action: { [weak self, weak view] state in
                guard let self = self else { return }
                action(state)
                view?.setNeedsDisplay()
            })
            UIControl.checkOnStateChange(retainer: self, id: 0)
            action(view.state)
            UIControl.checkOnStateChange(retainer: self, id: 0)
        } else {
            action(UIControl.State.normal)
        }
    }
    
    private static let scaleOverResize = ExtensionProperty<CALayer, Bool>()
    public var scaleOverResize: Bool {
        get {
            return CALayer.scaleOverResize.get(self) ?? false
        }
        set(value) {
            CALayer.scaleOverResize.set(self, value)
        }
    }
    
    private static let baseSize = ExtensionProperty<CALayer, CGSize>()
    private static let onResize = ExtensionProperty<CALayer, StandardEvent<CGRect>>()
    public var onResize: StandardEvent<CGRect> {
        get {
            if let existing = CALayer.onResize.get(self) {
                return existing
            } else {
                let new = StandardEvent<CGRect>()
                CALayer.onResize.set(self, new)
                return new
            }
        }
    }
    public func resize(_ bounds: CGRect) {
        var baseSize: CGSize = .zero
        if let stored = CALayer.baseSize.get(self) {
            baseSize = stored
        } else {
            baseSize = self.bounds.size
            CALayer.baseSize.set(self, self.bounds.size)
        }

        if scaleOverResize {
            self.frame = bounds
            self.setAffineTransform(CGAffineTransform(
                scaleX: bounds.size.width / baseSize.width,
                y: bounds.size.height / baseSize.height
            ))
        } else {
            self.frame = bounds
        }
        CALayer.onResize.get(self)?.invokeAll(self.bounds)
    }
    
    private static let matchingExtension = ExtensionProperty<CALayer, Close>()
    public func matchSize(_ view: UIView?) {
        if let previous = CALayer.matchingExtension.get(self) {
            previous.close()
        }
        if let view = view {
            let close = view.onLayoutSubviews.addAndRunWeak(self, view) { this, view in
                this.resize(view.bounds)
            }
            CALayer.matchingExtension.set(self, close)
        } else {
            CALayer.matchingExtension.set(self, nil)
        }
    }

    @objc public func toImage() -> UIImage? {
        if CFGetTypeID(self.contents as CFTypeRef) == CGImage.typeID {
            return UIImage(cgImage: self.contents as! CGImage)
        } else {
            setNeedsDisplay()
            UIGraphicsBeginImageContextWithOptions(self.bounds.size, self.isOpaque, 0.0)
            guard let ctx = UIGraphicsGetCurrentContext() else {
                print("WARNING!  NO CURRENT CONTEXT!")
                return nil
            }
            self.render(in: ctx)
            let img = UIGraphicsGetImageFromCurrentImageContext()
            UIGraphicsEndImageContext()
            return img
        }
    }
}

public class CAImageLayer: CALayer {
    public var image: UIImage? = nil {
        didSet {
            self.contents = image?.cgImage
            self.bounds.size = image?.size ?? .zero
        }
    }
    @objc override public func toImage() -> UIImage? {
        return image
    }
    public convenience init(_ image: UIImage?) {
        self.init()
        self.image = image
        self.contents = image?.cgImage
        self.bounds.size = image?.size ?? .zero
    }
}
