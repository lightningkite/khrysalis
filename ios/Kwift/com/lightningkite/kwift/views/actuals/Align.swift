//
//  Align.swift
//  Kwift
//
//  Created by Joseph Ivie on 10/16/19.
//  Copyright © 2019 Lightning Kite. All rights reserved.
//

import Foundation

public enum Align {
    case start, center, end, fill
}

public struct AlignPair {
    public let horizontal: Align
    public let vertical: Align
    
    public init(horizontal: Align, vertical: Align) {
        self.horizontal = horizontal
        self.vertical = vertical
    }
    public init(_ horizontal: Align, _ vertical: Align) {
        self.horizontal = horizontal
        self.vertical = vertical
    }
    
    static public let center = AlignPair(horizontal: .center, vertical: .center)
    static public let fill = AlignPair(horizontal: .fill, vertical: .fill)
    
    static public let topLeft = AlignPair(horizontal: .start, vertical: .start)
    static public let topCenter = AlignPair(horizontal: .center, vertical: .start)
    static public let topFill = AlignPair(horizontal: .fill, vertical: .start)
    static public let topRight = AlignPair(horizontal: .end, vertical: .start)
    static public let centerLeft = AlignPair(horizontal: .start, vertical: .center)
    static public let centerCenter = AlignPair(horizontal: .center, vertical: .center)
    static public let centerFill = AlignPair(horizontal: .fill, vertical: .center)
    static public let centerRight = AlignPair(horizontal: .end, vertical: .center)
    static public let fillLeft = AlignPair(horizontal: .start, vertical: .fill)
    static public let fillCenter = AlignPair(horizontal: .center, vertical: .fill)
    static public let fillFill = AlignPair(horizontal: .fill, vertical: .fill)
    static public let fillRight = AlignPair(horizontal: .end, vertical: .fill)
    static public let bottomLeft = AlignPair(horizontal: .start, vertical: .end)
    static public let bottomCenter = AlignPair(horizontal: .center, vertical: .end)
    static public let bottomFill = AlignPair(horizontal: .fill, vertical: .end)
    static public let bottomRight = AlignPair(horizontal: .end, vertical: .end)
    
}
