//
//  Layouting.swift
//  Kwift
//
//  Created by Joseph Ivie on 10/12/19.
//  Copyright © 2019 Lightning Kite. All rights reserved.
//

import UIKit

public enum Align {
    case start, center, end, fill
}

public struct AlignPair {
    public let horizontal: Align
    public let vertical: Align
    
    static public let center = AlignPair(horizontal: .center, vertical: .center)
    static public let fill = AlignPair(horizontal: .fill, vertical: .fill)
}

public class LinearLayout: UIView {
    
    public struct LayoutParams {
        public let width: CGFloat
        public let height: CGFloat
        public let margin: UIEdgeInsets
        public let gravity: UIGravityBehavior
    }
    
    private var subviewsWithParams: Dictionary<UIView, LayoutParams> = Dictionary()
    
    public func addSubview(_ view: UIView, _ params: LayoutParams) {
        addSubview(view)
        subviewsWithParams[view] = params
    }
    
    override public func sizeThatFits(_ size: CGSize) -> CGSize {
        <#code#>
    }
    override public func layoutSubviews() {
        <#code#>
    }
}
