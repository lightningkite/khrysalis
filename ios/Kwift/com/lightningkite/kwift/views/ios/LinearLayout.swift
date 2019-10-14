//
//  Layouting.swift
//  Kwift
//
//  Created by Joseph Ivie on 10/12/19.
//  Copyright © 2019 Lightning Kite. All rights reserved.
//

import UIKit


public class LinearLayout: UIView {
    
    public struct LayoutParams {
        public let size: CGSize
        public let margin: UIEdgeInsets
        public let gravity: AlignPair
        public let weight: CGFloat
        
        public init(
            size: CGSize,
            margin: UIEdgeInsets,
            gravity: AlignPair,
            weight: CGFloat
        ) {
            self.size = size
            self.margin = margin
            self.gravity = gravity
            self.weight = weight
        }
    }
    
    internal var subviewsWithParams: Dictionary<UIView, LayoutParams> = Dictionary()
    
    public func addSubview(_ view: UIView, _ params: LayoutParams) {
        addSubview(view)
        subviewsWithParams[view] = params
    }
    
    public override func willRemoveSubview(_ subview: UIView) {
        subviewsWithParams.removeValue(forKey: subview)
    }
    
    
    internal var measurements: Dictionary<UIView, CGSize> = Dictionary()
    
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
        
        for subview in subviews {
            guard let params = subviewsWithParams[subview] else { continue }
            let viewMeasured = subview.sizeThatFits(makeSize(
                primary: size[orientation] - result[orientation] - params.margin.start(orientation) - params.margin.end(orientation),
                secondary: size[orientation.other]
            ))
            let viewSize = CGSize(
                width: params.size.width == 0 ? viewMeasured.width : params.size.width,
                height: params.size.height == 0 ? viewMeasured.height : params.size.height
            )
            measurements[subview] = viewSize
            result[orientation] += params.margin.start(orientation)
            if includingWeighted || params.weight == 0 {
                result[orientation] += viewSize[orientation]
            }
            result[orientation] += params.margin.end(orientation)
            
            result[orientation.other] = max(result[orientation.other], viewSize[orientation.other] + params.margin.start(orientation.other) + params.margin.end(orientation.other))
        }
        
        return result
    }
    override public func sizeThatFits(_ size: CGSize) -> CGSize {
        return measure(size, includingWeighted: true)
    }
    override public func layoutSubviews() {
        var position: CGFloat = 0
        let size = self.bounds.size
        let requiredSize = measure(size, includingWeighted: false)
        let weightSum = subviewsWithParams.values.reduce(0) { (acc, params) in acc + params.weight }
        let remainingPrimarySize = size[orientation] - requiredSize[orientation]
        
        for subview in subviews {
            guard let params = subviewsWithParams[subview] else { continue }
            position += params.margin.start(orientation)
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
            switch gravityComponent {
            case .start:
                shift = params.margin.start(orientation.other)
                secondarySize = viewSize[orientation.other]
            case .center:
                shift = (size[orientation.other] - viewSize[orientation.other]) / 2 - params.margin.start(orientation.other) + params.margin.end(orientation.other)
                secondarySize = viewSize[orientation.other]
            case .end:
                shift = size[orientation.other] - viewSize[orientation.other] - params.margin.end(orientation.other)
                secondarySize = viewSize[orientation.other]
            case .fill:
                shift = params.margin.start(orientation.other)
                secondarySize = size[orientation.other] - params.margin.total(orientation.other)
            }
            
            subview.frame = CGRect(
                origin: makePoint(primary: position, secondary: shift),
                size: makeSize(primary: primarySize, secondary: secondarySize)
            )
            subview.layoutSubviews()
            
            position += primarySize
            position += params.margin.end(orientation)
        }
    }
}
