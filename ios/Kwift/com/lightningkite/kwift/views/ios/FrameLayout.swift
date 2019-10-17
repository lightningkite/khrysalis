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
        public let minimumSize: CGSize
        public let size: CGSize
        public let margin: UIEdgeInsets
        public let gravity: AlignPair
        
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
    
    override public func layoutSubviews() {
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
            switch params.gravity.horizontal {
            case .start:
                subview.frame.origin.x = params.margin.left + padding.left
                subview.frame.size.width = viewSize.width
            case .center:
                subview.frame.origin.x = (size.width - viewSize.width) / 2 + params.margin.left - params.margin.right
                subview.frame.size.width = viewSize.width
            case .end:
                subview.frame.origin.x = size.width - viewSize.width - params.margin.right - padding.right
                subview.frame.size.width = viewSize.width
            case .fill:
                subview.frame.origin.x = params.margin.left
                subview.frame.size.width = size.width - params.margin.total(.x) - padding.total(.x)
            }
            switch params.gravity.vertical {
            case .start:
                subview.frame.origin.y = params.margin.top + padding.top
                subview.frame.size.height = viewSize.height
            case .center:
                subview.frame.origin.y = (size.height - viewSize.height) / 2 + params.margin.top - params.margin.bottom
                subview.frame.size.height = viewSize.height
            case .end:
                subview.frame.origin.y = size.height - viewSize.height - params.margin.bottom - padding.bottom
                subview.frame.size.height = viewSize.height
            case .fill:
                subview.frame.origin.y = params.margin.top
                subview.frame.size.height = size.height - params.margin.total(.y) - padding.total(.y)
            }
        }
    }
}
