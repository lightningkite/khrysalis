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
        }
    }
    private var iconLayerRatio: CGFloat = 1
    public var iconPosition: Position = .center
    public var iconPadding: CGFloat = 8
    public var textGravity: AlignPair = .center
    
    override open func sizeThatFits(_ size: CGSize) -> CGSize {
        super.sizeThatFits(size)
        var result = CGSize.zero
        let isHorizontal = iconPosition == .left || iconPosition == .right
        let isVertical = iconPosition == .top || iconPosition == .bottom
        if let iconLayer = iconLayer {
            if isHorizontal {
                result.width += iconLayer.frame.size.width
            } else {
                result.width = max(result.width, iconLayer.frame.size.width)
            }
            if isVertical {
                result.height += iconLayer.frame.size.height
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
                iconLayer.frame = CGRect(
                    x: placeableRect.origin.x + (placeableRect.size.width - iconLayer.bounds.size.width) / 2,
                    y: placeableRect.origin.y + (placeableRect.size.height - iconLayer.bounds.size.height) / 2,
                    width: iconLayer.bounds.size.width,
                    height: iconLayer.bounds.size.height
                )
            case .left:
                iconLayer.frame = CGRect(
                    x: placeableRect.origin.x,
                    y: placeableRect.origin.y + (placeableRect.size.height - iconLayer.bounds.size.height) / 2,
                    width: iconLayer.bounds.size.width,
                    height: iconLayer.bounds.size.height
                )
                placeableRect.origin.x += iconLayer.bounds.size.width + iconPadding
                placeableRect.size.width -= iconLayer.bounds.size.width + iconPadding
            case .right:
                iconLayer.frame = CGRect(
                    x: placeableRect.origin.x + (placeableRect.size.width - iconLayer.bounds.size.width),
                    y: placeableRect.origin.y + (placeableRect.size.height - iconLayer.bounds.size.height) / 2,
                    width: iconLayer.bounds.size.width,
                    height: iconLayer.bounds.size.height
                )
                placeableRect.size.width -= iconLayer.bounds.size.width + iconPadding
            case .top:
                iconLayer.frame = CGRect(
                    x: placeableRect.origin.x + (placeableRect.size.width - iconLayer.bounds.size.width) / 2,
                    y: placeableRect.origin.y,
                    width: iconLayer.bounds.size.width,
                    height: iconLayer.bounds.size.height
                )
                placeableRect.origin.y += iconLayer.bounds.size.height + iconPadding
                placeableRect.size.height -= iconLayer.bounds.size.height + iconPadding
            case .bottom:
                iconLayer.frame = CGRect(
                    x: placeableRect.origin.x + (placeableRect.size.width - iconLayer.bounds.size.width) / 2,
                    y: placeableRect.origin.y + (placeableRect.size.height - iconLayer.bounds.size.height),
                    width: iconLayer.bounds.size.width,
                    height: iconLayer.bounds.size.height
                )
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
//        var placeableRect = self.bounds.inset(by: self.contentEdgeInsets)
//        if let titleLabel = titleLabel {
//            let titleSize = titleLabel.sizeThatFits(placeableRect.size)
//            switch iconPosition {
//            case .bottom:
//                titleLabel.frame = CGRect(
//                    x: placeableRect.origin.x + (placeableRect.size.width - titleSize.width) / 2,
//                    y: placeableRect.origin.y,
//                    width: titleSize.width,
//                    height: titleSize.height
//                )
//                placeableRect.origin.y += titleSize.height + iconPadding
//                placeableRect.size.height -= titleSize.height - iconPadding
//            case .top:
//                titleLabel.frame = CGRect(
//                    x: placeableRect.origin.x + (placeableRect.size.width - titleSize.width) / 2,
//                    y: placeableRect.origin.y + placeableRect.size.height - titleSize.height,
//                    width: titleSize.width,
//                    height: titleSize.height
//                )
//                placeableRect.size.height -= titleSize.height - iconPadding
//            case .left:
//                titleLabel.frame = CGRect(
//                    x: placeableRect.origin.x + placeableRect.size.width - titleSize.width,
//                    y: placeableRect.origin.y + (placeableRect.size.height - titleSize.height) / 2,
//                    width: titleSize.width,
//                    height: titleSize.height
//                )
//                placeableRect.size.width -= titleSize.width - iconPadding
//            case .right:
//                titleLabel.frame = CGRect(
//                    x: placeableRect.origin.x,
//                    y: placeableRect.origin.y + (placeableRect.size.height - titleSize.height) / 2,
//                    width: titleSize.width,
//                    height: titleSize.height
//                )
//                placeableRect.origin.x += titleSize.width + iconPadding
//                placeableRect.size.width -= titleSize.width - iconPadding
//            case .center:
//                titleLabel.frame = CGRect(
//                    x: placeableRect.origin.x + (placeableRect.size.width - titleSize.width) / 2,
//                    y: placeableRect.origin.y + (placeableRect.size.height - titleSize.height) / 2,
//                    width: titleSize.width,
//                    height: titleSize.height
//                )
//            }
//        }
//        if let iconLayer = iconLayer {
//            iconLayer.backgroundColor = UIColor.red.cgColor
//            if placeableRect.size.width < placeableRect.size.height * iconLayerRatio {
//                let newHeight = placeableRect.size.width / iconLayerRatio
//                iconLayer.frame = CGRect(
//                    x: placeableRect.origin.x,
//                    y: placeableRect.origin.y + (placeableRect.size.height - newHeight) / 2,
//                    width: placeableRect.size.width,
//                    height: newHeight
//                )
//            } else {
//                let newWidth = placeableRect.size.height * iconLayerRatio
//                iconLayer.frame = CGRect(
//                    x: placeableRect.origin.x + (placeableRect.size.width - newWidth) / 2,
//                    y: placeableRect.origin.y,
//                    width: newWidth,
//                    height: placeableRect.size.height
//                )
//            }
//        }
    }
}

