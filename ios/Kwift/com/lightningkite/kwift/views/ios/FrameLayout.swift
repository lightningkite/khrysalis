//
//  FrameLayout.swift
//  Kwift
//
//  Created by Joseph Ivie on 10/14/19.
//  Copyright © 2019 Lightning Kite. All rights reserved.
//

import UIKit


open class FrameLayout: UIView {
    public var padding: UIEdgeInsets = .zero
    
    public struct LayoutParams {
        public var minimumSize: CGSize
        public var size: CGSize
        public var margin: UIEdgeInsets
        public var gravity: AlignPair
        
        public init(
            minimumSize: CGSize = .zero,
            size: CGSize = .zero,
            margin: UIEdgeInsets = .zero,
            gravity: AlignPair = .center
        ) {
            self.minimumSize = minimumSize
            self.size = size
            self.margin = margin
            self.gravity = gravity
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
        gravity: AlignPair = .center
    ) {
        addSubview(view)
        subviewsWithParams[view] = LayoutParams(minimumSize: minimumSize, size: size, margin: margin, gravity: gravity)
    }
    
    public override func willRemoveSubview(_ subview: UIView) {
        subviewsWithParams.removeValue(forKey: subview)
        measurements.removeValue(forKey: subview)
        childBounds.removeValue(forKey: subview)
    }
    
    override public func sizeThatFits(_ size: CGSize) -> CGSize {
        var output = CGSize.zero
        for subview in subviews {
            guard subview.includeInLayout, let params = subviewsWithParams[subview] else { continue }
            let viewMeasured = subview.sizeThatFits(size)
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
            output.width = max(output.width, viewSize.width + padding.total(.x) + params.margin.total(.x))
            output.height = max(output.height, viewSize.height + padding.total(.y) + params.margin.total(.y))
        }
        return output
    }
    
    override open func setNeedsLayout() {
        super.setNeedsLayout()
        superview?.setNeedsLayout()
    }
    
    override public func layoutSubviews() {
        super.layoutSubviews()
        
        let size = self.bounds.size
        for subview in subviews {
            guard subview.includeInLayout, let params = subviewsWithParams[subview] else { continue }
            let viewMeasured = subview.sizeThatFits(size)
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
            switch params.gravity.horizontal {
            case .start:
                subview.frame.origin.x = params.margin.left + padding.left
                subview.frame.size.width = viewSize.width
                clickBounds.origin.x = 0
                clickBounds.size.width = viewSize.width + params.margin.left + params.margin.right + padding.left
            case .center:
                subview.frame.origin.x = (size.width - viewSize.width) / 2 + params.margin.left - params.margin.right
                subview.frame.size.width = viewSize.width
                clickBounds.origin.x = (size.width - viewSize.width) / 2 - params.margin.left
                clickBounds.size.width = viewSize.width + params.margin.left + params.margin.right
            case .end:
                subview.frame.origin.x = size.width - viewSize.width - params.margin.right - padding.right
                subview.frame.size.width = viewSize.width
                clickBounds.origin.x = size.width - viewSize.width - params.margin.left - params.margin.right - padding.right
                clickBounds.size.width = viewSize.width + params.margin.left + params.margin.right + padding.right
            case .fill:
                subview.frame.origin.x = params.margin.left + padding.left
                subview.frame.size.width = size.width - params.margin.total(.x) - padding.total(.x)
                clickBounds.origin.x = 0
                clickBounds.size.width = size.width
            }
            switch params.gravity.vertical {
            case .start:
                subview.frame.origin.y = params.margin.top + padding.top
                subview.frame.size.height = viewSize.height
                clickBounds.origin.y = 0
                clickBounds.size.height = viewSize.height + params.margin.top + params.margin.bottom + padding.top
            case .center:
                subview.frame.origin.y = (size.height - viewSize.height) / 2 + params.margin.top - params.margin.bottom
                subview.frame.size.height = viewSize.height
                clickBounds.origin.y = (size.height - viewSize.height) / 2 - params.margin.top
                clickBounds.size.height = viewSize.height + params.margin.top + params.margin.bottom
            case .end:
                subview.frame.origin.y = size.height - viewSize.height - params.margin.bottom - padding.bottom
                subview.frame.size.height = viewSize.height
                clickBounds.origin.y = size.height - viewSize.height - params.margin.top - params.margin.bottom - padding.bottom
                clickBounds.size.height = viewSize.height + params.margin.top + params.margin.bottom + padding.bottom
            case .fill:
                subview.frame.origin.y = params.margin.top + padding.top
                subview.frame.size.height = size.height - params.margin.total(.y) - padding.total(.y)
                clickBounds.origin.y = 0
                clickBounds.size.height = size.height
            }
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
    var debugDraw = false
    override open func draw(_ rect: CGRect) {
        super.draw(rect)
        if debugDraw {
            let ctx = UIGraphicsGetCurrentContext()
            ctx?.saveGState()
            ctx?.clear(rect)
            for (key, value) in childBounds {
                ctx?.setLineWidth(2)
                ctx?.setFillColor(UIColor.clear.cgColor)
                if key === lastHit {
                    ctx?.setStrokeColor(UIColor.green.cgColor)
                } else {
                    ctx?.setStrokeColor(UIColor.blue.cgColor)
                }
                UIBezierPath(rect: value.insetBy(dx: 1, dy: 1)).stroke()
            }
            if let lastPoint = lastPoint {
                ctx?.setLineWidth(2)
                ctx?.setFillColor(UIColor.clear.cgColor)
                ctx?.setStrokeColor(UIColor.red.cgColor)
                UIBezierPath(ovalIn: CGRect(x: lastPoint.x, y: lastPoint.y, width: 1, height: 1)).stroke()
            }
            ctx?.restoreGState()
        }
    }
}
