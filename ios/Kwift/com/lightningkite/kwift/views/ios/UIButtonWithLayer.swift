//
//  UIButtonWithLayer.swift
//  Kwift
//
//  Created by Joseph Ivie on 10/16/19.
//  Copyright © 2019 Lightning Kite. All rights reserved.
//

import Foundation
import UIKit

open class UIButtonWithLayer: UIButton {
    
    public enum Position { case left, top, right, bottom, center }
    
    public var iconLayer: CALayer? {
        willSet {
            iconLayer?.removeFromSuperlayer()
        }
        didSet {
            if let iconLayer = iconLayer {
                iconLayerRatio = iconLayer.bounds.size.width / iconLayer.bounds.size.height
                layer.addSublayer(iconLayer)
            }
            setNeedsLayout()
        }
    }
    private var iconLayerRatio: CGFloat = 1
    public var iconPosition: Position = .center{
        didSet {
            setNeedsLayout()
        }
    }
    public var iconPadding: CGFloat = 8 {
        didSet {
            setNeedsLayout()
        }
    }
    public var textGravity: AlignPair = .center{
        didSet {
            setNeedsLayout()
        }
    }
    
    override open func sizeThatFits(_ size: CGSize) -> CGSize {
        super.sizeThatFits(size)
        var result = CGSize.zero
        let isHorizontal = iconPosition == .left || iconPosition == .right
        let isVertical = iconPosition == .top || iconPosition == .bottom
        if let iconLayer = iconLayer {
            if isHorizontal {
                result.width += iconLayer.frame.size.width + iconPadding
            } else {
                result.width = max(result.width, iconLayer.frame.size.width)
            }
            if isVertical {
                result.height += iconLayer.frame.size.height + iconPadding
            } else {
                result.height = max(result.height, iconLayer.frame.size.height)
            }
        }
        if let labelSize = titleLabel?.sizeThatFits(size) {
            if isHorizontal {
                result.width += labelSize.width
            } else {
                result.width = max(result.width, labelSize.width)
            }
            if isVertical {
                result.height += labelSize.height
            } else {
                result.height = max(result.height, labelSize.height)
            }
        }
        result.width += contentEdgeInsets.left + contentEdgeInsets.right
        result.height += contentEdgeInsets.top + contentEdgeInsets.bottom
        return result
    }
    
    override open func layoutSubviews() {
        super.layoutSubviews()
        var placeableRect = self.bounds.inset(by: self.contentEdgeInsets)
        if let iconLayer = iconLayer {
            switch iconPosition {
            case .center:
                iconLayer.resize(CGRect(
                    x: placeableRect.origin.x + (placeableRect.size.width - iconLayer.bounds.size.width) / 2,
                    y: placeableRect.origin.y + (placeableRect.size.height - iconLayer.bounds.size.height) / 2,
                    width: iconLayer.bounds.size.width,
                    height: iconLayer.bounds.size.height
                ))
            case .left:
                iconLayer.resize(CGRect(
                    x: placeableRect.origin.x,
                    y: placeableRect.origin.y + (placeableRect.size.height - iconLayer.bounds.size.height) / 2,
                    width: iconLayer.bounds.size.width,
                    height: iconLayer.bounds.size.height
                ))
                placeableRect.origin.x += iconLayer.bounds.size.width + iconPadding
                placeableRect.size.width -= iconLayer.bounds.size.width + iconPadding
            case .right:
                iconLayer.resize(CGRect(
                    x: placeableRect.origin.x + (placeableRect.size.width - iconLayer.bounds.size.width),
                    y: placeableRect.origin.y + (placeableRect.size.height - iconLayer.bounds.size.height) / 2,
                    width: iconLayer.bounds.size.width,
                    height: iconLayer.bounds.size.height
                ))
                placeableRect.size.width -= iconLayer.bounds.size.width + iconPadding
            case .top:
                iconLayer.resize(CGRect(
                    x: placeableRect.origin.x + (placeableRect.size.width - iconLayer.bounds.size.width) / 2,
                    y: placeableRect.origin.y,
                    width: iconLayer.bounds.size.width,
                    height: iconLayer.bounds.size.height
                ))
                placeableRect.origin.y += iconLayer.bounds.size.height + iconPadding
                placeableRect.size.height -= iconLayer.bounds.size.height + iconPadding
            case .bottom:
                iconLayer.resize(CGRect(
                    x: placeableRect.origin.x + (placeableRect.size.width - iconLayer.bounds.size.width) / 2,
                    y: placeableRect.origin.y + (placeableRect.size.height - iconLayer.bounds.size.height),
                    width: iconLayer.bounds.size.width,
                    height: iconLayer.bounds.size.height
                ))
                placeableRect.size.height -= iconLayer.bounds.size.height + iconPadding
            }
        }
        if let titleLabel = titleLabel {
            var destination = CGRect.zero
            destination.size = titleLabel.bounds.size
            switch textGravity.horizontal {
            case .start:
                destination.origin.x = placeableRect.origin.x
            case .center:
                destination.origin.x = placeableRect.origin.x + (placeableRect.size.width - destination.size.width) / 2
            case .end:
                destination.origin.x = placeableRect.origin.x + placeableRect.size.width - destination.size.width
            case .fill:
                destination.origin.x = placeableRect.origin.x + (placeableRect.size.width - destination.size.width) / 2
            }
            switch textGravity.vertical {
            case .start:
                destination.origin.y = placeableRect.origin.y
            case .center:
                destination.origin.y = placeableRect.origin.y + (placeableRect.size.height - destination.size.height) / 2
            case .end:
                destination.origin.y = placeableRect.origin.y + placeableRect.size.height - destination.size.height
            case .fill:
                destination.origin.y = placeableRect.origin.y + (placeableRect.size.height - destination.size.height) / 2
            }
            titleLabel.frame = destination
        }
    }
}

