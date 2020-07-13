//
//  CAStateLayer.swift
//  Khrysalis Template
//
//  Created by Joseph Ivie on 9/26/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit
import RxSwift

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
            //TODO: Center this properly
            self.setAffineTransform(CGAffineTransform(
                scaleX: bounds.size.width / baseSize.width,
                y: bounds.size.height / baseSize.height
            ))
        } else {
            self.frame = bounds
        }
        CALayer.onResize.get(self)?.invokeAll(self.bounds)
    }
    
    private static let matchingExtension = ExtensionProperty<CALayer, Disposable>()
    public func matchSize(_ view: UIView?) {
        if let previous = CALayer.matchingExtension.get(self) {
            previous.dispose()
        }
        if let view = view {
            let close = view.onLayoutSubviews.startWith(view).addWeak(referenceA: self) { this, view in
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
    
    public var maxCornerRadius: CGFloat {
        get {
            return cornerRadius
        }
        set(value) {
            self.onResize.startWith(self.bounds).addWeak(referenceA: self) { (self, bounds) in
                self.cornerRadius = min(min(value, bounds.size.width/2), bounds.size.height/2)
            }
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

public extension CALayer {
    @objc func guessBackingColor() -> CGColor? {
        if let bg = self.backgroundColor, bg.alpha > 0.9 {
            return bg
        }
        let color = colorOfPoint(point: CGPoint(x: bounds.minX, y: bounds.minY))
        if color.alpha >= 0.9 {
            return color
        }
        return nil
    }
    private func colorOfPoint(point:CGPoint) -> CGColor {

        var pixel: [CUnsignedChar] = [0, 0, 0, 0]

        let colorSpace = CGColorSpaceCreateDeviceRGB()
        let bitmapInfo = CGBitmapInfo(rawValue: CGImageAlphaInfo.premultipliedLast.rawValue)

        let context = CGContext(data: &pixel, width: 1, height: 1, bitsPerComponent: 8, bytesPerRow: 4, space: colorSpace, bitmapInfo: bitmapInfo.rawValue)

        context!.translateBy(x: -point.x, y: -point.y)

        self.render(in: context!)

        let red: CGFloat   = CGFloat(pixel[0]) / 255.0
        let green: CGFloat = CGFloat(pixel[1]) / 255.0
        let blue: CGFloat  = CGFloat(pixel[2]) / 255.0
        let alpha: CGFloat = CGFloat(pixel[3]) / 255.0

        let color = UIColor(red:red, green: green, blue:blue, alpha:alpha)

        return color.cgColor
    }
}

public extension CAGradientLayer {
    func setGradientAngle(degrees: CGFloat){
        let radius: CGFloat = 0.5
        let radians = degrees * CGFloat.pi / 180
        print(radians)
        self.startPoint = CGPoint(
            x: 0.5 - cos(radians) * radius,
            y: 0.5 + sin(radians) * radius
        )
        self.endPoint = CGPoint(
            x: 0.5 + cos(radians) * radius,
            y: 0.5 - sin(radians) * radius
        )
    }
}
