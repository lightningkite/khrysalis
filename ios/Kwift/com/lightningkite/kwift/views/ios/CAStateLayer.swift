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
            let _ = view.addOnStateChange(retainer: self, id: 0, action: { [weak view] state in
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
    private static let matchingExtension = ExtensionProperty<CALayer, Close>()
    public func matchSize(_ view: UIView?) {
        if let previous = CALayer.matchingExtension.get(self) {
            previous.close()
        }
        if let view = view {
            let close = view.onLayoutSubviews.addAndRunWeak(self, view) { this, view in
                this.frame = view.bounds
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
        }
    }
    @objc override public func toImage() -> UIImage? {
        return image
    }
    public convenience init(_ image: UIImage?) {
        self.init()
        self.image = image
    }
}
