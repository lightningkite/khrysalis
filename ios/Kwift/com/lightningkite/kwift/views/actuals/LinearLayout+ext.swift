//
//  LinearLayout+ext.swift
//  Kwift
//
//  Created by Joseph Ivie on 10/16/19.
//  Copyright © 2019 Lightning Kite. All rights reserved.
//

import UIKit

public extension LinearLayout {
    func params(
        sizeX: Int32 = 0,
        sizeY: Int32 = 0,
        marginStart: Int32 = 0,
        marginEnd: Int32 = 0,
        marginTop: Int32 = 0,
        marginBottom: Int32 = 0,
        gravity: AlignPair = .center,
        weight: Float = 0
    ) -> LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(
            minimumSize: .zero,
            size: CGSize(width: Int(sizeX), height: Int(sizeY)),
            margin: UIEdgeInsets(
                top: CGFloat(marginTop),
                left: CGFloat(marginStart),
                bottom: CGFloat(marginBottom),
                right: CGFloat(marginEnd)
            ),
            gravity: gravity
        )
    }

}
