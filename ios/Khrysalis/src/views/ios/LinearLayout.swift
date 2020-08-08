//
//  Layouting.swift
//  Khrysalis
//
//  Created by Joseph Ivie on 10/12/19.
//  Copyright © 2019 Lightning Kite. All rights reserved.
//

import UIKit


open class LinearLayout: UIView {
    
    public var padding: UIEdgeInsets = .zero {
        didSet {
            self.setNeedsLayout()
        }
    }
    public var gravity: AlignPair = .topLeft {
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
        public var weight: CGFloat
        
        public init(
            minimumSize: CGSize = .zero,
            size: CGSize = .zero,
            margin: UIEdgeInsets = .zero,
            padding: UIEdgeInsets = .zero,
            gravity: AlignPair = .center,
            weight: CGFloat = 0
        ) {
            self.minimumSize = minimumSize
            self.size = size
            self.margin = margin
            self.padding = padding
            self.gravity = gravity
            self.weight = weight
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
    
    internal var subviewsWithParams: Array<(UIView, LayoutParams)> = Array()
    
    public func params(for view: UIView) -> LayoutParams? {
        return subviewsWithParams.find { entry in entry.0 === view }?.1
    }
    public func params(for view: UIView, setTo: LayoutParams) {
        if let index = (subviewsWithParams.firstIndex { entry in entry.0 === view }) {
            subviewsWithParams[index] = (view, setTo)
            self.setNeedsLayout()
        }
    }
    
    public func addView(_ view: UIView, _ params: LayoutParams) {
        addSubview(view)
        subviewsWithParams.append((view, params))
        setNeedsLayout()
    }
    
    public func removeView(_ view: UIView) {
        view.removeFromSuperview()
    }
    
    public func removeAllViews() {
        self.subviews.forEach { $0.removeFromSuperview() }
    }
    
    public func addSubview(_ view: UIView, _ params: LayoutParams) {
        addSubview(view)
        subviewsWithParams.append((view, params))
        setNeedsLayout()
    }
    public func addSubview(
        _ view: UIView,
        minimumSize: CGSize = .zero,
        size: CGSize = .zero,
        margin: UIEdgeInsets = .zero,
        padding: UIEdgeInsets = .zero,
        gravity: AlignPair = .center,
        weight: CGFloat = 0
    ) {
        addSubview(view)
        let params = LayoutParams(minimumSize: minimumSize, size: size, margin: margin, padding:padding, gravity: gravity, weight: weight)
        subviewsWithParams.append((view, params))
        setNeedsLayout()
    }
    
    public override func willRemoveSubview(_ subview: UIView) {
        subviewsWithParams.removeAll { it in it.0 == subview }
        measurements.removeValue(forKey: subview)
        childBounds.removeValue(forKey: subview)
        setNeedsLayout()
        post {
            subview.refreshLifecycle()
        }
    }
    
    public override func didAddSubview(_ subview: UIView) {
        subview.refreshLifecycle()
    }
    
    override open func setNeedsLayout() {
        super.setNeedsLayout()
        self.notifyParentSizeChanged()
    }
    
    
    internal var measurements: Dictionary<UIView, CGSize> = Dictionary()
    internal var childBounds: Dictionary<UIView, CGRect> = Dictionary()
    
    public var orientation: Dimension = .x
    private func makePoint(primary: CGFloat, secondary: CGFloat) -> CGPoint {
        switch orientation {
        case .x:
            return CGPoint(x: primary, y: secondary)
        case .y:
            return CGPoint(x: secondary, y: primary)
        }
    }
    private func makeSize(primary: CGFloat, secondary: CGFloat) -> CGSize {
        switch orientation {
        case .x:
            return CGSize(width: primary, height: secondary)
        case .y:
            return CGSize(width: secondary, height: primary)
        }
    }
    
    private func measure(_ size: CGSize, includingWeighted: Bool = false) -> CGSize {
        var result = CGSize.zero
        
        result[orientation] += padding.start(orientation)
        var weightTotal: CGFloat = 0
        
        for (subview, params) in subviewsWithParams {
            guard subview.includeInLayout else { continue }
            weightTotal += params.weight
            let combined = params.combined
            if params.weight != 0 {
                result[orientation] += combined.start(orientation)
                result[orientation] += combined.end(orientation)
                continue
            }
            let subMaximumsA = makeSize(
                primary: size[orientation] - result[orientation] - combined.total(orientation),
                secondary: size[orientation.other] - combined.total(orientation.other) - padding.total(orientation.other)
            )
            let subMaximums = CGSize(
                width: max(
                    params.minimumSize.width,
                    params.size.width == 0 ? subMaximumsA.width : params.size.width
                ),
                height: max(
                    params.minimumSize.height,
                    params.size.height == 0 ? subMaximumsA.height : params.size.height
                )
            )
            let viewMeasured = subview.sizeThatFits(subMaximums)
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
            result[orientation] += combined.start(orientation)
            result[orientation] += viewSize[orientation]
            result[orientation] += combined.end(orientation)
            
            result[orientation.other] = max(
                result[orientation.other],
                viewSize[orientation.other] +
                    combined.total(orientation.other) +
                    padding.total(orientation.other)
            )
        }
        
        result[orientation] += padding.end(orientation)
        
        let remainingArea = size[orientation] - result[orientation]
        
        for (subview, params) in subviewsWithParams {
            if params.weight == 0 { continue }
            let combined = params.combined
            let subMaximumsA = makeSize(
                primary: remainingArea * params.weight / weightTotal,
                secondary: size[orientation.other] - combined.total(orientation.other) - padding.total(orientation.other)
            )
            let subMaximums = CGSize(
                width: max(
                    params.minimumSize.width,
                    params.size.width == 0 ? subMaximumsA.width : params.size.width
                ),
                height: max(
                    params.minimumSize.height,
                    params.size.height == 0 ? subMaximumsA.height : params.size.height
                )
            )
            let viewMeasured = subview.sizeThatFits(subMaximums)
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
            result[orientation.other] = max(
                result[orientation.other],
                viewSize[orientation.other] +
                    combined.total(orientation.other) +
                    padding.total(orientation.other)
            )
            if includingWeighted {
                result[orientation] += viewSize[orientation]
            }
        }
        
        return result
    }
    override public func sizeThatFits(_ size: CGSize) -> CGSize {
        return measure(size, includingWeighted: true)
    }
    override public func layoutSubviews() {
        super.layoutSubviews()
        
        var position: CGFloat = 0
        let size = self.bounds.size
        let requiredSize = measure(size, includingWeighted: false)
        let weightSum = subviewsWithParams.filter { $0.0.includeInLayout }.reduce(0) { (acc, pair) in acc + pair.1.weight }
        let remainingPrimarySize = size[orientation] - requiredSize[orientation]
        if weightSum == 0 {
            switch gravity[orientation] {
            case .start:
                position = 0
            case .center:
                position = remainingPrimarySize / 2
            case .end:
                position = remainingPrimarySize
            case .fill:
                position = remainingPrimarySize / 2
            }
        }
        
        position += padding.start(orientation)
        for (subview, params) in subviewsWithParams {
            guard subview.includeInLayout else { continue }
            let combined = params.combined
            position += combined.start(orientation)
            let viewSize = measurements[subview]!
            
            let primarySize: CGFloat
            if params.weight == 0 {
                primarySize = viewSize[orientation]
            } else {
                primarySize = (params.weight / weightSum) * remainingPrimarySize
            }
            
            let shift: CGFloat
            let secondarySize: CGFloat
            let gravityComponent: Align = params.gravity[orientation.other]
            let secondaryCap = size[orientation.other] - combined.total(orientation.other) - padding.total(orientation.other)
            switch gravityComponent {
            case .start:
                shift = combined.start(orientation.other) + padding.start(orientation.other)
                secondarySize = min(viewSize[orientation.other], secondaryCap)
            case .center:
                shift = (size[orientation.other] - viewSize[orientation.other] - padding.total(orientation.other)) / 2 - combined.start(orientation.other) + combined.end(orientation.other) + padding.start(orientation.other)
                secondarySize = min(viewSize[orientation.other], secondaryCap)
            case .end:
                shift = size[orientation.other] - viewSize[orientation.other] - combined.end(orientation.other) - padding.end(orientation.other)
                secondarySize = min(viewSize[orientation.other], secondaryCap)
            case .fill:
                shift = combined.start(orientation.other) + padding.start(orientation.other)
                secondarySize = secondaryCap
            }
            
            subview.frame = CGRect(
                origin: makePoint(primary: position, secondary: shift),
                size: makeSize(primary: primarySize, secondary: secondarySize)
            )
            childBounds[subview] = CGRect(
                origin: makePoint(primary: position - params.padding.start(orientation), secondary: params.padding.start(orientation.other)),
                size: makeSize(primary: primarySize + params.padding.total(orientation), secondary: size[orientation.other] - params.padding.total(orientation.other))
            )
            subview.layoutSubviews()
            
            position += primarySize
            position += combined.end(orientation)
        }
        position += padding.end(orientation)
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
