//
//  FrameLayout.swift
//  Kwift
//
//  Created by Joseph Ivie on 10/14/19.
//  Copyright © 2019 Lightning Kite. All rights reserved.
//

import UIKit


public class FrameLayout: UIView {
    public struct LayoutParams {
        public let size: CGSize
        public let margin: UIEdgeInsets
        public let gravity: AlignPair
        
        public init(
            size: CGSize,
            margin: UIEdgeInsets,
            gravity: AlignPair
        ) {
            self.size = size
            self.margin = margin
            self.gravity = gravity
        }
    }
    
    internal var subviewsWithParams: Dictionary<UIView, LayoutParams> = Dictionary()
    internal var measurements: Dictionary<UIView, CGSize> = Dictionary()
    
    public func addSubview(_ view: UIView, _ params: LayoutParams) {
        addSubview(view)
        subviewsWithParams[view] = params
    }
    
    public override func willRemoveSubview(_ subview: UIView) {
        subviewsWithParams.removeValue(forKey: subview)
    }
    
    override public func sizeThatFits(_ size: CGSize) -> CGSize {
        var output = CGSize.zero
        for subview in subviews {
            guard let params = subviewsWithParams[subview] else { continue }
            let viewMeasured = subview.sizeThatFits(size)
            let viewSize = CGSize(
                width: params.size.width == 0 ? viewMeasured.width : params.size.width,
                height: params.size.height == 0 ? viewMeasured.height : params.size.height
            )
            measurements[subview] = viewSize
            output.width = max(output.width, viewSize.width)
            output.height = max(output.height, viewSize.height)
        }
        return output
    }
    
    override public func layoutSubviews() {
        let size = self.bounds.size
        for subview in subviews {
            guard let params = subviewsWithParams[subview] else { continue }
            let viewMeasured = subview.sizeThatFits(size)
            let viewSize = CGSize(
                width: params.size.width == 0 ? viewMeasured.width : params.size.width,
                height: params.size.height == 0 ? viewMeasured.height : params.size.height
            )
            switch params.gravity.horizontal {
            case .start:
                subview.frame.origin.x = params.margin.left
                subview.frame.size.width = viewSize.width
            case .center:
                subview.frame.origin.x = (size.width - viewSize.width) / 2 - params.margin.left + params.margin.right
                subview.frame.size.width = viewSize.width
            case .end:
                subview.frame.origin.x = size.width - viewSize.width - params.margin.right
                subview.frame.size.width = viewSize.width
            case .fill:
                subview.frame.origin.x = params.margin.left
                subview.frame.size.width = size.width - params.margin.right - params.margin.left
            }
            switch params.gravity.vertical {
            case .start:
                subview.frame.origin.y = params.margin.top
                subview.frame.size.height = viewSize.height
            case .center:
                subview.frame.origin.y = (size.height - viewSize.height) / 2 - params.margin.top + params.margin.bottom
                subview.frame.size.height = viewSize.height
            case .end:
                subview.frame.origin.y = size.height - viewSize.height - params.margin.bottom
                subview.frame.size.height = viewSize.height
            case .fill:
                subview.frame.origin.y = params.margin.top
                subview.frame.size.height = size.height - params.margin.bottom - params.margin.top
            }
        }
    }
}
