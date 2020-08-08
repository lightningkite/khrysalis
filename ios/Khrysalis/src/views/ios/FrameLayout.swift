//
//  FrameLayout.swift
//  Khrysalis
//
//  Created by Joseph Ivie on 10/14/19.
//  Copyright © 2019 Lightning Kite. All rights reserved.
//

import UIKit


open class FrameLayout: UIView {
    public var padding: UIEdgeInsets = .zero {
        didSet {
            self.setNeedsLayout()
        }
    }
    
    public struct LayoutParams {
        public var minimumSize: CGSize
        public var size: CGSize
        public var margin: UIEdgeInsets
        public var padding: UIEdgeInsets
        public var gravity: AlignPair
        
        public init(
            minimumSize: CGSize = .zero,
            size: CGSize = .zero,
            margin: UIEdgeInsets = .zero,
            padding: UIEdgeInsets = .zero,
            gravity: AlignPair = .center
        ) {
            self.minimumSize = minimumSize
            self.size = size
            self.margin = margin
            self.padding = padding
            self.gravity = gravity
        }
        
        public var combined: UIEdgeInsets {
            return UIEdgeInsets(
                top: margin.top + padding.top,
                left: margin.left + padding.left,
                bottom: margin.bottom + padding.bottom,
                right: margin.right + padding.right
            )
        }
    }
    
    internal var subviewsWithParams: Dictionary<UIView, LayoutParams> = Dictionary()
    internal var measurements: Dictionary<UIView, CGSize> = Dictionary()
    internal var childBounds: Dictionary<UIView, CGRect> = Dictionary()
    
    public func params(for view: UIView) -> LayoutParams? {
        return subviewsWithParams[view]
    }
    public func params(for view: UIView, setTo: LayoutParams) {
        subviewsWithParams[view] = setTo
        self.setNeedsLayout()
    }
    
    public func addView(_ view: UIView, _ params: LayoutParams) {
        addSubview(view)
        subviewsWithParams[view] = params
    }
    
    public func removeAllViews() {
        self.subviews.forEach { $0.removeFromSuperview() }
    }
    
    public func addSubview(_ view: UIView, _ params: LayoutParams) {
        addSubview(view)
        subviewsWithParams[view] = params
    }
    
    public func addSubview(
        _ view: UIView,
        minimumSize: CGSize = .zero,
        size: CGSize = .zero,
        margin: UIEdgeInsets = .zero,
        padding: UIEdgeInsets = .zero,
        gravity: AlignPair = .center
    ) {
        addSubview(view)
        subviewsWithParams[view] = LayoutParams(minimumSize: minimumSize, size: size, margin: margin, padding: padding, gravity: gravity)
    }
    
    public override func willRemoveSubview(_ subview: UIView) {
        subviewsWithParams.removeValue(forKey: subview)
        measurements.removeValue(forKey: subview)
        childBounds.removeValue(forKey: subview)
        post {
            subview.refreshLifecycle()
        }
    }
    
    public override func didAddSubview(_ subview: UIView) {
        subview.refreshLifecycle()
    }
    
    override public func sizeThatFits(_ size: CGSize) -> CGSize {
        var output = CGSize.zero
        for subview in subviews {
            guard subview.includeInLayout, let params = subviewsWithParams[subview] else { continue }
            let combined = params.combined
            let paddedSize = CGSize(
                width: size.width - padding.total(.x) - combined.total(.x),
                height: size.height - padding.total(.y) - combined.total(.y)
            )
            let viewMeasured = subview.sizeThatFits(paddedSize)
            let viewSize = CGSize(
                width: max(
                    params.minimumSize.width,
                    params.size.width == 0 ? viewMeasured.width : params.size.width
                ),
                height: max(
                    params.minimumSize.height,
                    params.size.height == 0 ? viewMeasured.height : params.size.height
                )
            )
            measurements[subview] = viewSize
            output.width = max(output.width, viewSize.width + padding.total(.x) + combined.total(.x))
            output.height = max(output.height, viewSize.height + padding.total(.y) + combined.total(.y))
        }
        return output
    }
    
    override open func setNeedsLayout() {
        super.setNeedsLayout()
        self.notifyParentSizeChanged()
    }
    
    override public func layoutSubviews() {
        super.layoutSubviews()
        
        let size = self.bounds.size
        for subview in subviews {
            guard subview.includeInLayout, let params = subviewsWithParams[subview] else { continue }
            let combined = params.combined
            let paddedSize = CGSize(
                width: size.width - padding.total(.x) - combined.total(.x),
                height: size.height - padding.total(.y) - combined.total(.y)
            )
            let viewMeasured = subview.sizeThatFits(paddedSize)
            let viewSize = CGSize(
                width: max(
                    params.minimumSize.width,
                    params.size.width == 0 ? viewMeasured.width : params.size.width
                ),
                height: max(
                    params.minimumSize.height,
                    params.size.height == 0 ? viewMeasured.height : params.size.height
                )
            )
            var clickBounds = CGRect.zero
            func handleDimension(dimen: Dimension) {
                switch params.gravity[dimen] {
                    case .start:
                        subview.frame.origin[dimen] = combined.start(dimen) + padding.start(dimen)
                        subview.frame.size[dimen] = viewSize[dimen]
                        clickBounds.origin[dimen] = subview.frame.origin[dimen] - params.padding.start(dimen)
                        clickBounds.size[dimen] = subview.frame.size[dimen] + params.padding.total(dimen)
                    case .center:
                        subview.frame.origin[dimen] = (size[dimen] - viewSize[dimen]) / 2 + combined.start(dimen) - combined.end(dimen)
                        subview.frame.size[dimen] = viewSize[dimen]
                        clickBounds.origin[dimen] = subview.frame.origin[dimen] - params.padding.start(dimen)
                        clickBounds.size[dimen] = subview.frame.size[dimen] + params.padding.total(dimen)
                    case .end:
                        subview.frame.origin[dimen] = size[dimen] - viewSize[dimen] - combined.end(dimen) - padding.end(dimen)
                        subview.frame.size[dimen] = viewSize[dimen]
                        clickBounds.origin[dimen] = subview.frame.origin[dimen] - params.padding.start(dimen)
                        clickBounds.size[dimen] = subview.frame.size[dimen] + params.padding.total(dimen)
                    case .fill:
                        subview.frame.origin[dimen] = combined.start(dimen) + padding.start(dimen)
                        subview.frame.size[dimen] = size[dimen] - combined.total(dimen) - padding.total(dimen)
                        clickBounds.origin[dimen] = params.margin.start(dimen) + padding.start(dimen)
                        clickBounds.size[dimen] = size[dimen] - params.margin.total(dimen) + padding.total(dimen)
                }
            }
            handleDimension(dimen: .x)
            handleDimension(dimen: .y)
            childBounds[subview] = clickBounds
        }
    }
    weak var lastHit: View?
    var lastPoint: CGPoint?
    override open func hitTest(_ point: CGPoint, with event: UIEvent?) -> UIView? {
        lastPoint = point
        for key in subviews.reversed() {
            guard !key.isHidden, key.alpha > 0.1, key.includeInLayout else { continue }
            guard let value = childBounds[key] else { continue }
            if value.contains(point) {
                lastHit = key
                setNeedsDisplay()
                if let sub = key.hitTest(key.convert(point, from: self), with: event) {
                    return sub
                } else {
                    return key
                }
            }
        }
        return nil
    }
    var debugDraw = LayoutSettings.debugDraw
//    override open func draw(_ rect: CGRect) {
//        super.draw(rect)
//        if debugDraw {
//            let ctx = UIGraphicsGetCurrentContext()
//            ctx?.saveGState()
//            ctx?.clear(rect)
//            for (key, value) in childBounds {
//                ctx?.setLineWidth(2)
//                ctx?.setFillColor(UIColor.clear.cgColor)
//                if key === lastHit {
//                    ctx?.setStrokeColor(UIColor.green.cgColor)
//                } else {
//                    ctx?.setStrokeColor(UIColor.blue.cgColor)
//                }
//                UIBezierPath(rect: value.insetBy(dx: 1, dy: 1)).stroke()
//            }
//            if let lastPoint = lastPoint {
//                ctx?.setLineWidth(2)
//                ctx?.setFillColor(UIColor.clear.cgColor)
//                ctx?.setStrokeColor(UIColor.red.cgColor)
//                UIBezierPath(ovalIn: CGRect(x: lastPoint.x, y: lastPoint.y, width: 1, height: 1)).stroke()
//            }
//            ctx?.restoreGState()
//        }
//    }
}
